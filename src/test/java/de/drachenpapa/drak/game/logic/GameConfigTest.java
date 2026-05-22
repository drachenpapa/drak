package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("GameConfig")
class GameConfigTest {

    private static PlayerConfig playerOne() {
        return new PlayerConfig("Player 1", Color.RED, 'a', 'd');
    }

    private static PlayerConfig playerTwo() {
        return new PlayerConfig("Player 2", Color.BLUE, 'j', 'l');
    }

    @Test
    @DisplayName("creates defensive copy of player configs")
    void createsDefensiveCopy() {
        List<PlayerConfig> configs = new ArrayList<>();
        configs.add(playerOne());
        configs.add(playerTwo());

        GameConfig gameConfig = new GameConfig(3, configs);
        configs.clear();

        assertThat(gameConfig.playerConfigs()).hasSize(2);
    }

    @Test
    @DisplayName("createPlayers builds runtime players from configs")
    void createPlayersBuildsRuntimePlayersFromConfigs() {
        PlayerConfig one = new PlayerConfig("One", Color.RED, 'a', 'd');
        PlayerConfig two = new PlayerConfig("Two", Color.BLUE, 'j', 'l');
        GameConfig config = new GameConfig(4, List.of(one, two));

        List<Player> players = config.createPlayers();

        assertAll(
            () -> assertThat(players).hasSize(2),
            () -> assertThat(players)
                .extracting(Player::getPlayerName, Player::getLeftKey, Player::getRightKey)
                .containsExactly(
                    tuple("One", 'a', 'd'),
                    tuple("Two", 'j', 'l')
                )
        );
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
                .hasMessageContaining("At least two players");
        }

        @Test
        @DisplayName("rejects single player list")
        void rejectsSinglePlayerList() {
            List<PlayerConfig> players = List.of(playerOne());
            assertThatThrownBy(() -> new GameConfig(3, players))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least two players");
        }

        @Test
        @DisplayName("rejects duplicate control key shared between players")
        void rejectsDuplicateControlKeyAcrossPlayers() {
            List<PlayerConfig> players = List.of(
                new PlayerConfig("Player 1", Color.RED, 'a', 'd'),
                new PlayerConfig("Player 2", Color.BLUE, 'a', 'f')
            );
            assertThatThrownBy(() -> new GameConfig(3, players))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate");
        }

        @Test
        @DisplayName("rejects duplicate control key where one player's right key equals another's left key")
        void rejectsCrossPlayerKeyConflict() {
            List<PlayerConfig> players = List.of(
                new PlayerConfig("Player 1", Color.RED, 'a', 'd'),
                new PlayerConfig("Player 2", Color.BLUE, 'j', 'd')
            );
            assertThatThrownBy(() -> new GameConfig(3, players))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate");
        }
    }
}
