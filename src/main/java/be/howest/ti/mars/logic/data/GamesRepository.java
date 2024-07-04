package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.domain.Game;
import be.howest.ti.mars.logic.domain.Gamemode;

import java.util.Map;

public interface GamesRepository {
    Game getGame(int gameId);
    Map<Integer, Game> getActiveGames();
    int createGame(int playerId, Gamemode gamemode, String location);
    void saveGame(int gameId);
}
