package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.domain.Gamemode;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryGamesRepositoryTest {
    @Test
    void createGame() {
        InMemoryGamesRepository repo = new InMemoryGamesRepository();

        repo.createGame(3, Gamemode.CLASSIC, "randomLocation");
        repo.createGame(4, Gamemode.CLASSIC, "randomLocation");
        repo.createGame(5, Gamemode.CLASSIC, "randomLocation");

        assertEquals(3, repo.getActiveGames().size());
    }

    @Test
    void dontCreateNewGameWhenPlayerHasGameRunning() {
        InMemoryGamesRepository repo = new InMemoryGamesRepository();

        repo.createGame(3, Gamemode.CLASSIC, "randomLocation");
        repo.createGame(3, Gamemode.CLASSIC, "randomLocation");
        repo.createGame(3, Gamemode.CLASSIC, "randomLocation");

        assertEquals(1, repo.getActiveGames().size());
    }

    @Test
    void returnExistingGameIdWhenPlayerHasGameRunning() {
        InMemoryGamesRepository repo = new InMemoryGamesRepository();

        int firstGameId = repo.createGame(3, Gamemode.CLASSIC, "randomLocation");
        int secondGameId =repo.createGame(3, Gamemode.CLASSIC, "randomLocation");

        assertEquals(firstGameId, secondGameId);
    }

    @Test
    void errorWhenGettingNonExistingGame() {
        InMemoryGamesRepository repo = new InMemoryGamesRepository();

        assertThrows(NoSuchElementException.class, () -> repo.getGame(281293881));
    }
}