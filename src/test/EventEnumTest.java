package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import main.event.EventEnum;

public class EventEnumTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testEnumFromString() {
		assertEquals(EventEnum.FOLLOW, EventEnum.enumFromString("F"));
		assertEquals(EventEnum.UNFOLLOW, EventEnum.enumFromString("U"));
		assertEquals(EventEnum.BROADCAST, EventEnum.enumFromString("B"));
		assertEquals(EventEnum.PRIVATEMSG, EventEnum.enumFromString("P"));
		assertEquals(EventEnum.STATUSUPDATE, EventEnum.enumFromString("S"));
		assertEquals(null, EventEnum.enumFromString("A"));
	}

}
