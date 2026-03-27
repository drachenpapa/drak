package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.assertj.core.api.Assertions.assertThat;

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
