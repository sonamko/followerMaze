package test;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import main.event.Event;
import main.server.Client;
import main.server.ClientThread;
import main.server.Server;

public class ServerTest {
	private Server server;
	private Logger logger = Logger.getLogger(ServerTest.class.getName());
	private Method addFollowerMethod;
	private Method removeFollowerMethod;
	private Method addClientToConnectionListMethod;

	public ServerTest() {
		try {
			addFollowerMethod = Server.class.getDeclaredMethod("addFollowers", Event.class);
			addFollowerMethod.setAccessible(true);
			removeFollowerMethod = Server.class.getDeclaredMethod("removeFollowers", Event.class);
			removeFollowerMethod.setAccessible(true);
			addClientToConnectionListMethod=Server.class.getDeclaredMethod("addClientToConnectionList", ClientThread.class);
			addClientToConnectionListMethod.setAccessible(true);
		} catch (NoSuchMethodException ex) {
			Logger.getLogger(ServerTest.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(ServerTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Before
	public void setUp() throws Exception {
		server = new Server();
	}

	@Test
	public void testAddClientToConnectionList() {
		ClientThread cThread = new ClientThread(new Socket(), server);
		cThread.setClientId("100");
		try {
			addClientToConnectionListMethod.invoke(server, cThread);
			assertTrue(server.getClientConnections().containsKey(100L));
		} catch (IllegalAccessException ex) {
			logger.severe("IllegalAccessException in testAddClientToConnectionList method " + ex.getMessage());
		} catch (IllegalArgumentException ex) {
			logger.severe("IllegalArgumentException in testAddClientToConnectionList method " + ex.getMessage());
		} catch (InvocationTargetException ex) {
			logger.severe("InvocationTargetException in testAddClientToConnectionList method " + ex.getMessage());
		}
		
	}

	@Test
	public void testAddFollowerToNonExistentClientShouldCreateTheClientWithNewFollower() {
		try {
			Event event = new Event("4|F|40|68");
			assertFalse(server.getClientFollowers().containsKey(event.getToUserId()));
			addFollowerMethod.invoke(server, event);
			assertTrue(server.getClientFollowers().containsKey(event.getToUserId()));
			assertTrue(server.getClientFollowers().get(event.getToUserId()).getFollowers()
					.contains(event.getFromUserId()));
		} catch (IllegalAccessException ex) {
			logger.severe("Exception in testAddFollowerToNonExistentClientShouldCreateTheClientWithNewFollower method " + ex.getMessage());
		} catch (IllegalArgumentException ex) {
			logger.severe("Exception in testAddFollowerToNonExistentClientShouldCreateTheClientWithNewFollower method " + ex.getMessage());
		} catch (InvocationTargetException ex) {
			logger.severe("Exception in testAddFollowerToNonExistentClientShouldCreateTheClientWithNewFollower method " + ex.getMessage());
		}
	}

	@Test
	public void testAddFollowerToExistigClientShouldAddTheFollower() {
		try {
			Client client = new Client (100, new LinkedList<Long>());
			server.getClientFollowers().put(client.getId(), client);
			Event event = new Event("4|F|68|100");
			addFollowerMethod.invoke(server, event);
			assertTrue(server.getClientFollowers().get(event.getToUserId()).getFollowers()
					.contains(event.getFromUserId()));
		} catch (IllegalAccessException ex) {
			logger.severe("Exception in testAddFollowerToExistigClientShouldAddTheFollower method " + ex.getMessage());
		} catch (IllegalArgumentException ex) {
			logger.severe("Exception in testAddFollowerToExistigClientShouldAddTheFollower method " + ex.getMessage());
		} catch (InvocationTargetException ex) {
			logger.severe("Exception in testAddFollowerToExistigClientShouldAddTheFollower method " + ex.getMessage());
		}
	}
	
	@Test
	public void testRemoveFollowerToExistigClientShouldRemoveTheFollower()
	{
		try {
			Client client = new Client (100, new LinkedList<Long>());
			server.getClientFollowers().put(client.getId(), client);
			Event event = new Event("4|U|68|100");
			removeFollowerMethod.invoke(server, event);
			assertFalse(server.getClientFollowers().get(event.getToUserId()).getFollowers()
					.contains(event.getFromUserId()));
		} catch (IllegalAccessException ex) {
			logger.severe("Exception in testRemoveFollowerToExistigClientShouldRemoveTheFollower method " + ex.getMessage());
		} catch (IllegalArgumentException ex) {
			logger.severe("Exception in testRemoveFollowerToExistigClientShouldRemoveTheFollower method " + ex.getMessage());
		} catch (InvocationTargetException ex) {
			logger.severe("Exception in testRemoveFollowerToExistigClientShouldRemoveTheFollower method " + ex.getMessage());
		}
	}

}
