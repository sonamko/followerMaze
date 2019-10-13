package test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

import main.event.Event;
import main.event.EventEnum;

/**
|666|F|60|50 | 666       | Follow       | 60           | 50         |
|1|U|12|9    | 1         | Unfollow     | 12           | 9          |
|542532|B    | 542532    | Broadcast    | -            | -          |
|43|P|32|56  | 43        | Private Msg  | 32           | 56         |
|634|S|32    | 634       | Status Update| 32           | -          |
 */
public class EventTest {
	Event event;
	
	@After
	public void tearDown() throws Exception {
		event = null;
	}


	@Test
	public void testToStringFollow() {
		event= new Event("666|F|60|50");
		assertEquals("666", event.getSeqNo().toString());
		assertEquals(EventEnum.FOLLOW, event.getEventType());
		assertEquals("60", event.getFromUserId().toString());
		assertEquals("50", event.getToUserId().toString());
		assertEquals("666|F|60|50", event.toString());
	}
	
	@Test
	public void testToStringUnFollow() {
		event= new Event("1|U|12|9");
		assertEquals("1", event.getSeqNo().toString());
		assertEquals(EventEnum.UNFOLLOW, event.getEventType());
		assertEquals("12", event.getFromUserId().toString());
		assertEquals("9", event.getToUserId().toString());
		assertEquals("1|U|12|9", event.toString());
	}
	
	@Test
	public void testToStringBroadcast() {
		event= new Event("542532|B");
		assertEquals("542532", event.getSeqNo().toString());
		assertEquals(EventEnum.BROADCAST, event.getEventType());
		assertEquals("542532|B", event.toString());
	}
	
	@Test
	public void testToStringPrivateMsg() {
		event= new Event("43|P|32|56");
		assertEquals("43", event.getSeqNo().toString());
		assertEquals(EventEnum.PRIVATEMSG, event.getEventType());
		assertEquals("32", event.getFromUserId().toString());
		assertEquals("56", event.getToUserId().toString());
		assertEquals("43|P|32|56", event.toString());
	}
	
	@Test
	public void testToStringStatusUpdate() {
		event= new Event("634|S|32");
		assertEquals("634", event.getSeqNo().toString());
		assertEquals(EventEnum.STATUSUPDATE, event.getEventType());
		assertEquals("32", event.getFromUserId().toString());
		assertEquals("634|S|32", event.toString());
	}
	
}
