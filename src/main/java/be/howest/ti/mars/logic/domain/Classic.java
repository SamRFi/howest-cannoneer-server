package be.howest.ti.mars.logic.domain;


public class Classic extends Stats {
    public Classic() {
        super();
    }
    public Classic(int shotsAmount, int targetsHit, int score) {
        super(shotsAmount, targetsHit, score);
    }
    @Override
    protected void updateScore() {
        super.score = super.getTargetsHit();
    }
}
