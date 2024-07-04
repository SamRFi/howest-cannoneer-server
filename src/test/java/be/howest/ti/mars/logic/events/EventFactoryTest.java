package be.howest.ti.mars.logic.events;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventFactoryTest {
    @Test
    void createIncomingEvent() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("type", "join");
        jsonObject.put("clientId", "2");
        EventFactory eventFactory = new EventFactory();


        assertEquals("2" ,eventFactory.createIncomingEvent(jsonObject).getClientId());

    }
}