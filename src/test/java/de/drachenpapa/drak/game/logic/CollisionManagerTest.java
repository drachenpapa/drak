package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CollisionManagerTest {

    private final PlayerManager playerManager = mock(PlayerManager.class);

    private List<Player> players;
    private List<Point[]> curvePoints;
    private CollisionManager collisionManager;

    @BeforeEach
    public void setUp() {
        players = List.of(
                new Player("Player 1", Color.RED, '1', 'q'),
                new Player("Player 2", Color.GREEN, 'y', 'x'));
        players.get(0).setAlive(true);
        players.get(1).setAlive(true);
        curvePoints = new ArrayList<>();
        collisionManager = new CollisionManager(players, curvePoints, playerManager);
    }

    @Test
    public void testHandleCollisionSetsPlayerDeadAndIncreasesPoints() {
        collisionManager.handleCollision(0);

        assertThat("Player should be set to dead after collision", players.getFirst().isAlive(), is(false));
        verify(playerManager).increasePointsForAlivePlayers();
    }

    @Test
    public void testIsCollisionDetectedWrapsAroundAndDetectsNoCollision() {
        Curve curve = new Curve(700, 10, 0, 1000);

        boolean collision = collisionManager.isCollisionDetected(curve);

        assertThat("No collision should be detected after wrapping around", collision, is(false));
        assertThat("Curve x position should be wrapped to PLAY_AREA_WIDTH - 1", curve.getXPosition(), is(0));
    }

    @Test
    public void testIsCollisionDetectedDetectsSelfCollision() {
        Curve curve = new Curve(10, 10, 0, 1000);
        curve.addPoint(10, 10);
        for (int i = 0; i < 11; i++) {
            curve.addPoint(10, 10);
        }

        boolean collision = collisionManager.isCollisionDetected(curve);

        assertThat("Self-collision should be detected when curve overlaps itself", collision, is(true));
    }

    @Test
    public void testIsCollisionDetectedDetectsOtherCurveCollision() {
        Curve curve = new Curve(20, 20, 0, 1000);
        Point[] otherCurve = new Point[]{new Point(20, 20)};
        curvePoints.add(otherCurve);

        boolean collision = collisionManager.isCollisionDetected(curve);

        assertThat("Collision with another curve should be detected", collision, is(true));
    }
}
