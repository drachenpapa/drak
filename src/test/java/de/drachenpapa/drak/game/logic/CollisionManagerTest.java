package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.CollisionSettings;
import de.drachenpapa.drak.game.config.DisplaySettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;

@DisplayName("CollisionManager")
@ExtendWith(MockitoExtension.class)
class CollisionManagerTest {

    @Mock
    private PlayerManager playerManager;

    private List<Player> players;
    private List<Point[]> curvePoints;
    private CollisionManager collisionManager;

    @BeforeEach
    void setUp() {
        players = List.of(
            new Player("Player 1", Color.RED, '1', 'q'),
            new Player("Player 2", Color.GREEN, 'y', 'x'));
        players.get(0).setAlive(true);
        players.get(1).setAlive(true);
        curvePoints = new ArrayList<>();
        collisionManager = new CollisionManager(players, curvePoints, playerManager);
    }

    @Nested
    @DisplayName("handleCollision()")
    class HandleCollision {

        @Test
        @DisplayName("marks player as dead and increases points for alive players")
        void marksPlayerDeadAndIncreasesPoints() {
            collisionManager.handleCollision(0);

            assertThat(players.getFirst().isAlive()).isFalse();
            verify(playerManager).increasePointsForAlivePlayers();
        }
    }

    @Nested
    @DisplayName("isCollisionDetected()")
    class IsCollisionDetected {

        @Test
        @DisplayName("wraps x-position and returns false when curve exits play area")
        void wrapsPositionAndReturnsFalseOnBoundaryExit() {
            Curve curve = new Curve(700, 10, 0, 1000);

            boolean collision = collisionManager.isCollisionDetected(curve);

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

            boolean collision = collisionManager.isCollisionDetected(curve);

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

            boolean collision = collisionManager.isCollisionDetected(curve);

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

            boolean collision = collisionManager.isCollisionDetected(curve);

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

            boolean bottomCollision = collisionManager.isCollisionDetected(bottomExit);
            boolean topCollision = collisionManager.isCollisionDetected(topExit);

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

            boolean collision = collisionManager.isCollisionDetected(curve);

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

            boolean collision = collisionManager.isCollisionDetected(curve);

            assertThat(collision).isFalse();
        }

        @Test
        @DisplayName("returns true when hitting another curve")
        void returnsTrueOnOtherCurveCollision() {
            Curve curve = new Curve(20, 20, 0, 1000);
            curvePoints.add(new Point[]{new Point(20, 20)});

            boolean collision = collisionManager.isCollisionDetected(curve);

            assertThat(collision).isTrue();
        }

        @Test
        @DisplayName("returns false for near miss outside collision radius")
        void returnsFalseForNearMissOutsideCollisionRadius() {
            Curve curve = new Curve(20, 20, 0, 1000);
            curvePoints.add(new Point[]{new Point(23, 23)});

            boolean collision = collisionManager.isCollisionDetected(curve);

            assertThat(collision).isFalse();
        }

        @Test
        @DisplayName("returns false at exact collision radius boundary")
        void returnsFalseAtExactCollisionRadiusBoundary() {
            Curve curve = new Curve(20, 20, 0, 1000);
            curvePoints.add(new Point[]{new Point(23, 20)});

            boolean collision = collisionManager.isCollisionDetected(curve);

            assertThat(collision).isFalse();
        }
    }

    @Nested
    @DisplayName("internal helpers")
    class InternalHelpers {

        @Test
        @DisplayName("buildSegmentPoints interpolates inclusive endpoints when within step threshold")
        void buildSegmentPointsInterpolatesInclusiveEndpointsWithinThreshold() {
            Point[] points = ReflectionTestUtils.invokeMethod(collisionManager, "buildSegmentPoints", 0, 0, 3, 0);

            assertThat(points)
                .hasSize(4)
                .containsExactly(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0));
        }

        @Test
        @DisplayName("buildSegmentPoints returns endpoint only when movement exceeds interpolation threshold")
        void buildSegmentPointsReturnsEndpointOnlyAboveThreshold() {
            int targetX = CollisionSettings.MAX_INTERPOLATION_STEPS_PER_TICK + 1;

            Point[] points = ReflectionTestUtils.invokeMethod(collisionManager, "buildSegmentPoints", 0, 0, targetX, 0);

            assertThat(points)
                .hasSize(1)
                .containsExactly(new Point(targetX, 0));
        }

        @Test
        @DisplayName("buildSegmentPoints returns endpoint only for zero movement")
        void buildSegmentPointsReturnsEndpointForZeroMovement() {
            Point[] points = ReflectionTestUtils.invokeMethod(collisionManager, "buildSegmentPoints", 10, 20, 10, 20);

            assertThat(points)
                .hasSize(1)
                .containsExactly(new Point(10, 20));
        }

        @Test
        @DisplayName("isOutOfBounds is true at upper boundaries and false at last in-bounds coordinate")
        void isOutOfBoundsHandlesUpperBoundaryExactly() {
            Boolean outAtWidth = ReflectionTestUtils.invokeMethod(collisionManager, "isOutOfBounds", DisplaySettings.PLAY_AREA_WIDTH, 10);
            Boolean outAtHeight = ReflectionTestUtils.invokeMethod(collisionManager, "isOutOfBounds", 10, DisplaySettings.PLAY_AREA_HEIGHT);
            Boolean inBounds = ReflectionTestUtils.invokeMethod(collisionManager, "isOutOfBounds", DisplaySettings.PLAY_AREA_WIDTH - 1, DisplaySettings.PLAY_AREA_HEIGHT - 1);

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

            Boolean inside = ReflectionTestUtils.invokeMethod(collisionManager, "isPointCollision", center, 2, 2);
            Boolean atBoundary = ReflectionTestUtils.invokeMethod(collisionManager, "isPointCollision", center, CollisionSettings.CURVE_WIDTH, 0);

            assertAll(
                () -> assertThat(inside).isTrue(),
                () -> assertThat(atBoundary).isFalse()
            );
        }
    }
}
