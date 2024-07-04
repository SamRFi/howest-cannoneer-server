package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.domain.Game;
import be.howest.ti.mars.logic.domain.Gamemode;
import be.howest.ti.mars.logic.exceptions.CannoneerException;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InMemoryGamesRepository implements GamesRepository {

    private static final Logger LOGGER = Logger.getLogger(InMemoryGamesRepository.class.getName());
    private static final String MSG_ONGOING_GAMES_FULL = "Max amount of ongoing games reached";
    private static final String MSG_GAME_UNKNOWN = "No game found with the id: ";

    private static final int GAME_ID_MIN = 0;
    private static final int GAME_ID_MAX = 1024;

    private static final Random random = new SecureRandom();

    private final Map<Integer, Game> ongoingGames = new HashMap<>();

    @Override
    public Game getGame(int gameId) {
        if (!doesGameExist(gameId)) throw new NoSuchElementException(MSG_GAME_UNKNOWN + gameId);

        return ongoingGames.get(gameId);
    }

    @Override
    public Map<Integer, Game> getActiveGames() {
        return ongoingGames;
    }

    @Override
    public int createGame(int playerId, Gamemode gamemode, String location) {
        if (getGameIdIfPlayerIsAlreadyInGame(playerId) != 0) return getGameIdIfPlayerIsAlreadyInGame(playerId);
        int id = createValidId();
        ongoingGames.put(id, new Game(location, gamemode, playerId));
        setTimeout(() -> saveGame(id), 3600000);
        return id;
    }

    private int getGameIdIfPlayerIsAlreadyInGame(int playerId) {
        AtomicInteger result = new AtomicInteger();
        ongoingGames.forEach((k, v) -> {
            if (v.getPlayerId() == playerId) {
                result.set(k);
            }
        });
        return result.get();
    }

    private static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "thread error", e);
                runnable.run();
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void saveGame(int gameId) {
        if (!doesGameExist(gameId)) throw new NoSuchElementException(MSG_GAME_UNKNOWN + gameId);

        Repositories.getH2Repo().saveGame(ongoingGames.get(gameId).endGame());

        ongoingGames.remove(gameId);
    }

    private boolean doesGameExist(int gameId) {
        return ongoingGames.containsKey(gameId);
    }

    private int createValidId() {
        if (ongoingGames.size() == GAME_ID_MAX) throw new CannoneerException(MSG_ONGOING_GAMES_FULL);

        int tmp;

        do {
            tmp = random.nextInt(GAME_ID_MAX - GAME_ID_MIN) + GAME_ID_MIN;
        } while (ongoingGames.containsKey(tmp));

        return tmp;
    }
}
