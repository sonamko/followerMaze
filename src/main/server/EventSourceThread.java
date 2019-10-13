package main.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import main.resources.Properties;

/**
 * Thread Class representing event notifications received from Event Source
 * [useful if there is more than one event source]
 */
public class EventSourceThread extends Thread {

	private static final Logger logger = Logger.getLogger(EventSourceThread.class.getName());
	private Server server;
	private ServerSocket eventSource;
	private BufferedReader serverReader;

	public EventSourceThread(Server socketServer) {
		super();
		this.server = socketServer;
	}

	public void run() {
		try {

			String event;
			while ((event = serverReader.readLine()) != null) {
				server.handleEvent(event);
			}
		} catch (IOException e) {
			logger.severe(e.getMessage());
		} finally {
			try {
				serverReader.close();
			} catch (IOException e) {
				logger.severe(e.getMessage());
			}
		}

	}

	/**
	 * method for opening the port on Server to listen to Event Source for event
	 * notifications
	 * 
	 * @param eventListenerPort
	 *            : port sending the event notification
	 */
	public void openEventSocket(int eventListenerPort) {
		try {

			eventSource = new ServerSocket(Properties.eventSouceListnerPort);
			logger.info("Server waiting for connections..");
			Socket eventSourceSocket = eventSource.accept();
			serverReader = new BufferedReader(new InputStreamReader(eventSourceSocket.getInputStream()));
			logger.info("Server got source connections..");
		} catch (Exception e) {
			logger.severe("Exception when opening socket for Event source " + e.getMessage());
		}
	}

}
