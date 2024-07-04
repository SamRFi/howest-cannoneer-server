package be.howest.ti.mars.logic.events;

public class DiscardEvent extends IncomingEvent {
    public DiscardEvent(String clientId) {
        super(EventType.DISCARD, clientId);
    }
}
