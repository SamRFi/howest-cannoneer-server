package be.howest.ti.mars.logic.events;

public class AliveEvent extends IncomingEvent {
    String gameId;

    public AliveEvent(String clientId, String gameId) {
        super(EventType.ALIVE, clientId);
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
}
