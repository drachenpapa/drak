package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.TestReflectionUtils;
import de.drachenpapa.drak.game.config.CollisionSettings;
import de.drachenpapa.drak.game.config.DisplaySettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CollisionManager")
class CollisionManagerTest {

    private List<Player> players;
    private PlayerManager playerManager;
    private CollisionManager collisionManager;

    @BeforeEach
    void setUp() {
        players = List.of(
            new Player("Player 1", Color.RED, '1', 'q'),
            new Player("Player 2", Color.GREEN, 'y', 'x'));
        players.get(0).revive();
        players.get(1).revive();
        playerManager = new PlayerManager(players);
        collisionManager = new CollisionManager(playerManager);
    }

    @Nested
    @DisplayName("handleCollision()")
    class HandleCollision {

        @Test
        @DisplayName("marks player as dead and increases points for alive players")
        void marksPlayerDeadAndIncreasesPoints() {
            int alivePlayerScoreBefore = players.get(1).getScore();

            collisionManager.handleCollision(0);

            assertAll(
                () -> assertThat(players.getFirst().isAlive()).isFalse(),
                () -> assertThat(players.get(1).getScore()).isEqualTo(alivePlayerScoreBefore + 1)
            );
        }
    }

    @Nested
    @DisplayName("wrapCurvePosition() + isCollisionDetected()")
    class WrapAndDetect {

        @Test
        @DisplayName("wraps x-position and returns false when curve exits play area")
        void wrapsPositionAndReturnsFalseOnBoundaryExit() {
            Curve curve = new Curve(700, 10, 0, 1000);

            collisionManager.wrapCurvePosition(curve);
            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertAll(
                () -> assertThat(collision).isFalse(),
                () -> assertThat(curve.getXPosition()).isZero(),
                () -> assertThat(curve.getPreviousXPosition()).isZero()
            );
        }

        @Test
        @DisplayName("wraps exactly at right and bottom boundaries")
        void wrapsExactlyAtRightAndBottomBoundaries() {
            Curve curve = new Curve(DisplaySettings.PLAY_AREA_WIDTH, DisplaySettings.PLAY_AREA_HEIGHT, 0, 1000);

            collisionManager.wrapCurvePosition(curve);
            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertAll(
                () -> assertThat(collision).isFalse(),
                () -> assertThat(curve.getXPosition()).isZero(),
                () -> assertThat(curve.getPreviousXPosition()).isZero(),
                () -> assertThat(curve.getYPosition()).isZero(),
                () -> assertThat(curve.getPreviousYPosition()).isZero()
            );
        }

        @Test
        @DisplayName("does not wrap at exact zero coordinates")
        void doesNotWrapAtExactZeroCoordinates() {
            Curve curve = new Curve(0, 0, 0, 1000);

            collisionManager.wrapCurvePosition(curve);
            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertAll(
                () -> assertThat(collision).isFalse(),
                () -> assertThat(curve.getXPosition()).isZero(),
                () -> assertThat(curve.getYPosition()).isZero()
            );
        }

        @Test
        @DisplayName("wraps x-position to right edge when moving left out of bounds")
        void wrapsXPositionFromLeftOutOfBounds() {
            Curve curve = new Curve(-1, 10, 0, 1000);

            collisionManager.wrapCurvePosition(curve);
            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertAll(
                () -> assertThat(collision).isFalse(),
                () -> assertThat(curve.getXPosition()).isEqualTo(DisplaySettings.PLAY_AREA_WIDTH - 1),
                () -> assertThat(curve.getPreviousXPosition()).isEqualTo(DisplaySettings.PLAY_AREA_WIDTH - 1)
            );
        }

        @Test
        @DisplayName("wraps y-position from bottom and top out of bounds")
        void wrapsYPositionOnVerticalBoundaryExit() {
            Curve bottomExit = new Curve(10, DisplaySettings.PLAY_AREA_HEIGHT, 0, 1000);
            Curve topExit = new Curve(10, -1, 0, 1000);

            collisionManager.wrapCurvePosition(bottomExit);
            boolean bottomCollision = collisionManager.isCollisionDetected(bottomExit, 0);
            collisionManager.wrapCurvePosition(topExit);
            boolean topCollision = collisionManager.isCollisionDetected(topExit, 0);

            assertAll(
                () -> assertThat(bottomCollision).isFalse(),
                () -> assertThat(bottomExit.getYPosition()).isZero(),
                () -> assertThat(bottomExit.getPreviousYPosition()).isZero(),
                () -> assertThat(topCollision).isFalse(),
                () -> assertThat(topExit.getYPosition()).isEqualTo(DisplaySettings.PLAY_AREA_HEIGHT - 1),
                () -> assertThat(topExit.getPreviousYPosition()).isEqualTo(DisplaySettings.PLAY_AREA_HEIGHT - 1)
            );
        }

        @Test
        @DisplayName("returns true on self-collision (own old trail in grid, not in grace zone)")
        void returnsTrueOnSelfCollision() {
            // Simulate old trail at (10,10) in occupancy grid (not recent)
            playerManager.markTrailOwner(10, 10, 1); // ownerId = playerIndex + 1

            // Curve at (10,10) whose grace zone contains only different positions
            Curve curve = new Curve(10, 10, 0, 1000);
            // Fill grace zone with different positions by simulating ticks at far-away location
            curve.addGraceSegment(new java.awt.Point[]{
                new java.awt.Point(200, 200), new java.awt.Point(201, 200)
            });
            curve.setXPosition(10);
            curve.setYPosition(10);

            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertThat(collision).isTrue();
        }

        @Test
        @DisplayName("ignores overlap in recent grace zone for self-collision detection")
        void ignoresOverlapInRecentGraceZoneForSelfCollisionDetection() {
            // Simulate own trail marked in grid
            playerManager.markTrailOwner(100, 100, 1); // ownerId = player 0

            Curve curve = new Curve(100, 100, 0, 1000);
            // Fill grace zone with (100,100) via addGraceSegment to simulate "just was here"
            curve.addGraceSegment(new java.awt.Point[]{new java.awt.Point(100, 100)});
            curve.setXPosition(100);
            curve.setYPosition(100);

            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            // (100,100) is in grace zone → no self-collision
            assertThat(collision).isFalse();
        }

        @Test
        @DisplayName("returns true when hitting another curve (via occupied grid)")
        void returnsTrueOnOtherCurveCollision() {
            Curve curve = new Curve(20, 20, 0, 1000);
            playerManager.markTrailOwner(20, 20, 2);

            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertThat(collision).isTrue();
        }

        @Test
        @DisplayName("returns false for near miss outside collision radius")
        void returnsFalseForNearMissOutsideCollisionRadius() {
            Curve curve = new Curve(20, 20, 0, 1000);
            playerManager.markTrailOwner(23, 23, 2);

            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertThat(collision).isFalse();
        }

        @Test
        @DisplayName("returns false at exact collision radius boundary")
        void returnsFalseAtExactCollisionRadiusBoundary() {
            Curve curve = new Curve(20, 20, 0, 1000);
            playerManager.markTrailOwner(23, 20, 2);

            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertThat(collision).isFalse();
        }

        @Test
        @DisplayName("marks grid with player owner id after successful movement")
        void marksGridWithPlayerOwnerIdAfterSuccessfulMovement() {
            Curve curve = new Curve(50, 50, 0, 1000);
            curve.setPreviousXPosition(50);
            curve.setPreviousYPosition(50);
            curve.setXPosition(51);
            curve.setYPosition(50);

            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertAll(
                () -> assertThat(collision).isFalse(),
                () -> assertThat(playerManager.getTrailOwner(50, 50)).isEqualTo(1),
                () -> assertThat(playerManager.getTrailOwner(51, 50)).isEqualTo(1)
            );
        }

        @Test
        @DisplayName("detects own old trail collision (not in grace zone)")
        void detectsOwnTrailCollisionWhenNotInGraceZone() {
            playerManager.markTrailOwner(100, 100, 1);
            Curve curve = new Curve(100, 100, 0, 1000);
            // Grace zone filled with different positions
            curve.addGraceSegment(new java.awt.Point[]{new java.awt.Point(200, 200)});
            curve.setXPosition(100);
            curve.setYPosition(100);

            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertThat(collision).isTrue();
        }

        @Test
        @DisplayName("ignores own trail when it is fully within the recent grace zone")
        void ignoresOwnTrailWhenInGraceZone() {
            playerManager.markTrailOwner(100, 100, 1);
            Curve curve = new Curve(100, 100, 0, 1000);
            curve.addGraceSegment(new java.awt.Point[]{new java.awt.Point(100, 100)});

            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertThat(collision).isFalse();
        }
    }

    @Nested
    @DisplayName("internal helpers")
    class InternalHelpers {

        @Test
        @DisplayName("buildSegmentPoints interpolates inclusive endpoints when within step threshold")
        void buildSegmentPointsInterpolatesInclusiveEndpointsWithinThreshold() {
            Point[] points = TestReflectionUtils.invokeMethod(collisionManager, "buildSegmentPoints", 0, 0, 3, 0);

            assertThat(points)
                .hasSize(4)
                .containsExactly(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0));
        }

        @Test
        @DisplayName("buildSegmentPoints returns endpoint only when movement exceeds interpolation threshold")
        void buildSegmentPointsReturnsEndpointOnlyAboveThreshold() {
            int targetX = CollisionSettings.MAX_INTERPOLATION_STEPS_PER_TICK + 1;

            Point[] points = TestReflectionUtils.invokeMethod(collisionManager, "buildSegmentPoints", 0, 0, targetX, 0);

            assertThat(points)
                .hasSize(1)
                .containsExactly(new Point(targetX, 0));
        }

        @Test
        @DisplayName("buildSegmentPoints interpolates when movement equals interpolation threshold")
        void buildSegmentPointsInterpolatesAtExactThreshold() {
            int targetX = CollisionSettings.MAX_INTERPOLATION_STEPS_PER_TICK;

            Point[] points = TestReflectionUtils.invokeMethod(collisionManager, "buildSegmentPoints", 0, 0, targetX, 0);

            assertThat(points)
                .hasSize(targetX + 1)
                .contains(new Point(0, 0), new Point(targetX, 0));
        }

        @Test
        @DisplayName("buildSegmentPoints returns endpoint only for zero movement")
        void buildSegmentPointsReturnsEndpointForZeroMovement() {
            Point[] points = TestReflectionUtils.invokeMethod(collisionManager, "buildSegmentPoints", 10, 20, 10, 20);

            assertThat(points)
                .hasSize(1)
                .containsExactly(new Point(10, 20));
        }

        @Test
        @DisplayName("isOutOfBounds is true at upper boundaries and false at last in-bounds coordinate")
        void isOutOfBoundsHandlesUpperBoundaryExactly() {
            assertAll(
                () -> assertThat(DisplaySettings.isOutOfBounds(DisplaySettings.PLAY_AREA_WIDTH, 10)).isTrue(),
                () -> assertThat(DisplaySettings.isOutOfBounds(10, DisplaySettings.PLAY_AREA_HEIGHT)).isTrue(),
                () -> assertThat(DisplaySettings.isOutOfBounds(DisplaySettings.PLAY_AREA_WIDTH - 1, DisplaySettings.PLAY_AREA_HEIGHT - 1)).isFalse()
            );
        }

        @Test
        @DisplayName("isPointCollision respects radial distance inside and at radius boundary")
        void isPointCollisionRespectsDistanceBoundary() {
            Boolean inside = TestReflectionUtils.invokeMethod(collisionManager, "isPointCollision", 0, 0, 2, 2);
            Boolean atBoundary = TestReflectionUtils.invokeMethod(collisionManager, "isPointCollision", 0, 0, CollisionSettings.CURVE_WIDTH, 0);

            assertAll(
                () -> assertThat(inside).isTrue(),
                () -> assertThat(atBoundary).isFalse()
            );
        }

        @Test
        @DisplayName("markPointsInGrid skips out-of-bounds points")
        void markPointsInGridSkipsOutOfBoundsPoints() {
            Point[] points = {new Point(-1, 0), new Point(10, 10)};

            TestReflectionUtils.invokeMethod(collisionManager, "markPointsInGrid", points, 0);

            assertThat(playerManager.getTrailOwner(10, 10)).isEqualTo(1);
        }

        @Test
        @DisplayName("isTrailCollision detects collision on capped maxX boundary")
        void isTrailCollisionDetectsCollisionOnCappedMaxXBoundary() {
            int x = DisplaySettings.PLAY_AREA_WIDTH - 2;
            int y = 20;
            playerManager.markTrailOwner(DisplaySettings.PLAY_AREA_WIDTH - 1, y, 2);
            Curve curve = new Curve(x, y, 0, 1000);

            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertThat(collision).isTrue();
        }

        @Test
        @DisplayName("isTrailCollision detects collision on capped maxY boundary")
        void isTrailCollisionDetectsCollisionOnCappedMaxYBoundary() {
            int x = 20;
            int y = DisplaySettings.PLAY_AREA_HEIGHT - 2;
            playerManager.markTrailOwner(x, DisplaySettings.PLAY_AREA_HEIGHT - 1, 2);
            Curve curve = new Curve(x, y, 0, 1000);

            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertThat(collision).isTrue();
        }
    }
}
