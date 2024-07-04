package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.domain.*;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MockMarsController implements MarsController {
    private static final String SOME_QUOTE = "quote";
    
    @Override
    public int createGame(int playerId, Gamemode gamemode, String locaiton) {
        return 0;
    }

    @Override
    public void fireCanon(int gameId, int playerId) {
    }

    @Override
    public Game getGame(int gameId) {
        return null;
    }

    @Override
    public Map<Integer, JsonObject> getActiveGames() {
        return null;
    }

    @Override
    public void addViewer(int gameId, String viewerId) {

    }

    @Override
    public void stopGame(int gameId) {
    }

    @Override
    public Player getPlayer(int playerId) {
        List<Game> history = new ArrayList<>();
        Sniper sniper = new Sniper(2, 1, 200);
        Game game = new Game("aze", "aze", 5, 2, sniper);
        history.add(game);
        return new Player(3, "testPerson", history);
    }

    @Override
    public List<LeaderboardPlayer> getLeaderboard(Gamemode gamemode) {
        List<LeaderboardPlayer> tmp = new ArrayList<>();
        tmp.add(new LeaderboardPlayer(5, "aze", 8000));

        return tmp;
    }

    @Override
    public Map<String, Integer> getLeaderboardsPlayerOverview(int playerId) {
        return null;
    }
}
