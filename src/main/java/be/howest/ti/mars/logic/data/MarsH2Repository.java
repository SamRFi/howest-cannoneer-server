package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.domain.*;
import be.howest.ti.mars.logic.exceptions.RepositoryException;
import org.h2.tools.Server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
This is only a starter class to use an H2 database.
In this start project there was no need for a Java interface MarsRepository.
Please always use interfaces when needed.

To make this class useful, please complete it with the topics seen in the module OOA & SD
 */

public class MarsH2Repository {
    private static final Logger LOGGER = Logger.getLogger(MarsH2Repository.class.getName());
    private static final String SQL_PLAYER_BY_ID = "select id, name from players where id = ?;";
    private static final String SQL_GAME_HISTORY_BY_PLAYER_ID = "select location, starttime, gamemode, durationinminutes, shotsamount, targetshit, score from games where playerId = ?";
    private static final String SQL_TOP_100_BY_GAMEMODE = "select players.id, players.name, max(games.score) as highscore from games left join players on games.playerId = players.id where gamemode = ? group by players.id, name order by highscore DESC limit 100;";
    private static final String SQL_LEADERBOARD_PLACEMENT =
            "select lb.* " +
            "from (" +
                    "select row_number() over(order by max(games.score) DESC) as rank, players.id, players.name, max(games.score) as highscore " +
                    "from games " +
                    "left join players " +
                    "on games.playerId = players.id " +
                    "where gamemode = ? " +
                    "group by players.id, name " +
                    "order by highscore DESC" +
            ") as lb " +
            "where lb.id = ?;";
    private static final String SQL_INSERT_GAME = "insert into games (`playerId`, `location`, `starttime`, `gamemode`, `durationinminutes`, `shotsamount`, `targetshit`, `score`) values (?, ?, ?, ?, ?, ?, ?, ?);";
    private final Server dbWebConsole;
    private final String username;
    private final String password;
    private final String url;

    public MarsH2Repository(String url, String username, String password, int console) {
        try {
            this.username = username;
            this.password = password;
            this.url = url;
            this.dbWebConsole = Server.createWebServer(
                    "-ifNotExists",
                    "-webPort", String.valueOf(console)).start();
            LOGGER.log(Level.INFO, "Database web console started on port: {0}", console);
            this.generateData();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "DB configuration failed", ex);
            throw new RepositoryException("Could not configure MarsH2repository");
        }
    }

    public Player getPlayer(int playerId) {
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_PLAYER_BY_ID)
        ) {
            stmt.setInt(1, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    List<Game> gameHistory = getGameHistory(id);
                    return new Player(id, name, gameHistory);
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get player.", ex);
            throw new RepositoryException("Could not get player.");
        }
    }

    public List<LeaderboardPlayer> getLeaderboard(Gamemode gamemode) {
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_TOP_100_BY_GAMEMODE)
        ) {
            stmt.setString(1, gamemode.toString().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                ArrayList<LeaderboardPlayer> tmp = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int score = rs.getInt("highscore");

                    tmp.add(new LeaderboardPlayer(id, name, score));
                }
                return tmp;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get leaderboard.", ex);
            throw new RepositoryException("Could not get leaderboard.");
        }
    }

    public Map<String, Integer> getLeaderboardsPlayerOverview(int playerId) {
        Map<String, Integer> tmp = new HashMap<>();

        for (Gamemode gamemode : Gamemode.values()) {
            tmp.put(gamemode.toString().toLowerCase(), getLeaderboardPlacement(playerId, gamemode));
        }

        return tmp;
    }

    private int getLeaderboardPlacement(int playerId, Gamemode gamemode) {
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_LEADERBOARD_PLACEMENT)
        ) {
            stmt.setString(1, gamemode.toString().toLowerCase());
            stmt.setInt(2, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rank");
                } else {
                    return 0;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get rank.", ex);
            throw new RepositoryException("Could not get highscore.");
        }
    }

    private List<Game> getGameHistory(int playerId) {
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GAME_HISTORY_BY_PLAYER_ID)
        ) {
            stmt.setInt(1, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                ArrayList<Game> tmp = new ArrayList<>();
                while (rs.next()) {
                    String location = rs.getString("location");
                    String starttime = rs.getString("starttime");
                    int duration = rs.getInt("durationinminutes");
                    String gamemode = rs.getString("gamemode");

                    int shotsAmount = rs.getInt("shotsamount");
                    int targetsHit = rs.getInt("targetshit");
                    int score = rs.getInt("score");

                    switch (gamemode) {
                        case "sniper":
                            Sniper sniperStats = new Sniper(shotsAmount, targetsHit, score);
                            tmp.add(new Game(location, starttime, duration, playerId, sniperStats));
                            break;
                        case "classic":
                            Classic classicStats = new Classic(shotsAmount, targetsHit, score);
                            tmp.add(new Game(location, starttime, duration, playerId, classicStats));
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid gamemode field");
                    }
                }
                return tmp;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get gameshistory.", ex);
            throw new RepositoryException("Could not get gameshistory.");
        }
    }

    public void saveGame(Game game) {
        try (
                Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_GAME)
        ) {
            stmt.setInt(1, game.getPlayerId());
            stmt.setString(2, game.getLocation());
            stmt.setString(3, game.getStartTime());
            stmt.setString(4, game.getGameMode());
            stmt.setInt(5, game.getDurationInMinutes());
            stmt.setInt(6, game.getStats().getShotsAmount());
            stmt.setInt(7, game.getStats().getTargetsHit());
            stmt.setInt(8, game.getStats().getScore());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Saving game failed, no rows affected.");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to save game.", ex);
            throw new RepositoryException("Could not save game.");
        }
    }

    public void cleanUp() {
        if (dbWebConsole != null && dbWebConsole.isRunning(false))
            dbWebConsole.stop();

        try {
            Files.deleteIfExists(Path.of("./db-07.mv.db"));
            Files.deleteIfExists(Path.of("./db-07.trace.db"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Database cleanup failed.", e);
            throw new RepositoryException("Database cleanup failed.");
        }
    }

    public void generateData() {
        cleanUp();

        try {
            executeScript("db-create.sql");
            executeScript("db-populate.sql");
        } catch (IOException | SQLException ex) {
            LOGGER.log(Level.SEVERE, "Execution of database scripts failed.", ex);
        }
    }

    private void executeScript(String fileName) throws IOException, SQLException {
        String createDbSql = readFile(fileName);
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(createDbSql);
        ) {
            stmt.executeUpdate();
        }
    }

    private String readFile(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null)
            throw new RepositoryException("Could not read file: " + fileName);

        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
