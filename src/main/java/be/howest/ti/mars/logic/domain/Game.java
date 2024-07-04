package be.howest.ti.mars.logic.domain;

import be.howest.ti.mars.logic.exceptions.CannoneerException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Game {
    private static final int MAX_ACCURACY = 101;
    private static final int ACCURACY = 50;

    private final String location;
    private final String startTime;

    private final int playerId;
    private final Stats stats;
    private int durationInMinutes;
    @JsonIgnore
    private Map<String, Boolean> viewers;

    public Game(String location, Gamemode gamemode, int playerId) {
        this.location = location;
        this.startTime = convertDateToString(new Date());
        if (gamemode.equals(Gamemode.CLASSIC)) {
            this.stats = new Classic();
        } else {
            this.stats = new Sniper();
        }
        this.durationInMinutes = 0;
        this.playerId = playerId;
        this.viewers = new HashMap<>();
    }

    public Game(String location, String startTime, int durationInMinutes, int playerId, Stats stats) {
        this.location = location;
        this.startTime = startTime;
        this.stats = stats;
        this.durationInMinutes = durationInMinutes;
        this.playerId = playerId;
        this.viewers = new HashMap<>();
    }

    public void addViewer(String viewer) {
        this.viewers.put(viewer, true);
    }

    private void resetAlive() {
        for (Map.Entry<String, Boolean> entry: viewers.entrySet()) {
            entry.setValue(false);
        }
    }

    public void setAlive(String clientId) {
        viewers.put(clientId, true);
    }

    private void removeViewers() {
        for (Map.Entry<String, Boolean> entry: viewers.entrySet()) {
            if (Boolean.FALSE.equals(entry.getValue())) {
                viewers.remove(entry.getKey());
            }
        }
    }

    public List<String> getViewers() {
        return new ArrayList<>(viewers.keySet());
    }

    public int getViewCount() {
        return viewers.size();
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getGameMode() {
        return stats.getClass().getSimpleName().toLowerCase();
    }

    public void setDuration(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public String getLocation() {
        return location;
    }

    private String convertDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat();
        return dateFormat.format(date);
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public String getStartTime() {
        return startTime;
    }

    public Stats getStats() {
        return stats;
    }

    public void fire() {
        removeViewers();
        resetAlive();

        this.stats.addShot();

        if (new SecureRandom().nextInt(MAX_ACCURACY) < ACCURACY) {
            this.stats.addTargetHit();
        }
    }

    public Game endGame() {
        Date end = new Date();
        Date start = null;

        try {
            start = new SimpleDateFormat().parse(this.startTime);
        } catch (ParseException e) {
            throw new CannoneerException("ParseException");
        }

        long timespan = end.getTime() - start.getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timespan);
        this.durationInMinutes = (int) minutes;

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game that = (Game) o;
        return location.equals(that.location) && startTime.equals(that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, startTime);
    }
}
