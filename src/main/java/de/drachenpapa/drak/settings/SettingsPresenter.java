package de.drachenpapa.drak.settings;

import de.drachenpapa.drak.game.logic.GameConfig;
import de.drachenpapa.drak.game.logic.GameEngine;

/**
 * Presenter for settings configuration.
 * Contains testable business logic separate from Swing UI.
 */
public class SettingsPresenter {

    private final SettingsPlayerAssembler playerAssembler;
    private final ControlKeyInputValidator keyValidator;

    public SettingsPresenter() {
        ControlKeyInputValidator sharedValidator = new ControlKeyInputValidator();
        this.playerAssembler = new SettingsPlayerAssembler(sharedValidator);
        this.keyValidator = sharedValidator;
    }

    GameEngine startGame(PlayerSettingsPanel[] playerPanels, int speedLevel) {
        var playerConfigs = playerAssembler.createSelectedPlayerConfigs(playerPanels);
        var gameConfig = new GameConfig(speedLevel, playerConfigs);
        return GameEngine.create(gameConfig);
    }

    public boolean isValidKey(String input) {
        return keyValidator.isValidKeyInput(input);
    }

    public char extractKey(String input) {
        return keyValidator.toControlKey(input);
    }
}
