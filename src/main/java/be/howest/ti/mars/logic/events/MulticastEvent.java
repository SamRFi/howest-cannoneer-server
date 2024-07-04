package be.howest.ti.mars.logic.events;

import java.util.List;

public class MulticastEvent extends OutgoingEvent {

    private final List<String> group;

    public MulticastEvent(List<String > group, String message) {
        super(EventType.MULTICAST, message);
        this.group = group;
    }

    public List<String> getGroup() {
        return group;
    }
}
