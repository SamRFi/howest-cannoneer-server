package be.howest.ti.mars.logic.domain;

import be.howest.ti.mars.logic.exceptions.CannoneerException;

public abstract class Stats {
    private int shotsAmount;
    private int targetsHit;
    protected int score;

    protected Stats() {
        this.shotsAmount = 0;
        this.targetsHit = 0;
        this.score = 0;
    }

    protected Stats(int shotsAmount, int targetsHit, int score) {
        this.shotsAmount = shotsAmount;
        this.targetsHit = targetsHit;
        this.score = score;
    }


    public void setShotsAmount(int shotsAmount) {
        this.shotsAmount = shotsAmount;
    }

    public void setTargetsHit(int targetsHit) {
        this.targetsHit = targetsHit;
    }

    public void setScore(int score) {
        this.score = score;
    }

    protected void addShot() {
        this.shotsAmount += 1;
        updateScore();
    }

    protected void addTargetHit() {
        this.targetsHit += 1;
        if (this.targetsHit > this.shotsAmount) {
            throw new CannoneerException("can't have more target hits than total shots");
        }
        updateScore();
    }

    public int getShotsAmount() {
        return shotsAmount;
    }

    public int getTargetsHit() {
        return targetsHit;
    }

    public int getTargetsMissed() {
        return shotsAmount - targetsHit;
    }

    public int getScore() {
        return score;
    }

    public int getAccuracy() {
        if (this.targetsHit == 0&&this.shotsAmount == 0) {
            return 100;
        }

        return (int) (((double) targetsHit / (double) shotsAmount) * 100);
    }

    protected void updateScore() {}

}
