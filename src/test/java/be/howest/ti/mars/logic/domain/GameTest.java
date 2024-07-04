package be.howest.ti.mars.logic.domain;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    @Test
    void initializeGame() {
        Game game = new Game("b-sector", Gamemode.SNIPER, 999);
        Game gameClassic = new Game("b-sector", Gamemode.CLASSIC, 999);

        assertEquals("Sniper", game.getStats().getClass().getSimpleName());
        assertEquals("Classic", gameClassic.getStats().getClass().getSimpleName());
    }

    @Test
    void getGameMode() {
        Game game = new Game("b-sector", Gamemode.SNIPER, 999);
        assertEquals("sniper", game.getGameMode());
    }

    @Test
    void getSniperScore() {
        Game game = new Game("b-sector", Gamemode.SNIPER, 999);

        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();
        game.getStats().addShot();

        assertEquals(1, game.getStats().getScore());
    }

    @Test
    void getClassicScore() {
        Game game = new Game("b-sector", Gamemode.CLASSIC, 999);

        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();
        game.getStats().addShot();

        assertEquals(3, game.getStats().getScore());
    }

    @Test
    void jsonSerializationDuration() {
        Game game = new Game("b-sector", Gamemode.CLASSIC, 999);

        JsonObject.mapFrom(game);

        assertEquals(0, JsonObject.mapFrom(game).getMap().get("durationInMinutes"));
    }

    @Test
    void jsonSerializationTotal() {
        Game game = new Game("b-sector", Gamemode.CLASSIC, 999);
        //assertEquals("bx", JsonObject.mapFrom(game));
        assertNotNull(JsonObject.mapFrom(game));
    }

    @Test
    void fire() {
        Game game1 = new Game("b-sector", Gamemode.CLASSIC, 999);
        Game game2 = new Game("b-sector", Gamemode.CLASSIC, 998);

        game1.fire();
        game1.fire();

        assertNotEquals(game1.getStats().getShotsAmount(), game2.getStats().getShotsAmount());
    }

    @Test
    void addViewers() {
        Game game1 = new Game("b-sector", Gamemode.CLASSIC, 999);

        game1.addViewer("martian");
        game1.addViewer("human");

        assertEquals(2, game1.getViewers().size());
    }
}