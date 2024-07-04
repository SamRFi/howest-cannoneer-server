package be.howest.ti.mars.logic.domain;

import java.util.Objects;

public class LeaderboardPlayer {
    private final int id;
    private final String name;
    private final int score;

    public LeaderboardPlayer(int id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeaderboardPlayer that = (LeaderboardPlayer) o;

        if (id != that.id) return false;
        if (score != that.score) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + score;
        return result;
    }
}
