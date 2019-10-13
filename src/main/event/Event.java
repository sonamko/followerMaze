package main.event;

import main.resources.Properties;

/**
 * Simple Event POJO class to represent events received from Event Source
 */
public class Event implements Comparable<Event> {

	private Long seqNo;
	private Long fromUserId;
	private Long toUserId;
	private EventEnum eventType;
	private final String eventSeparator = "|";

	public Event(String event) {
		String[] eventList = event.split(Properties.eventSeparator);
		if (eventList.length > 0) {
			this.seqNo = Long.parseLong(eventList[0]);
		}
		if (eventList.length > 1) {
			this.eventType = EventEnum.enumFromString(eventList[1]);
		}
		if (eventList.length > 2) {
			this.fromUserId = Long.parseLong(eventList[2]);
		}
		if (eventList.length > 3) {
			this.toUserId = Long.parseLong(eventList[3]);
		}
	}

	public Long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public Long getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(Long fromUserId) {
		this.fromUserId = fromUserId;
	}

	public Long getToUserId() {
		return toUserId;
	}

	public void setToUserId(Long toUserId) {
		this.toUserId = toUserId;
	}

	public EventEnum getEventType() {
		return eventType;
	}

	public void setEventType(EventEnum eventType) {
		this.eventType = eventType;
	}

	@Override
	public String toString() {
		return seqNo + eventSeparator + eventType.getName() + (null != fromUserId ? (eventSeparator + fromUserId) : "")
				+ (null != toUserId ? (eventSeparator + toUserId) : "");
	}

	@Override
	public int compareTo(Event e) {
		return this.seqNo.compareTo(e.getSeqNo());
	}

}
