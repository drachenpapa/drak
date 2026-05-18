package de.drachenpapa.drak.settings;

import de.drachenpapa.drak.game.logic.PlayerConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds runtime Player instances from selected settings rows.
 */
class SettingsPlayerAssembler {

    List<PlayerConfig> createSelectedPlayerConfigs(PlayerSettingsPanel[] panels) {
        List<PlayerConfig> playerConfigs = new ArrayList<>(panels.length);
        for (PlayerSettingsPanel panel : panels) {
            if (!panel.checkBox.isSelected()) {
                continue;
            }
            char leftKey = panel.leftKeyButton.getText().charAt(0);
            char rightKey = panel.rightKeyButton.getText().charAt(0);
            playerConfigs.add(new PlayerConfig(panel.nameField.getText(), panel.colorButton.getBackground(), leftKey, rightKey));
        }
        return playerConfigs;
    }
}
