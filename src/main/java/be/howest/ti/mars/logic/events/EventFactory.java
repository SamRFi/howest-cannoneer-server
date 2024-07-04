package be.howest.ti.mars.logic.events;

import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventFactory {
    private static final Logger LOGGER = Logger.getLogger(EventFactory.class.getName());
    private static final EventFactory instance = new EventFactory();
    private static final String GAME_ID_KEY = "gameId";

    public static EventFactory getInstance() {
        return instance;
    }

    public IncomingEvent createIncomingEvent(JsonObject json) {
        EventType eventType = EventType.fromString(json.getString("type"));
        String clientId = json.getString("clientId");
        IncomingEvent event = new DiscardEvent(clientId);
        switch (eventType) {
            case JOIN:
                LOGGER.log(Level.INFO, "Start creating incoming join event");
                JoinEvent joinEvent = new JoinEvent(clientId, json.getString(GAME_ID_KEY));
                event = joinEvent;
                LOGGER.log(Level.INFO, "Created incoming join event");
                break;
            case SHOOT:
                LOGGER.log(Level.INFO, "Start creating incoming shoot event");
                ShootEvent shootEvent = new ShootEvent(clientId, json.getString(GAME_ID_KEY));
                event = shootEvent;
                LOGGER.log(Level.INFO, "Created incoming shoot event");
                break;
            case ALIVE:
                LOGGER.log(Level.INFO, "Start creating incoming alive event");
                AliveEvent aliveEvent = new AliveEvent(clientId, json.getString(GAME_ID_KEY));
                event = aliveEvent;
                LOGGER.log(Level.INFO, "Created incoming alive event");
                break;
            default:
                LOGGER.log(Level.INFO, "Discarded incoming event");
                break;
        }
        return event;
    }

    public BroadcastEvent createBroadcastEvent(String msg) {
        return new BroadcastEvent(msg);
    }

    public UnicastEvent createUnicastEvent(String recipient, String msg) {
        return new UnicastEvent(recipient, msg);
    }

    public MulticastEvent createMulticastEvent(List<String> group, String msg) {
        return new MulticastEvent(group, msg);
    }
}
