package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.domain.Gamemode;
import be.howest.ti.mars.logic.exceptions.CannoneerException;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultMarsControllerTest {

    private static final String URL = "jdbc:h2:./db-07";

    @BeforeAll
    void setupTestSuite() {
        Repositories.shutdown();
        JsonObject dbProperties = new JsonObject(Map.of("url", "jdbc:h2:./db-07",
                "username", "",
                "password", "",
                "webconsole.port", 9000));
        Repositories.configure(dbProperties);
    }

    @BeforeEach
    void setupTest() {
        Repositories.getH2Repo().generateData();
    }

    @Test
    void getLiveGames() {
        DefaultMarsController controller = new DefaultMarsController();

        controller.createGame(3, Gamemode.CLASSIC, "Home");
        controller.createGame(4, Gamemode.CLASSIC, "Home");

        assertEquals(2, controller.getActiveGames().size());
    }

    @Test
    void fireCannonAsWrongUser() {
        DefaultMarsController controller = new DefaultMarsController();

        controller.createGame(3, Gamemode.CLASSIC, "Home");
        Set<Integer> gameIdFromCreatedGameInSet = controller.getActiveGames().keySet();
        int gameIdFromCreatedGame = new ArrayList<>(gameIdFromCreatedGameInSet).get(0);

        assertThrows(CannoneerException.class, () -> controller.fireCanon(gameIdFromCreatedGame, 2));
    }

    @Test
    void fireCannon() {
        DefaultMarsController controller = new DefaultMarsController();

        controller.createGame(3, Gamemode.CLASSIC, "Home");
        Set<Integer> gameIdFromCreatedGameInSet = controller.getActiveGames().keySet();
        int gameIdFromCreatedGame = new ArrayList<>(gameIdFromCreatedGameInSet).get(0);
        controller.fireCanon(gameIdFromCreatedGame, 3);

        assertTrue(controller.getGame(gameIdFromCreatedGame).getStats().getShotsAmount() > 0);
    }

    @Test
    void errorWhenPlayerDoesntExist() {
        DefaultMarsController controller = new DefaultMarsController();

        assertThrows(NoSuchElementException.class, () -> controller.getPlayer(8312398));
    }

    @Test
    void getLeaderBoard() {
        DefaultMarsController controller = new DefaultMarsController();

        assertFalse(controller.getLeaderboard(Gamemode.CLASSIC).isEmpty());
    }

}
