package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.CurvePhysicsSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Curve")
class CurveTest {

    private Curve curve;

    @BeforeEach
    void setUp() {
        curve = new Curve(0, 0, 90, 2000);
    }

    @Nested
    @DisplayName("direction changes")
    class DirectionChanges {

        @Test
        @DisplayName("increases angle by TURN_ANGLE on turnLeft()")
        void increasesAngleOnTurnLeft() {
            curve.turnLeft();
            assertThat(curve.getDirectionAngle()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("decreases angle by TURN_ANGLE on turnRight()")
        void decreasesAngleOnTurnRight() {
            curve.turnRight();
            assertThat(curve.getDirectionAngle()).isEqualTo(80.0);
        }
    }

    @Nested
    @DisplayName("move()")
    class Move {

        @Test
        @DisplayName("updates position, previous position and appends a point")
        void updatesPositionAndAddsPoint() {
            int initialX = curve.getXPosition();
            int initialY = curve.getYPosition();

            curve.move();

            assertThat(curve.getYPosition()).isNotEqualTo(initialY);
            assertThat(curve.getPoints()).hasSize(2);
            assertThat(curve.getPreviousXPosition()).isEqualTo(initialX);
            assertThat(curve.getPreviousYPosition()).isEqualTo(initialY);
        }

        @Test
        @DisplayName("moves exactly STEP_SIZE on angle 0")
        void movesExactlyStepSizeOnAngleZero() {
            Curve horizontal = new Curve(10, 20, 0, 2_000);

            horizontal.move();

            assertThat(horizontal.getXPosition()).isEqualTo(10 + (int) CurvePhysicsSettings.STEP_SIZE);
            assertThat(horizontal.getYPosition()).isEqualTo(20);
        }

        @Test
        @DisplayName("moves exactly STEP_SIZE on angle 90")
        void movesExactlyStepSizeOnAngleNinety() {
            Curve vertical = new Curve(10, 20, 90, 2_000);

            vertical.move();

            assertThat(vertical.getXPosition()).isEqualTo(10);
            assertThat(vertical.getYPosition()).isEqualTo(20 - (int) CurvePhysicsSettings.STEP_SIZE);
        }
    }

    @Nested
    @DisplayName("addPoint()")
    class AddPoint {

        @Test
        @DisplayName("appends point with correct coordinates")
        void appendsPointWithCorrectCoordinates() {
            int initialSize = curve.getPoints().size();

            curve.addPoint(42, 24);

            Point lastPoint = curve.getPoints().getLast();
            assertThat(curve.getPoints()).hasSize(initialSize + 1);
            assertThat(lastPoint).extracting(p -> p.x, p -> p.y).containsExactly(42, 24);
        }

        @Test
        @DisplayName("keeps point list bounded when many points are added")
        void keepsPointListBounded() {
            for (int i = 0; i < 60_000; i++) {
                curve.addPoint(i, i);
            }

            assertThat(curve.getPoints()).hasSizeLessThanOrEqualTo(50_000);
        }

        @Test
        @DisplayName("does not trim when list size is exactly MAX_STORED_POINTS")
        void doesNotTrimAtExactPointLimit() {
            for (int i = 1; i < CurvePhysicsSettings.MAX_STORED_POINTS; i++) {
                curve.addPoint(i, i);
            }

            assertAll(
                () -> assertThat(curve.getPoints())
                    .hasSize(CurvePhysicsSettings.MAX_STORED_POINTS),
                () -> {
                    assert curve.getPoints() != null;
                    assertThat(curve.getPoints().getFirst())
                        .extracting(p -> p.x, p -> p.y)
                        .containsExactly(0, 0);
                }
            );
        }

        @Test
        @DisplayName("trims oldest point when exceeding MAX_STORED_POINTS by one")
        void trimsOldestPointWhenExceedingPointLimitByOne() {
            for (int i = 1; i <= CurvePhysicsSettings.MAX_STORED_POINTS; i++) {
                curve.addPoint(i, i);
            }

            assertAll(
                () -> assertThat(curve.getPoints())
                    .hasSize(CurvePhysicsSettings.MAX_STORED_POINTS),
                () -> {
                    assert curve.getPoints() != null;
                    assertThat(curve.getPoints().getFirst())
                        .extracting(p -> p.x, p -> p.y)
                        .containsExactly(1, 1);
                }
            );
        }
    }

    @Nested
    @DisplayName("gap generation")
    class GapGeneration {

        @Test
        @DisplayName("activates after interval elapsed and deactivates afterwards")
        void activatesAndDeactivates() {
            Curve gapCurve = new Curve(0, 0, 90, 10);
            ReflectionTestUtils.setField(gapCurve, "lastGapTimestamp", System.currentTimeMillis() - 20);

            assertThat(gapCurve.isGeneratingGap())
                .as("Gap should be active after interval elapsed")
                .isTrue();

            int count = 0;
            while (gapCurve.isGeneratingGap() && count < 10) {
                count++;
            }
            assertThat(gapCurve.isGeneratingGap())
                .as("Gap should end after some time")
                .isFalse();
        }

        @Test
        @DisplayName("continueGap decrements positive counter and then deactivates at zero")
        void continueGapTransitionsFromActiveToInactive() {
            ReflectionTestUtils.setField(curve, "isGapActive", true);
            ReflectionTestUtils.setField(curve, "gapLengthCounter", 1);

            Boolean firstTick = ReflectionTestUtils.invokeMethod(curve, "continueGap");
            Boolean secondTick = ReflectionTestUtils.invokeMethod(curve, "continueGap");

            assertAll(
                () -> assertThat(firstTick).isTrue(),
                () -> assertThat(secondTick).isFalse(),
                () -> assertThat(curve.isGapActive()).isFalse()
            );
        }
    }
}
