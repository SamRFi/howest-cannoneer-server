package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.domain.*;
import io.vertx.core.json.JsonObject;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface MarsController {

    int createGame(int playerId, Gamemode gamemode, String location);
    void fireCanon(int gameId, int playerId);

    Game getGame(int gameId);

    Map<Integer, JsonObject> getActiveGames();
    void addViewer(int gameId, String viewerId);
    void stopGame(int gameId) throws ParseException;

    Player getPlayer(int playerId);
    List<LeaderboardPlayer> getLeaderboard(Gamemode gamemode);
    Map<String, Integer> getLeaderboardsPlayerOverview(int playerId);
}
