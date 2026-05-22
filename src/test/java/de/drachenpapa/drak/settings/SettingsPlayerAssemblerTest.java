package de.drachenpapa.drak.settings;

import de.drachenpapa.drak.game.logic.PlayerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DisplayName("SettingsPlayerAssembler")
class SettingsPlayerAssemblerTest {

    private final SettingsPlayerAssembler assembler = new SettingsPlayerAssembler(new ControlKeyInputValidator());

    @Test
    @DisplayName("creates configs only for selected player rows")
    void createsConfigsOnlyForSelectedRows() {
        PlayerSettingsPanel first = new PlayerSettingsPanel(0, Color.RED, '1', 'q');
        first.setSelected(true);
        first.setPlayerName("Alice");
        first.setSelectedColor(Color.MAGENTA);
        first.setLeftKey("a");
        first.setRightKey("d");

        PlayerSettingsPanel second = new PlayerSettingsPanel(1, Color.GREEN, 'y', 'x');
        second.setSelected(false);
        second.setPlayerName("Bob");

        List<PlayerConfig> configs = assembler.createSelectedPlayerConfigs(new PlayerSettingsPanel[]{first, second});

        assertThat(configs)
            .hasSize(1)
            .extracting(PlayerConfig::playerName, PlayerConfig::color, PlayerConfig::leftKey, PlayerConfig::rightKey)
            .containsExactly(tuple("Alice", Color.MAGENTA, 'a', 'd'));
    }

    @Test
    @DisplayName("returns empty list when no rows are selected")
    void returnsEmptyListWhenNoRowsAreSelected() {
        PlayerSettingsPanel panel = new PlayerSettingsPanel(0, Color.RED, '1', 'q');
        panel.setSelected(false);

        List<PlayerConfig> configs = assembler.createSelectedPlayerConfigs(new PlayerSettingsPanel[]{panel});

        assertThat(configs).isEmpty();
    }
}
