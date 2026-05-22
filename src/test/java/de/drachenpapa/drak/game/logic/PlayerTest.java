package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.TestReflectionUtils;
import de.drachenpapa.drak.game.config.CurvePhysicsSettings;
import de.drachenpapa.drak.game.config.CurveSpawnSettings;
import de.drachenpapa.drak.game.config.DisplaySettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Player")
class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Player 1", Color.RED, '1', 'q');
    }

    @Nested
    @DisplayName("initial state")
    class InitialState {

        @Test
        @DisplayName("has correct name")
        void hasCorrectName() {
            assertThat(player.getPlayerName()).isEqualTo("Player 1");
        }

        @Test
        @DisplayName("has correct color")
        void hasCorrectColor() {
            assertThat(player.getColor()).isEqualTo(Color.RED);
        }

        @Test
        @DisplayName("has a non-null curve")
        void hasNonNullCurve() {
            assertThat(player.getCurve()).isNotNull();
        }

        @Test
        @DisplayName("uses injected random generator for initial curve")
        void usesInjectedRandomGeneratorForInitialCurve() {
            int safeWidth = DisplaySettings.PLAY_AREA_WIDTH - 2 * CurveSpawnSettings.SPAWN_POSITION_OFFSET;
            int safeHeight = DisplaySettings.PLAY_AREA_HEIGHT - 2 * CurveSpawnSettings.SPAWN_POSITION_OFFSET;
            long gapIntervalStub = CurveSpawnSettings.MIN_INITIAL_GAP_INTERVAL_MS;

            RandomGenerator rng = mock(RandomGenerator.class);
            when(rng.nextInt(safeWidth)).thenReturn(10);
            when(rng.nextInt(safeHeight)).thenReturn(20);
            when(rng.nextDouble(CurvePhysicsSettings.ANGLE_FULL_CIRCLE)).thenReturn(90.0);
            when(rng.nextLong(CurveSpawnSettings.MIN_INITIAL_GAP_INTERVAL_MS,
                CurveSpawnSettings.MAX_INITIAL_GAP_INTERVAL_MS + 1)).thenReturn(gapIntervalStub);

            Player deterministicPlayer = new Player("P", Color.BLUE, 'a', 'd', rng);
            Curve curve = deterministicPlayer.getCurve();

            assertThat(curve.getXPosition()).isEqualTo(10 + CurveSpawnSettings.SPAWN_POSITION_OFFSET);
            assertThat(curve.getYPosition()).isEqualTo(20 + CurveSpawnSettings.SPAWN_POSITION_OFFSET);
            assertThat(curve.getDirectionAngle()).isEqualTo(90.0);
            assertThat(curve.getGapIntervalTicks()).isGreaterThan(0);
            assertThat(TestReflectionUtils.getField(curve, "randomGenerator")).isSameAs(rng);
        }
    }

    @Nested
    @DisplayName("setCurve()")
    class SetCurve {

        @Test
        @DisplayName("updates the active curve")
        void updatesActiveCurve() {
            Curve newCurve = new Curve(200, 300, 90, 5);
            player.setCurve(newCurve);
            assertThat(player.getCurve()).isEqualTo(newCurve);
        }

        @Test
        @DisplayName("resetCurve replaces active curve reference")
        void resetCurveReplacesActiveCurveReference() {
            Curve before = player.getCurve();

            player.resetCurve();

            assertThat(player.getCurve())
                .isNotNull()
                .isNotSameAs(before);
        }
    }

    @Nested
    @DisplayName("key state")
    class KeyState {

        @Test
        @DisplayName("left key starts unpressed and can be set")
        void leftKeyStartsUnpressedAndCanBeSet() {
            assertThat(player.isLeftKeyPressed()).isFalse();
            player.setLeftKeyPressed(true);
            assertThat(player.isLeftKeyPressed()).isTrue();
        }

        @Test
        @DisplayName("right key starts unpressed and can be set")
        void rightKeyStartsUnpressedAndCanBeSet() {
            assertThat(player.isRightKeyPressed()).isFalse();
            player.setRightKeyPressed(true);
            assertThat(player.isRightKeyPressed()).isTrue();
        }
    }

    @Nested
    @DisplayName("increaseScore()")
    class IncreaseScore {

        @Test
        @DisplayName("increments score by one")
        void incrementsScoreByOne() {
            int initialScore = player.getScore();
            player.increaseScore();
            assertThat(player.getScore()).isEqualTo(initialScore + 1);
        }
    }
}
