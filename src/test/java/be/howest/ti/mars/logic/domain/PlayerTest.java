package be.howest.ti.mars.logic.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    @Test
    void addGameToHistory() {
        Player player = new Player(10, "Sam");
        Game game = new Game("b-sector", Gamemode.CLASSIC, 999);
        Game game2 = new Game("b-sector", Gamemode.SNIPER, 999);

        player.addGameToHistory(game);
        player.addGameToHistory(game2);

        assertEquals("sniper", player.getGamesHistory().get(0).getGameMode());
        assertEquals("classic", player.getGamesHistory().get(1).getGameMode());
    }

    @Test
    void classicStatsFromHistory() {
        Player player = new Player(10, "Sam");
        Game game = new Game("b-sector", Gamemode.CLASSIC, 999);
        Game game2 = new Game("b-sector", Gamemode.CLASSIC, 999);

        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();

        game2.getStats().addShot();
        game2.getStats().addShot();
        game2.getStats().addTargetHit();
        game2.getStats().addTargetHit();

        player.addGameToHistory(game2);
        player.addGameToHistory(game);


        assertEquals(4, game.getStats().getScore());
        assertEquals(6, player.getTotalStats().get("totalShots"));
        assertEquals(4, player.getClassicStats().get("highScore"));
        assertEquals(100, player.getClassicStats().get("accuracy"));
    }

    @Test
    void sniperStatsFromHistory() {
        Player player = new Player(10, "Sam");
        Game game = new Game("b-sector", Gamemode.SNIPER, 999);
        Game game2 = new Game("b-sector", Gamemode.SNIPER, 999);

        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();
        game.setDuration(10);

        game2.getStats().addShot();
        game2.getStats().addShot();
        game2.setDuration(30);

        player.addGameToHistory(game);
        player.addGameToHistory(game2);

        assertEquals(0, game2.getStats().getAccuracy());
        assertEquals(4, player.getSniperStats().get("highScore"));
        assertEquals(50, player.getSniperStats().get("accuracy"));
        assertEquals(40, player.getSniperStats().get("minutesPlayed"));

    }

    @Test
    void getTotalStatsFromHistory() {
        Player player = new Player(10, "Sam");
        Game game = new Game("b-sector", Gamemode.SNIPER, 999);
        Game game2 = new Game("b-sector", Gamemode.CLASSIC, 999);

        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addShot();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();
        game.getStats().addTargetHit();

        game2.getStats().addShot();
        game2.getStats().addShot();

        player.addGameToHistory(game);
        player.addGameToHistory(game2);

        assertEquals(50, player.getTotalStats().get("accuracy"));
        assertEquals(6, player.getTotalStats().get("totalShots"));
    }



}