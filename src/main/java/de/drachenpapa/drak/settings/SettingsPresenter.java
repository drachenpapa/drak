package de.drachenpapa.drak.settings;

import de.drachenpapa.drak.game.logic.GameConfig;
import de.drachenpapa.drak.game.logic.GameEngine;
import de.drachenpapa.drak.game.logic.PlayerConfig;

import java.util.List;
import java.util.Objects;

/**
 * Presenter for settings configuration.
 * Contains testable business logic separate from Swing UI.
 */
public class SettingsPresenter {

    private final SettingsPlayerAssembler playerAssembler;
    private final ControlKeyInputValidator keyValidator;

    public SettingsPresenter() {
        this(new SettingsPlayerAssembler(), new ControlKeyInputValidator());
    }

    SettingsPresenter(SettingsPlayerAssembler playerAssembler, ControlKeyInputValidator keyValidator) {
        this.playerAssembler = Objects.requireNonNull(playerAssembler);
        this.keyValidator = Objects.requireNonNull(keyValidator);
    }

    /**
     * Starts the game with current settings.
     */
    GameEngine startGame(PlayerSettingsPanel[] playerPanels, int speedLevel) {
        List<PlayerConfig> playerConfigs = playerAssembler.createSelectedPlayerConfigs(playerPanels);
        GameConfig gameConfig = new GameConfig(speedLevel, playerConfigs);
        return new GameEngine(gameConfig);
    }

    /**
     * Validates control key input.
     */
    public boolean isValidKey(String input) {
        return !keyValidator.isInvalidKeyInput(input);
    }

    /**
     * Extracts validated control key character.
     */
    public char extractKey(String input) {
        return keyValidator.toControlKey(input);
    }
}
