package main.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Thread class representing each client thread
 */
public class ClientThread extends Thread {

	private Socket clientSocket;
	private Server server;
	private static final Logger logger = Logger.getLogger(ClientThread.class.getName());
	private String clientId;
	private LinkedList<Long> clientFollowers = new LinkedList<>();

	public ClientThread(Socket clientSocket, Server server) {
		super();
		this.clientSocket = clientSocket;
		this.server = server;
	}

	public ClientThread(String clientId, LinkedList<Long> followers) {
		super();
		this.clientId = clientId;
		this.clientFollowers = followers;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public LinkedList<Long> getClientFollowers() {
		return clientFollowers;
	}

	public void setClientFollowers(LinkedList<Long> clientFollowers) {
		this.clientFollowers = clientFollowers;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void run() {
		readClientId();
		server.addClientToConnectionList(this);
	}

	/**
	 * Method for reading client id when client connection is established
	 */
	private void readClientId() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			clientId = br.readLine();
		} catch (IOException e) {
			logger.severe("Exception raised while reading client id " + e.getMessage());
		}
		logger.fine("read Client id on remote port " + clientId);
	}

}
