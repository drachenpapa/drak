package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

            assertThat(new int[]{curve.getXPosition(), curve.getYPosition()})
                    .isNotEqualTo(new int[]{initialX, initialY});
            assertThat(curve.getPoints()).hasSize(2);
            assertThat(curve.getPreviousXPosition()).isEqualTo(initialX);
            assertThat(curve.getPreviousYPosition()).isEqualTo(initialY);
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

            assertThat(curve.getPoints()).hasSize(initialSize + 1);
            assertThat(curve.getPoints().getLast()).satisfies(point -> {
                assertThat(point.x).isEqualTo(42);
                assertThat(point.y).isEqualTo(24);
            });
        }
    }

    @Nested
    @DisplayName("gap generation")
    class GapGeneration {

        @Test
        @DisplayName("activates after interval elapsed and deactivates afterwards")
        void activatesAndDeactivates() throws InterruptedException {
            Curve gapCurve = new Curve(0, 0, 90, 10);
            Thread.sleep(20);

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
    }
}
