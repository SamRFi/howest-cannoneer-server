package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.domain.*;
import io.netty.util.internal.StringUtil;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

class MarsH2RepositoryTest {
    private static final String URL = "jdbc:h2:./db-07";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, "", "");
    }

    @BeforeEach
    void setupTestSuite() {
        Repositories.shutdown();
        JsonObject dbProperties = new JsonObject(Map.of("url",URL,
                "username", "",
                "password", "",
                "webconsole.port", 9000 ));
        Repositories.configure(dbProperties);
    }

    @Test
    void getPlayer() {
        Assertions.assertNotNull(Repositories.getH2Repo().getPlayer(2).toString());
    }

    @Test
    void getLeaderboard() {
        Assertions.assertFalse(Repositories.getH2Repo().getLeaderboard(Gamemode.CLASSIC).isEmpty());
    }

    @Test
    void getLeaderboardPlayerOverview() {
        Assertions.assertFalse(Repositories.getH2Repo().getLeaderboardsPlayerOverview(1).isEmpty());
    }

    @Test
    void saveGame() {
        Stats epicStats = new Sniper();
        epicStats.setScore(100000);
        Repositories.getH2Repo().saveGame(new Game("Hometown", "39281", 20, 2, epicStats));

        Assertions.assertEquals(2, Repositories.getH2Repo().getLeaderboard(Gamemode.SNIPER).get(0).getId());
    }
}
