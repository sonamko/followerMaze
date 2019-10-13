package main.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.logging.Logger;

import main.event.Event;
import main.event.EventEnum;
import main.resources.Properties;

/**
 * Main class for receiving events from Event source, Client connection from
 * clients and responsible for redirecting event to respective client in ordered
 * sequence
 */
public class Server {

	private static final Logger logger = Logger.getLogger(Server.class.getName());
	private long eventQueueCounter = 1;
	private Map<Long, ClientThread> clientConnections = new HashMap<Long, ClientThread>();
	private Map<Long, Client> clientFollowers = new HashMap<Long, Client>();
	private PriorityQueue<Event> eventPriorityQueue = new PriorityQueue<Event>();

	public Map<Long, ClientThread> getClientConnections() {
		return clientConnections;
	}

	public void setClientConnections(Map<Long, ClientThread> clientConnections) {
		this.clientConnections = clientConnections;
	}
	
	public Map<Long, Client> getClientFollowers() {
		return clientFollowers;
	}

	public void setClientFollowers(Map<Long, Client> clientFollowers) {
		this.clientFollowers = clientFollowers;
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.startEventConnection();
		try {
			server.startClientConnection();
		} catch (IOException e) {
			logger.severe("Exception while connecting to client " + e.getMessage());
		} finally {
			for (ClientThread cThread : server.getClientConnections().values()) {
				try {
					cThread.getClientSocket().close();
				} catch (IOException e) {
					logger.severe("Exception while closing client connections " + e.getMessage());
				}
			}
		}

	}

	/**
	 * Opens serverSocket for connecting to Event source and spawns a thread
	 * [useful when there are multiple event sources]
	 */
	private void startEventConnection() {
		EventSourceThread eventSourceThread = new EventSourceThread(this);
		logger.info("opening event source connection");
		try {
			eventSourceThread.openEventSocket(Properties.eventSouceListnerPort);
			eventSourceThread.start();
		} catch (Exception e) {
			logger.severe(e.getMessage());
		}
	}

	/**
	 * Opens serverSocket for connecting to Client connections and spawns a
	 * thread for each connecting client
	 */
	private void startClientConnection() throws IOException {
		ServerSocket clientServerSocket = new ServerSocket(Properties.clientListenerPort);
		logger.info("Server waiting for client connections..");
		try {
			while (!clientServerSocket.isClosed()) {
				Socket clientSocket = clientServerSocket.accept();
				ClientThread clientThread = new ClientThread(clientSocket, this);
				clientThread.start();
			}

		} finally {
			clientServerSocket.close();
		}

	}

	/**
	 * Method for registering active client connections and adding into a global
	 * list
	 * 
	 * @param clientThread:
	 *            representing single client connection
	 */
	public void addClientToConnectionList(ClientThread clientThread) {
		logger.fine("Trying to register client " + clientThread.getClientId());
		synchronized (clientConnections) {
			clientConnections.put(Long.parseLong(clientThread.getClientId()), clientThread);
			logger.fine("Registered client " + clientThread.getClientId());
		}
	}

	/**
	 * Method responsible for reading event from Event Source ,ordering into a
	 * Priority Queue and handling it as per Event type
	 * 
	 * @param eventString
	 *            : representing event as received from event source
	 */
	public void handleEvent(String eventString) {
		Event event = new Event(eventString);
		eventPriorityQueue.add(event);
		Event qHead = eventPriorityQueue.peek();
		// picking the events in sequential order
		while (qHead != null && (qHead.getSeqNo() == eventQueueCounter)) {
			EventEnum eventType = qHead.getEventType();
			switch (eventType) {
			case FOLLOW:
				handleFollowEvent(qHead);
				break;
			case BROADCAST:
				handleBroadcastEvent(qHead, clientConnections);
				break;
			case PRIVATEMSG:
				handlePrivateMsgEvent(qHead);
				break;
			case UNFOLLOW:
				handleUnfollowEvent(qHead);
				break;
			case STATUSUPDATE:
				handleStatusUpdateEvent(qHead);
				break;
			default:
				logger.severe("No Matching event type");
				break;

			}
			eventPriorityQueue.remove();
			eventQueueCounter++;
			qHead = eventPriorityQueue.peek();
		}
		return;
	}

	/**
	 * Method for Handling "UNFOLLOW" Events
	 * 
	 * @param event
	 *            : Representing Event type : UNFOLLOW
	 */
	private void handleUnfollowEvent(Event event) {
		removeFollowers(event);
		ClientThread toUserSocket = clientConnections.get(event.getToUserId());
		if (toUserSocket != null) {
			toUserSocket.getClientFollowers().remove(event.getFromUserId());
			logger.info(event.getFromUserId() + "Unfollows " + event.getToUserId());
		}
	}

	/**
	 * Method for Handling "FOLLOW" Events
	 * 
	 * @param event
	 *            : Representing Event type : FOLLOW
	 */
	private void handleFollowEvent(Event event) {
		addFollowers(event);
		ClientThread toUserSocket = clientConnections.get(event.getToUserId());
		if (null != toUserSocket) {
			toUserSocket.getClientFollowers().add(event.getFromUserId());
			logger.info(event.getFromUserId() + "Follows " + event.getToUserId());
			notifyClient(event, toUserSocket.getClientSocket());
		}
	}

	/**
	 * Method for adding Followers into global list of type client id, List of followers
	 * 
	 * @param event
	 *            : Representing Event type : FOLLOW
	 */
	private void addFollowers(Event event) {
		long clientId = event.getToUserId();
		Client client = clientFollowers.get(clientId);
		if (client != null) {
			client.getFollowers().add(event.getFromUserId());
		} else {
			LinkedList<Long> followers = new LinkedList<Long>();
			followers.add(event.getFromUserId());
			Client newClient = new Client(event.getToUserId(), followers);
			clientFollowers.put(event.getToUserId(), newClient);
		}
	}

	/**
	 * Method for removing Followers from global list of type client id, List of
	 * followers
	 * 
	 * @param event
	 *            : Representing Event type : UNFOLLOW
	 */
	private void removeFollowers(Event event) {
		long clientId = event.getToUserId();
		Client client = clientFollowers.get(clientId);
		if (client != null) {
			client.getFollowers().remove(event.getFromUserId());
		}
	}

	/**
	 * Method for redirecting Events to respective clients
	 * 
	 * @param event
	 *            : Representing event as received from event source
	 * @param clientSocket
	 *            : Representing active client connection
	 */
	private void notifyClient(Event event, Socket clientSocket) {
		try {
			if (clientSocket.getOutputStream() != null) {
				PrintWriter pw;
				pw = new PrintWriter(clientSocket.getOutputStream());
				pw.println(event.toString());
				pw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for Handling "PRIVATE MSG" Events
	 * 
	 * @param event
	 *            : Representing Event type : PRIVATE MSG
	 */
	private void handlePrivateMsgEvent(Event privateMsgEvent) {
		ClientThread toUserSocket = clientConnections.get(privateMsgEvent.getToUserId());
		if (null != toUserSocket) {
			logger.info(privateMsgEvent.getFromUserId() + " sends Private Msg to " + privateMsgEvent.getToUserId());
			notifyClient(privateMsgEvent, toUserSocket.getClientSocket());
		}
	}

	/**
	 * Method for Handling "BROADCAST" Events
	 * 
	 * @param event
	 *            : Representing Event type : BROADCAST
	 */
	private void handleBroadcastEvent(Event broadcastEvent, Map<Long, ClientThread> clientConnections) {
		logger.info(broadcastEvent.getSeqNo() + " Broadcasted to all connected clients");
		for (ClientThread clientThread : clientConnections.values()) {
			notifyClient(broadcastEvent, clientThread.getClientSocket());
		}
	}

	/**
	 * Method for Handling "STATUS UPDATE" Events
	 * 
	 * @param event
	 *            : Representing Event type : STATUS UPDATE
	 */
	private void handleStatusUpdateEvent(Event statusUpdateEvent) {
		Client client = clientFollowers.get(statusUpdateEvent.getFromUserId());
		if (null != client) {
			logger.info(client.getId() + " Updates the status");
			for (Long id : client.getFollowers()) {
				ClientThread activeClientThread = clientConnections.get(id);
				if (null != activeClientThread) {
					notifyClient(statusUpdateEvent, activeClientThread.getClientSocket());
				}
			}

		}
	}

}
