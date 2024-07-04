package be.howest.ti.mars.logic.events;

public enum EventType {
    UNICAST("unicast"),
    BROADCAST("broadcast"),
    MULTICAST("multicast"),
    MESSAGE("message"),
    DISCARD("discard"),
    JOIN("join"),
    SHOOT("shoot"),
    GAME_CHANGED("changed"),
    ALIVE("alive"),
    GAME_ENDED("ended");

    private String type;

    EventType(String type) {
        this.type = type;
    }

    public static EventType fromString(String type) {
        for(EventType eventType: EventType.values()){
            if (eventType.type.equals(type)) {
                return eventType;
            }
        }
        return EventType.DISCARD;
    }
}
