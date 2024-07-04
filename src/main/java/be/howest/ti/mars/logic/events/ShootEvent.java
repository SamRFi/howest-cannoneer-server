package be.howest.ti.mars.logic.events;

public class ShootEvent extends IncomingEvent {

    private final String groupId;

    public ShootEvent(String clientId, String group) {
        super(EventType.MESSAGE, clientId);
        this.groupId = group;
    }

    public String getGroupId() {
        return groupId;
    }
}
