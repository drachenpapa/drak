package de.drachenpapa.drak.settings;

import de.drachenpapa.drak.game.logic.PlayerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SettingsPlayerAssembler")
class SettingsPlayerAssemblerTest {

    private final SettingsPlayerAssembler assembler = new SettingsPlayerAssembler();

    @Test
    @DisplayName("creates configs only for selected player rows")
    void createsConfigsOnlyForSelectedRows() {
        PlayerSettingsPanel first = new PlayerSettingsPanel(0, Color.RED, '1', 'q');
        first.checkBox.setSelected(true);
        first.nameField.setText("Alice");
        first.colorButton.setBackground(Color.MAGENTA);
        first.leftKeyButton.setText("a");
        first.rightKeyButton.setText("d");

        PlayerSettingsPanel second = new PlayerSettingsPanel(1, Color.GREEN, 'y', 'x');
        second.checkBox.setSelected(false);
        second.nameField.setText("Bob");

        List<PlayerConfig> configs = assembler.createSelectedPlayerConfigs(new PlayerSettingsPanel[]{first, second});

        assertThat(configs).hasSize(1);
        PlayerConfig config = configs.getFirst();
        assertThat(config.playerName()).isEqualTo("Alice");
        assertThat(config.color()).isEqualTo(Color.MAGENTA);
        assertThat(config.leftKey()).isEqualTo('a');
        assertThat(config.rightKey()).isEqualTo('d');
    }

    @Test
    @DisplayName("returns empty list when no rows are selected")
    void returnsEmptyListWhenNoRowsAreSelected() {
        PlayerSettingsPanel panel = new PlayerSettingsPanel(0, Color.RED, '1', 'q');
        panel.checkBox.setSelected(false);

        List<PlayerConfig> configs = assembler.createSelectedPlayerConfigs(new PlayerSettingsPanel[]{panel});

        assertThat(configs).isEmpty();
    }
}
