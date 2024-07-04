package be.howest.ti.mars.logic.events;

public class JoinEvent extends IncomingEvent {
    String gameId;

    public  JoinEvent(String clientId, String gameId) {
        super(EventType.JOIN, clientId);
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
}
