package be.howest.ti.mars.logic.domain;

public class Sniper extends Stats {
    public Sniper() {
        super();
    }
    public Sniper(int shotsAmount, int targetsHit, int score) {
        super(shotsAmount, targetsHit, score);
    }

    @Override
    protected void updateScore() {
        super.score = super.getTargetsHit() - (super.getShotsAmount() - super.getTargetsHit());
    }
}
