package main.server;

import java.net.Socket;
import java.util.LinkedList;

/**
 * Represents a Client POJO class.
 */
public class Client {

	private long id;
	private Socket clientSocket;
	// List of followers identifies by their id.
	private LinkedList<Long> followers = new LinkedList<Long>();;

	public Client(long id, LinkedList<Long> followers) {
		this.id = id;
		this.followers = followers;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public LinkedList<Long> getFollowers() {
		return followers;
	}

	public void setFollowers(LinkedList<Long> followers) {
		this.followers = followers;
	}
}
