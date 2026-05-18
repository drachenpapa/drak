package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("GameConfig")
class GameConfigTest {

    private static PlayerConfig playerOne() {
        return new PlayerConfig("Player 1", Color.RED, 'a', 'd');
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("rejects speed below range")
        void rejectsSpeedBelowRange() {
            List<PlayerConfig> players = List.of(playerOne());
            assertThatThrownBy(() -> new GameConfig(0, players))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Speed");
        }

        @Test
        @DisplayName("rejects speed above range")
        void rejectsSpeedAboveRange() {
            List<PlayerConfig> players = List.of(playerOne());
            assertThatThrownBy(() -> new GameConfig(6, players))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Speed");
        }

        @Test
        @DisplayName("rejects empty player list")
        void rejectsEmptyPlayerList() {
            List<PlayerConfig> players = List.of();
            assertThatThrownBy(() -> new GameConfig(3, players))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("At least one player");
        }
    }

    @Test
    @DisplayName("creates defensive copy of player configs")
    void createsDefensiveCopy() {
        List<PlayerConfig> configs = new ArrayList<>();
        configs.add(playerOne());

        GameConfig gameConfig = new GameConfig(3, configs);
        configs.clear();

        assertThat(gameConfig.playerConfigs()).hasSize(1);
    }

    @Test
    @DisplayName("createPlayers builds runtime players from configs")
    void createPlayersBuildsRuntimePlayersFromConfigs() {
        PlayerConfig one = new PlayerConfig("One", Color.RED, 'a', 'd');
        PlayerConfig two = new PlayerConfig("Two", Color.BLUE, 'j', 'l');
        GameConfig config = new GameConfig(4, List.of(one, two));

        List<Player> players = config.createPlayers();

        assertThat(players).hasSize(2);
        assertThat(players).extracting(Player::getPlayerName).containsExactly("One", "Two");
        assertThat(players).extracting(Player::getLeftKey).containsExactly('a', 'j');
        assertThat(players).extracting(Player::getRightKey).containsExactly('d', 'l');
    }
}
