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
        players.get(0).setAlive(true);
        players.get(1).setAlive(true);
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
    @DisplayName("isCollisionDetected()")
    class IsCollisionDetected {

        @Test
        @DisplayName("wraps x-position and returns false when curve exits play area")
        void wrapsPositionAndReturnsFalseOnBoundaryExit() {
            Curve curve = new Curve(700, 10, 0, 1000);

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

            boolean bottomCollision = collisionManager.isCollisionDetected(bottomExit, 0);
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
        @DisplayName("returns true on self-collision")
        void returnsTrueOnSelfCollision() {
            Curve curve = new Curve(10, 10, 0, 1000);
            curve.addPoint(10, 10);
            for (int i = 0; i < 11; i++) {
                curve.addPoint(10, 10);
            }

            boolean collision = collisionManager.isCollisionDetected(curve, 0);

            assertThat(collision).isTrue();
        }

        @Test
        @DisplayName("ignores overlap in recent points for self-collision detection")
        void ignoresOverlapInRecentPointsForSelfCollisionDetection() {
            Curve curve = new Curve(0, 0, 0, 1000);
            curve.addPoint(0, 10);
            curve.addPoint(0, 20);
            for (int i = 0; i < 10; i++) {
                curve.addPoint(100, 100);
            }
            curve.setXPosition(100);
            curve.setYPosition(100);

            boolean collision = collisionManager.isCollisionDetected(curve, 0);

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
        @DisplayName("ignores own owner id when checking other-trail collisions")
        void ignoresOwnOwnerIdWhenCheckingOtherTrailCollision() {
            playerManager.markTrailOwner(100, 100, 1);

            Boolean collision = TestReflectionUtils.invokeMethod(collisionManager, "isOtherTrailCollision", 100, 100, 0);

            assertThat(collision).isFalse();
        }

        @Test
        @DisplayName("markPointsInGrid skips out-of-bounds points")
        void markPointsInGridSkipsOutOfBoundsPoints() {
            Point[] points = {new Point(-1, 0), new Point(10, 10)};

            TestReflectionUtils.invokeMethod(collisionManager, "markPointsInGrid", points, 0);

            assertThat(playerManager.getTrailOwner(10, 10)).isEqualTo(1);
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
            Boolean outAtWidth = TestReflectionUtils.invokeMethod(collisionManager, "isOutOfBounds", DisplaySettings.PLAY_AREA_WIDTH, 10);
            Boolean outAtHeight = TestReflectionUtils.invokeMethod(collisionManager, "isOutOfBounds", 10, DisplaySettings.PLAY_AREA_HEIGHT);
            Boolean inBounds = TestReflectionUtils.invokeMethod(collisionManager, "isOutOfBounds", DisplaySettings.PLAY_AREA_WIDTH - 1, DisplaySettings.PLAY_AREA_HEIGHT - 1);

            assertAll(
                () -> assertThat(outAtWidth).isTrue(),
                () -> assertThat(outAtHeight).isTrue(),
                () -> assertThat(inBounds).isFalse()
            );
        }

        @Test
        @DisplayName("isPointCollision respects radial distance inside and at radius boundary")
        void isPointCollisionRespectsDistanceBoundary() {
            Point center = new Point(0, 0);

            Boolean inside = TestReflectionUtils.invokeMethod(collisionManager, "isPointCollision", center, 2, 2);
            Boolean atBoundary = TestReflectionUtils.invokeMethod(collisionManager, "isPointCollision", center, CollisionSettings.CURVE_WIDTH, 0);

            assertAll(
                () -> assertThat(inside).isTrue(),
                () -> assertThat(atBoundary).isFalse()
            );
        }

        @Test
        @DisplayName("isOtherTrailCollision detects collision on capped maxX boundary")
        void isOtherTrailCollisionDetectsCollisionOnCappedMaxXBoundary() {
            int x = DisplaySettings.PLAY_AREA_WIDTH - 2;
            int y = 20;
            playerManager.markTrailOwner(DisplaySettings.PLAY_AREA_WIDTH - 1, y, 2);

            Boolean collision = TestReflectionUtils.invokeMethod(collisionManager, "isOtherTrailCollision", x, y, 0);

            assertThat(collision).isTrue();
        }

        @Test
        @DisplayName("isOtherTrailCollision detects collision on capped maxY boundary")
        void isOtherTrailCollisionDetectsCollisionOnCappedMaxYBoundary() {
            int x = 20;
            int y = DisplaySettings.PLAY_AREA_HEIGHT - 2;
            playerManager.markTrailOwner(x, DisplaySettings.PLAY_AREA_HEIGHT - 1, 2);

            Boolean collision = TestReflectionUtils.invokeMethod(collisionManager, "isOtherTrailCollision", x, y, 0);

            assertThat(collision).isTrue();
        }
    }
}
