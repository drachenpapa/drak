package de.drachenpapa.drak.settings;

import com.formdev.flatlaf.FlatLightLaf;
import de.drachenpapa.drak.SwingUtils;
import de.drachenpapa.drak.game.logic.GameEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.HeadlessException;
import java.util.Objects;
import java.util.function.Consumer;

import static de.drachenpapa.drak.AppConstants.GAME_TITLE;

/**
 * Main panel for configuring all game and player settings.
 * Uses composition over inheritance – wraps a {@link JFrame} rather than extending it.
 * Each UI action is wired directly as a lambda, removing the need for a central
 * {@code ActionListener} dispatcher.
 */
public class SettingsUI {

    private static final Logger logger = LoggerFactory.getLogger(SettingsUI.class);

    private final JFrame frame;
    private final char[] defaultLeftControlKeys;
    private final char[] defaultRightControlKeys;
    private final Color[] defaultColors;
    private final PlayerSettingsPanel[] playerPanels;
    private final SettingsPresenter presenter;
    private OptionsControlPanel optionsControlPanel;
    private JCheckBox allCheckBox;

    public SettingsUI() {
        this(new SettingsPresenter());
    }

    SettingsUI(SettingsPresenter presenter) {
        this.presenter = Objects.requireNonNull(presenter, "presenter");

        initializeLookAndFeel();
        frame = new JFrame(GAME_TITLE);
        initializeWindowIcon();

        defaultColors = SettingsConstants.Players.defaultColors();
        defaultLeftControlKeys = SettingsConstants.Players.defaultLeftKeys();
        defaultRightControlKeys = SettingsConstants.Players.defaultRightKeys();

        playerPanels = new PlayerSettingsPanel[SettingsConstants.Players.MAX_PLAYERS];

        var mainPanel = buildMainPanel();
        bindPlayerPanelActions();
        loadDefaultPlayerSettings();

        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel buildMainPanel() {
        var mainPanel = new JPanel();
        configureWindow(mainPanel);
        initializePlayerPanels(mainPanel);
        allCheckBox = PlayerSettingsPanel.addAllSelector(mainPanel, playerPanels);
        optionsControlPanel = buildOptionsControlPanel();
        mainPanel.add(Box.createRigidArea(new Dimension(0, SettingsConstants.Ui.SETTINGS_PANEL_SPACER_SMALL)));
        mainPanel.add(optionsControlPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, SettingsConstants.Ui.SETTINGS_PANEL_SPACER_LARGE)));
        return mainPanel;
    }

    private OptionsControlPanel buildOptionsControlPanel() {
        return new OptionsControlPanel(
            SettingsConstants.Speed.DEFAULT,
            SettingsConstants.Speed.MIN,
            SettingsConstants.Speed.MAX,
            e -> startGame(),
            e -> loadDefaultPlayerSettings()
        );
    }

    private void startGame() {
        try {
            var speedLevel = optionsControlPanel.getSpeed();
            GameEngine gameEngine = presenter.startGame(playerPanels, speedLevel);
            gameEngine.startGame();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                frame,
                ex.getMessage(),
                SettingsConstants.Text.INVALID_CONFIGURATION_TITLE,
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void initializeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            logger.warn("Failed to initialize custom look and feel; falling back to default.", ex);
        }
        UIManager.put("Button.focus", SettingsConstants.Ui.TRANSPARENT_BLACK);
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(
            SettingsConstants.Ui.BUTTON_BORDER_VERTICAL,
            SettingsConstants.Ui.BUTTON_BORDER_HORIZONTAL,
            SettingsConstants.Ui.BUTTON_BORDER_VERTICAL,
            SettingsConstants.Ui.BUTTON_BORDER_HORIZONTAL
        ));
    }

    private void initializeWindowIcon() {
        SwingUtils.applyWindowIcon(frame, "/logo.png", logger);
    }

    private void configureWindow(JPanel mainPanel) {
        frame.setResizable(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(SettingsConstants.Ui.PANEL_BACKGROUND);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initializePlayerPanels(JPanel mainPanel) {
        for (int i = 0; i < SettingsConstants.Players.MAX_PLAYERS; i++) {
            playerPanels[i] = new PlayerSettingsPanel(i, defaultColors[i], defaultLeftControlKeys[i], defaultRightControlKeys[i]);
            mainPanel.add(playerPanels[i]);
        }
    }

    private void bindPlayerPanelActions() {
        for (var panel : playerPanels) {
            panel.addCheckBoxListener(e -> updatePanelEnabledState(panel));
            panel.addColorButtonListener(e -> updatePlayerColor(panel));
            panel.addLeftKeyButtonListener(e -> promptAndUpdateControlKey(
                panel.getLeftKey(), panel.getPlayerName(),
                SettingsConstants.Text.ENTER_LEFT_KEY, panel::setLeftKey));
            panel.addRightKeyButtonListener(e -> promptAndUpdateControlKey(
                panel.getRightKey(), panel.getPlayerName(),
                SettingsConstants.Text.ENTER_RIGHT_KEY, panel::setRightKey));
        }
    }

    private void loadDefaultPlayerSettings() {
        for (int i = 0; i < SettingsConstants.Players.MAX_PLAYERS; i++) {
            var panel = playerPanels[i];
            panel.setSelected(false);
            panel.setPlayerName(SettingsConstants.Players.PLAYER_NAME_PREFIX + (i + 1));
            panel.setSelectedColor(defaultColors[i]);
            panel.setLeftKey(String.valueOf(defaultLeftControlKeys[i]));
            panel.setRightKey(String.valueOf(defaultRightControlKeys[i]));

            if (i < SettingsConstants.Players.DEFAULT_ACTIVE_PLAYERS) {
                panel.setCheckBoxEnabled();
                panel.setSelected(true);
                panel.enableRow();
            } else {
                panel.disableRow();
            }
        }
        allCheckBox.setSelected(false);
        allCheckBox.setEnabled(true);
        optionsControlPanel.setSpeedToDefault();
    }

    private void updatePlayerColor(PlayerSettingsPanel panel) {
        try {
            var selectedColor = JColorChooser.showDialog(frame, SettingsConstants.Text.CHOOSE_PLAYER_COLOR, panel.getSelectedColor());
            if (selectedColor != null) {
                panel.setSelectedColor(selectedColor);
            }
        } catch (HeadlessException ex) {
            logger.warn("Failed to open color chooser dialog", ex);
        }
    }

    private void promptAndUpdateControlKey(String currentKey, String playerName,
                                           String promptPrefix, Consumer<String> applyKey) {
        var safePlayerName = playerName.strip();
        var newKey = JOptionPane.showInputDialog(frame, promptPrefix + safePlayerName, currentKey);
        if (newKey == null) {
            return;
        }
        if (!presenter.isValidKey(newKey)) {
            JOptionPane.showMessageDialog(
                frame,
                SettingsConstants.Text.INVALID_CONTROL_KEY_MESSAGE,
                SettingsConstants.Text.INVALID_CONTROL_KEY_TITLE,
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        applyKey.accept(String.valueOf(presenter.extractKey(newKey)));
    }

    private void updatePanelEnabledState(PlayerSettingsPanel panel) {
        if (panel.isSelected()) {
            panel.enableRow();
        } else {
            panel.disableRow();
        }
    }
}
