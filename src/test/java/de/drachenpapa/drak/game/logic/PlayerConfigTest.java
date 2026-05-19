package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PlayerConfig")
class PlayerConfigTest {

    @Test
    @DisplayName("toPlayer maps all config fields")
    void toPlayerMapsAllFields() {
        PlayerConfig config = new PlayerConfig("Player 1", Color.GREEN, 'j', 'k');

        Player player = config.toPlayer();

        assertThat(player.getPlayerName()).isEqualTo("Player 1");
        assertThat(player.getColor()).isEqualTo(Color.GREEN);
        assertThat(player.getLeftKey()).isEqualTo('j');
        assertThat(player.getRightKey()).isEqualTo('k');
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("rejects blank player name")
        void rejectsBlankPlayerName() {
            assertThatThrownBy(() -> new PlayerConfig("  ", Color.RED, 'a', 'd'))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player name");
        }

        @Test
        @DisplayName("rejects null color")
        void rejectsNullColor() {
            assertThatThrownBy(() -> new PlayerConfig("Player 1", null, 'a', 'd'))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Player color");
        }

        @Test
        @DisplayName("rejects whitespace control key")
        void rejectsWhitespaceControlKey() {
            assertThatThrownBy(() -> new PlayerConfig("Player 1", Color.RED, ' ', 'd'))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Control keys");
        }
    }
}
