package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CurveTest {

    private Curve curve;

    @BeforeEach
    public void setUp() {
        curve = new Curve(0, 0, 90, 2000);
    }

    @Test
    public void testTurnLeft() {
        curve.turnLeft();
        assertThat("Direction angle should increase by TURN_ANGLE when turning left", curve.getDirectionAngle(), is(100.0));
    }

    @Test
    public void testTurnRight() {
        curve.turnRight();
        assertThat("Direction angle should decrease by TURN_ANGLE when turning right", curve.getDirectionAngle(), is(80.0));
    }

    @Test
    public void testMove() {
        int initialX = curve.getXPosition();
        int initialY = curve.getYPosition();

        curve.move();

        assertThat("Curve position should change after move", curve.getXPosition() != initialX || curve.getYPosition() != initialY, is(true));
        assertThat("A new point should be added after move", curve.getPoints().size(), is(2));
        assertThat("Previous position should be updated", curve.getPreviousXPosition(), is(initialX));
        assertThat("Previous position should be updated", curve.getPreviousYPosition(), is(initialY));
    }

    @Test
    public void testAddPoint() {
        int initialSize = curve.getPoints().size();

        curve.addPoint(42, 24);

        assertThat("Curve should have one more point after addPoint", curve.getPoints().size(), is(initialSize + 1));
        assertThat("Added point x should match", curve.getPoints().get(initialSize).x, is(42));
        assertThat("Added point y should match", curve.getPoints().get(initialSize).y, is(24));
    }

    @Test
    public void testIsGeneratingGap() throws InterruptedException {
        Curve gapCurve = new Curve(0, 0, 90, 10);
        Thread.sleep(20);
        boolean gapActive = gapCurve.isGeneratingGap();
        assertThat("Gap should be active after interval elapsed", gapActive, is(true));

        int count = 0;
        while (gapCurve.isGeneratingGap() && count < 10) {
            count++;
        }
        assertThat("Gap should end after some time", gapCurve.isGeneratingGap(), is(false));
    }
}
