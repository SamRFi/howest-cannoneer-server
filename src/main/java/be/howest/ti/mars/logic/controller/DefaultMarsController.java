package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.domain.*;
import be.howest.ti.mars.logic.exceptions.CannoneerException;
import io.vertx.core.json.JsonObject;

import java.util.*;

/**
 * DefaultMarsController is the default implementation for the MarsController interface.
 * The controller shouldn't even know that it is used in an API context.
 * This class and all other classes in the logic-package (or future sub-packages)
 * should use 100% plain old Java Objects (POJOs). The use of Json, JsonObject or
 * Strings that contain encoded/json data should be avoided here.
 * Keep libraries and frameworks out of the logic packages as much as possible.
 * Do not be afraid to create your own Java classes if needed.
 */
public class DefaultMarsController implements MarsController {
    private static final String MSG_PLAYER_ID_UNKNOWN = "No player with id: %d";
    private static final String MSG_GAME_ID_UNKNOWN = "No player with id: %d";
    private static final String MSG_LEADERBOARD_UNKNOWN = "No leaderboard found for gamemode: ";
    private static final String MSG_PLAYER_NO_ACCESS_TO_GAME = "You have no access to this game. id:";

    @Override
    public int createGame(int playerId, Gamemode gamemode, String location) {
        return Repositories.getGamesRepo().createGame(playerId, gamemode, location);
    }

    @Override
    public void fireCanon(int gameId, int playerId) {
        Game game = Repositories.getGamesRepo().getGame(gameId);

        if (game.getPlayerId() != playerId) throw new CannoneerException(MSG_PLAYER_NO_ACCESS_TO_GAME + playerId);

        game.fire();
    }

    @Override
    public void stopGame(int gameId) {
        Repositories.getGamesRepo().saveGame(gameId);
    }

    @Override
    public Map<Integer, JsonObject> getActiveGames() {
        Map<Integer, Game> activeGames = Repositories.getGamesRepo().getActiveGames();
        Map<Integer, JsonObject> halfSerializedActiveGames = new HashMap<>();
        for (Map.Entry<Integer, Game> entry : activeGames.entrySet()) {
            Game game = entry.getValue();
            halfSerializedActiveGames.put(entry.getKey(), addPlayerNameToGameAsJsonObject(game));
        }

        return halfSerializedActiveGames;
    }

    private JsonObject addPlayerNameToGameAsJsonObject(Game game) {
        return JsonObject.mapFrom(game).put("playerName", getPlayer(game.getPlayerId()).getName());
    }

    @Override
    public Player getPlayer(int playerId) {
        Player player = Repositories.getH2Repo().getPlayer(playerId);

        if (null == player)
            throw new NoSuchElementException(String.format(MSG_PLAYER_ID_UNKNOWN, playerId));

        return player;
    }

    @Override
    public Game getGame(int gameId) {
        Game game = Repositories.getGamesRepo().getGame(gameId);

        if (null == game)
            throw new NoSuchElementException(String.format(MSG_GAME_ID_UNKNOWN, gameId));

        return game;
    }

    @Override
    public void addViewer(int gameId, String viewerId) {
        Repositories.getGamesRepo().getGame(gameId).addViewer(viewerId);
    }

    @Override
    public List<LeaderboardPlayer> getLeaderboard(Gamemode gamemode) {
        List<LeaderboardPlayer> leaderboard = Repositories.getH2Repo().getLeaderboard(gamemode);

        if (null == leaderboard)
            throw new NoSuchElementException(MSG_LEADERBOARD_UNKNOWN + gamemode.toString().toLowerCase());

        return leaderboard;
    }

    @Override
    public Map<String, Integer> getLeaderboardsPlayerOverview(int playerId) {
        Map<String, Integer> leaderboardsPlayerOverview = Repositories.getH2Repo().getLeaderboardsPlayerOverview(playerId);

        if (null == getPlayer(playerId))
            throw new CannoneerException(MSG_PLAYER_NO_ACCESS_TO_GAME + playerId);
        if (null == leaderboardsPlayerOverview)
            throw new NoSuchElementException(String.format(MSG_PLAYER_ID_UNKNOWN, playerId));

        return leaderboardsPlayerOverview;
    }
}