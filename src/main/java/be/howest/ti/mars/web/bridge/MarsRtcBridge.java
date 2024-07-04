package be.howest.ti.mars.web.bridge;

import be.howest.ti.mars.logic.controller.MarsController;
import be.howest.ti.mars.logic.domain.Game;
import be.howest.ti.mars.logic.events.*;
import be.howest.ti.mars.web.WebServer;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The RTC bridge is one of the class taught topics.
 * If you do not choose the RTC topic you don't have to do anything with this class.
 * Otherwise, you will need to expand this bridge with the websockets topics shown in the other modules.
 *
 * The client-side starter project does not contain any teacher code about the RTC topic.
 * The rtc bridge is already initialized and configured in the WebServer.java.
 * No need to change the WebServer.java
 *
 * The job of the "bridge" is to bridge between websockets events and Java (the controller).
 * Just like in the openapi bridge, keep business logic isolated in the package logic.
 * <p>
 */
public class MarsRtcBridge {
    private static final Logger LOGGER = Logger.getLogger(MarsRtcBridge.class.getName());
    private static final String CHNL_TO_SERVER = "events.to.server";
    private static final String EB_EVENT_TO_CLIENT_UNICAST = "events.to.client.";
    private static final String EB_EVENT_TO_CLIENTS = "events.to.clients";

    private final MarsController controller;
    private SockJSHandler sockJSHandler;
    private EventBus eb;

    public MarsRtcBridge(WebServer parent) {
        this.controller = parent.getController();
    }

    public void start() {
        registerConsumers();
    }

    private void registerConsumers() {
        eb.consumer(CHNL_TO_SERVER, this::handleIncomingMessage);
    }

    private void handleIncomingMessage(Message<JsonObject> msg) {
        LOGGER.log(Level.INFO, "Received message on socket");
        IncomingEvent incoming = EventFactory.getInstance().createIncomingEvent(msg.body());
        String loggerEventTypeMessage = String.format("Determined event type: %s", incoming.getType());
        LOGGER.log(Level.INFO, loggerEventTypeMessage);
        OutgoingEvent result = handleEvent(incoming);
        LOGGER.log(Level.INFO, "Start handling outgoing message");
        if (result != null) {
            handleOutgoingMessage(result);
        }
    }

    public void sendGameChanged(int gameId) {
        Game game = controller.getGame(gameId);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("type", EventType.GAME_CHANGED);
        jsonObject.put("data", game);

        OutgoingEvent message = EventFactory.getInstance().createMulticastEvent(game.getViewers(), jsonObject.encode());

        String loggerSendingMessage = String.format("Sending game update to %d client(s)", game.getViewCount());
        LOGGER.log(Level.INFO, loggerSendingMessage);
        handleOutgoingMessage(message);
    }

    public void sendGameEnded (int gameId) {
        Game game = controller.getGame(gameId);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("type", EventType.GAME_ENDED);
        jsonObject.put("data", gameId);

        OutgoingEvent message = EventFactory.getInstance().createMulticastEvent(game.getViewers(), jsonObject.encode());

        String loggerMessage = String.format("Sending game to %d client(s)", game.getViewCount());
        LOGGER.log(Level.INFO, loggerMessage);
        handleOutgoingMessage(message);
    }

    private void handleOutgoingMessage(OutgoingEvent result) {
        switch (result.getType()) {
            case BROADCAST:
                LOGGER.log(Level.INFO, "Sending broadcast message");
                broadCastMessage((BroadcastEvent) result);
                break;
            case UNICAST:
                LOGGER.log(Level.INFO, "Sending uni-cast message");
                uniCastMessage((UnicastEvent) result);
                break;
            case MULTICAST:
                LOGGER.log(Level.INFO, "Sending multicast message");
                multicastMessage((MulticastEvent) result);
                break;
            default:
                LOGGER.log(Level.INFO, "Failed to handle outgoing message, type of outgoing message not supported");
                break;
        }
    }

    private void uniCastMessage(UnicastEvent e) {
        eb.publish(EB_EVENT_TO_CLIENT_UNICAST + e.getRecipient(), e.getMessage());
        LOGGER.log(Level.INFO, "Sent uni-cast message");
    }

    private void broadCastMessage(BroadcastEvent e) {
        eb.publish(EB_EVENT_TO_CLIENTS, e.getMessage());
        LOGGER.log(Level.INFO, "Sent sending broadcast message");
    }

    private void multicastMessage(MulticastEvent e) {
        for (String clientId : e.getGroup()) {
            eb.publish(EB_EVENT_TO_CLIENT_UNICAST + clientId, e.getMessage());
        }
        LOGGER.log(Level.INFO, "Sent sending multicast message");
    }

    private void createSockJSHandler() {
        final PermittedOptions permittedOptions = new PermittedOptions().setAddressRegex("events\\..+");
        final SockJSBridgeOptions options = new SockJSBridgeOptions()
                .addInboundPermitted(permittedOptions)
                .addOutboundPermitted(permittedOptions);
        sockJSHandler.bridge(options);
    }

    public SockJSHandler getSockJSHandler(Vertx vertx) {
        sockJSHandler = SockJSHandler.create(vertx);
        eb = vertx.eventBus();
        createSockJSHandler();

        return sockJSHandler;
    }

    public OutgoingEvent handleEvent(IncomingEvent e) {
        OutgoingEvent result = null;
        switch (e.getType()) {
            case SHOOT:
                LOGGER.log(Level.INFO, "Start handling incoming shoot event");
                result = handleShootEvent((ShootEvent) e);
                break;
            case JOIN:
                LOGGER.log(Level.INFO, "Start handling incoming join event");
                result = handleJoinEvent((JoinEvent) e);
                break;
            case ALIVE:
                LOGGER.log(Level.INFO, "Start handling incoming alive event");
                handleAliveEvent((AliveEvent) e);
                break;
            default:
                LOGGER.log(Level.WARNING, "Type of incoming event not supported");
                throw new IllegalArgumentException("Type of incoming event not supported");
        }
        return result;
    }

    private OutgoingEvent handleShootEvent(ShootEvent e) {
        Game game = controller.getGame(Integer.parseInt(e.getGroupId()));
        JsonObject response = JsonObject.mapFrom(game);

        LOGGER.log(Level.INFO, "Finished handling incoming shoot event");
        return EventFactory.getInstance().createMulticastEvent(game.getViewers(), response.encode());
    }

    private OutgoingEvent handleJoinEvent(JoinEvent e) {
        int gameId = Integer.parseInt(e.getGameId());
        String clientId = e.getClientId();
        controller.addViewer(gameId, clientId);
        JsonObject response = JsonObject.mapFrom(controller.getGame(gameId));
        response.put("playerName", controller.getPlayer(controller.getGame(gameId).getPlayerId()).getName());

        LOGGER.log(Level.INFO, "Finished handling incoming join event");
        return EventFactory.getInstance().createUnicastEvent(e.getClientId(), response.encode());
    }

    private void handleAliveEvent(AliveEvent e) {
        int gameId = Integer.parseInt(e.getGameId());
        String clientId = e.getClientId();
        Game game = controller.getGame(gameId);

        game.setAlive(clientId);
        LOGGER.log(Level.INFO, "Finished handling incoming alive event");
    }
}
