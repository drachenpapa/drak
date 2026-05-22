package de.drachenpapa.drak.settings;

import de.drachenpapa.drak.game.logic.PlayerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builds runtime Player instances from selected settings rows.
 */
class SettingsPlayerAssembler {

    private final ControlKeyInputValidator keyValidator;


    SettingsPlayerAssembler(ControlKeyInputValidator keyValidator) {
        this.keyValidator = Objects.requireNonNull(keyValidator, "keyValidator");
    }

    List<PlayerConfig> createSelectedPlayerConfigs(PlayerSettingsPanel[] panels) {
        var playerConfigs = new ArrayList<PlayerConfig>(panels.length);
        for (var panel : panels) {
            if (!panel.isSelected()) {
                continue;
            }
            char leftKey = extractSingleKey(panel.getLeftKey(), "left");
            char rightKey = extractSingleKey(panel.getRightKey(), "right");
            playerConfigs.add(new PlayerConfig(panel.getPlayerName(), panel.getSelectedColor(), leftKey, rightKey));
        }
        return playerConfigs;
    }

    private char extractSingleKey(String keyText, String side) {
        if (!keyValidator.isValidKeyInput(keyText)) {
            throw new IllegalStateException("Invalid %s control key in settings UI.".formatted(side));
        }
        return keyValidator.toControlKey(keyText);
    }
}
