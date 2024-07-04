package be.howest.ti.mars.logic.domain;

import java.util.*;

public class Player {
    public static final String MINUTES_PLAYED = "minutesPlayed";
    public static final String HIGH_SCORE = "highScore";
    public static final String ACCURACY = "accuracy";
    public static final String TOTAL_SHOTS = "totalShots";

    public static final String SNIPER = "sniper";
    public static final String CLASSIC = "classic";

    private final int id;
    private final String name;
    private Map<String, Integer> classicStats;
    private Map<String, Integer> sniperStats;
    private final List<Game> gamesHistory;

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        this.classicStats = new HashMap<>();
        this.sniperStats = new HashMap<>();
        this.gamesHistory = new ArrayList<>();
    }

    public Player(int id, String name, List<Game> gamesHistory) {
        this.id = id;
        this.name = name;
        this.classicStats = new HashMap<>();
        this.sniperStats = new HashMap<>();
        this.gamesHistory = gamesHistory;
        updateAllStats();
    }

    public void addGameToHistory(Game game){
        gamesHistory.add(game);
        updateAllStats();
    }
    public String getName() {
        return name;
    }

    public Map<String, Integer> getClassicStats(){
        return classicStats;
    }

    private void updateClassicStats() {
        this.classicStats = calculateStats(CLASSIC);
    }

    private Map<String, Integer> createEmptyStatsMap(){
        Map<String, Integer> result = new HashMap<>();
        result.put(MINUTES_PLAYED, 0);
        result.put(HIGH_SCORE, 0);
        result.put(ACCURACY, 0);
        result.put(TOTAL_SHOTS, 0);
        return result;
    }
    private void updateSniperStats() {
        this.sniperStats = calculateStats(SNIPER);
    }

    private Map<String, Integer> calculateStats(String gamemode) {
        Map<String, Integer> result = createEmptyStatsMap();
        List<Integer> accuracySet = new ArrayList<>();
        for (Game game: gamesHistory) {
            if (game.getGameMode().equals(gamemode)) {
                result.replace(MINUTES_PLAYED, result.get(MINUTES_PLAYED) + game.getDurationInMinutes());
                if (game.getStats().getScore() > result.get(HIGH_SCORE)) {
                    result.replace(HIGH_SCORE, game.getStats().getScore());
                }
                accuracySet.add(game.getStats().getAccuracy());
                result.replace(TOTAL_SHOTS, result.get(TOTAL_SHOTS) + game.getStats().getShotsAmount());
            }
        }
        result.replace(ACCURACY,getAverage(accuracySet));
        return result;
    }

    public Map<String, Integer> getTotalStats() {
        Map<String, Integer> result = new HashMap<>();
        result.put(MINUTES_PLAYED, getClassicStats().get(MINUTES_PLAYED) + getSniperStats().get(MINUTES_PLAYED));
        result.put(ACCURACY, (getClassicStats().get(ACCURACY) + getSniperStats().get(ACCURACY) )/2 );
        result.put(TOTAL_SHOTS, getClassicStats().get(TOTAL_SHOTS) + getSniperStats().get(TOTAL_SHOTS));

        return result;
    }

    private void updateAllStats() {
        updateClassicStats();
        updateSniperStats();
    }

    private int getAverage(List<Integer> intCollection) {
        if (!intCollection.isEmpty()) {
            return intCollection.stream().reduce(0, Integer::sum) / (intCollection.size());
        }
        return 0;
    }

    public Map<String, Integer> getSniperStats() {
        return sniperStats; //summation from gameHistory
    }

    public List<Game> getGamesHistory() {
        List<Game> gameHistoryNewestToOldest = new ArrayList<>(gamesHistory);
        Collections.reverse(gameHistoryNewestToOldest);
        return gameHistoryNewestToOldest;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
