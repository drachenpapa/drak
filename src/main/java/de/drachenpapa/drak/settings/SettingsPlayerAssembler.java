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
            char leftKey = extractSingleKey(panel.leftKeyButton.getText(), "left");
            char rightKey = extractSingleKey(panel.rightKeyButton.getText(), "right");
            playerConfigs.add(new PlayerConfig(panel.nameField.getText(), panel.colorButton.getBackground(), leftKey, rightKey));
        }
        return playerConfigs;
    }

    private char extractSingleKey(String keyText, String side) {
        if (keyText == null || keyText.length() != 1 || Character.isWhitespace(keyText.charAt(0))) {
            throw new IllegalStateException("Invalid %s control key in settings UI.".formatted(side));
        }
        return keyText.charAt(0);
    }
}
