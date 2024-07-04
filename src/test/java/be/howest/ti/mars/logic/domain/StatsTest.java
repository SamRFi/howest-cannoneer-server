package be.howest.ti.mars.logic.domain;

import be.howest.ti.mars.logic.exceptions.CannoneerException;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatsTest {
    @Test
    void getSniperStats() {
        Stats sniperStats = new Sniper();

        assertEquals(0, sniperStats.getScore());
        assertEquals(0, sniperStats.getShotsAmount());
        assertEquals(0, sniperStats.getTargetsMissed());
        assertEquals(0, sniperStats.getTargetsHit());
        assertEquals(100, sniperStats.getAccuracy());
    }

    @Test
    void getNewClassicStats() {
        Stats classic = new Classic();

        assertEquals(0, classic.getScore());
        assertEquals(0, classic.getShotsAmount());
        assertEquals(0, classic.getTargetsMissed());
        assertEquals(0, classic.getTargetsHit());
        assertEquals(100, classic.getAccuracy());
    }

    @Test
    void shotGoesUp() {
        Stats sniperStats = new Sniper();

        sniperStats.addShot();

        assertEquals(1, sniperStats.getShotsAmount());
        sniperStats.addShot();
        assertEquals(2, sniperStats.getShotsAmount());

    }

    @Test
    void addTargetHit() {
        Stats sniperStats = new Sniper();
        sniperStats.addShot();
        sniperStats.addShot();
        sniperStats.addTargetHit();
        sniperStats.addTargetHit();

        assertEquals(2, sniperStats.getTargetsHit());
    }

    @Test
    void getAccuracy() {
        Stats sniperStats = new Sniper();

        sniperStats.addShot();
        sniperStats.addShot();
        sniperStats.addShot();
        sniperStats.addShot();
        sniperStats.addShot();
        sniperStats.addTargetHit();
        sniperStats.addTargetHit();
        sniperStats.addTargetHit();

        assertEquals(60, sniperStats.getAccuracy());
    }

    @Test
    void jsonSerializationScore() {
        Stats classicStats = new Classic();

        classicStats.addShot();
        classicStats.addShot();
        classicStats.addShot();
        classicStats.addShot();
        classicStats.addShot();
        classicStats.addTargetHit();
        classicStats.addTargetHit();
        classicStats.addTargetHit();

        assertEquals(3, JsonObject.mapFrom(classicStats).getMap().get("score"));
    }

    @Test
    void errorWhenTargetsHitIsHigherThanTotalShots() {
        Stats classicStats = new Classic();

        classicStats.addShot();
        classicStats.addShot();
        classicStats.addTargetHit();
        classicStats.addTargetHit();

        assertThrows(CannoneerException.class, classicStats::addTargetHit);

    }
}