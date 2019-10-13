package main.event;

/**
 * Enumeration class for Event types
 */
public enum EventEnum {
	FOLLOW("F"), UNFOLLOW("U"), BROADCAST("B"), PRIVATEMSG("P"), STATUSUPDATE("S");

	private final String name;

	EventEnum(String s) {
		this.name = s;
	}

	public String getName() {
		return name;
	}

	public static EventEnum enumFromString(String text) {
		for (EventEnum e : EventEnum.values()) {
			if (e.name.equalsIgnoreCase(text)) {
				return e;
			}
		}
		return null;
	}

}
