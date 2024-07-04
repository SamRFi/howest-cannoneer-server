package be.howest.ti.mars.logic.events;

public class IncomingEvent extends Event {
    private String clientId;

    public IncomingEvent(EventType type, String clientId) {
        super(type);
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }
}
