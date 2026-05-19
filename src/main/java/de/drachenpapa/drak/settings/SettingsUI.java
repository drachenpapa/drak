package de.drachenpapa.drak.settings;

import com.formdev.flatlaf.FlatLightLaf;
import de.drachenpapa.drak.game.logic.GameEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.drachenpapa.drak.Drak.GAME_TITLE;

/**
 * Main panel for configuring all game and player settings.
 * Combines option and player panels into a single settings UI.
 */
public class SettingsUI extends JFrame implements ActionListener {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(SettingsUI.class.getName());

    private final char[] defaultLeftControlKeys;
    private final char[] defaultRightControlKeys;
    private final Color[] defaultColors;
    private final OptionsControlPanel optionsControlPanel;
    private final PlayerSettingsPanel[] playerPanels;
    private final transient SettingsPresenter presenter;

    public SettingsUI() {
        this(new SettingsPresenter());
    }

    SettingsUI(SettingsPresenter presenter) {
        this.presenter = Objects.requireNonNull(presenter);
        initializeLookAndFeel();
        initializeWindowIcon();
        JPanel mainPanel = new JPanel();

        defaultColors = SettingsConstants.Players.defaultColors();
        defaultLeftControlKeys = SettingsConstants.Players.defaultLeftKeys();
        defaultRightControlKeys = SettingsConstants.Players.defaultRightKeys();

        configureWindow(mainPanel);

        playerPanels = new PlayerSettingsPanel[SettingsConstants.Players.MAX_PLAYERS];
        initializePlayerPanels(mainPanel);

        PlayerSettingsPanel.addAllSelector(mainPanel, playerPanels);

        optionsControlPanel = new OptionsControlPanel(
            SettingsConstants.Speed.DEFAULT,
            SettingsConstants.Speed.MIN,
            SettingsConstants.Speed.MAX,
            this
        );
        mainPanel.add(Box.createRigidArea(new Dimension(0, SettingsConstants.Ui.SETTINGS_PANEL_SPACER_SMALL)));
        mainPanel.add(optionsControlPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, SettingsConstants.Ui.SETTINGS_PANEL_SPACER_LARGE)));

        loadDefaultPlayerSettings();

        bindPlayerPanelActions();

        add(mainPanel);
        setVisible(true);
    }

    private void initializeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Failed to initialize custom look and feel; falling back to default.", ex);
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
        setTitle(GAME_TITLE);
        URL logoUrl = getClass().getResource("/logo.png");
        if (logoUrl != null) {
            setIconImage(Toolkit.getDefaultToolkit().getImage(logoUrl));
        } else {
            logger.log(Level.WARNING, "Logo resource not found [/logo.png]; window icon will not be set");
        }
    }

    private void configureWindow(JPanel mainPanel) {
        setSize(SettingsConstants.Ui.FRAME_WIDTH, SettingsConstants.Ui.FRAME_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(SettingsConstants.Ui.PANEL_BACKGROUND);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initializePlayerPanels(JPanel mainPanel) {
        for (int i = 0; i < SettingsConstants.Players.MAX_PLAYERS; i++) {
            playerPanels[i] = new PlayerSettingsPanel(i, defaultColors[i], defaultLeftControlKeys[i], defaultRightControlKeys[i]);
            mainPanel.add(playerPanels[i]);
        }
    }

    private void bindPlayerPanelActions() {
        for (int i = 0; i < SettingsConstants.Players.MAX_PLAYERS; i++) {
            playerPanels[i].checkBox.addActionListener(this);
            playerPanels[i].colorButton.addActionListener(this);
            playerPanels[i].leftKeyButton.addActionListener(this);
            playerPanels[i].rightKeyButton.addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (SettingsConstants.Commands.START_GAME.equals(e.getActionCommand())) {
            int speedLevel = optionsControlPanel.getSpeed();
            GameEngine gameEngine = presenter.startGame(playerPanels, speedLevel);
            gameEngine.startGame();
        } else if (SettingsConstants.Commands.LOAD_DEFAULTS.equals(e.getActionCommand())) {
            loadDefaultPlayerSettings();
        } else {
            handlePlayerActions(e);
        }
    }

    private void loadDefaultPlayerSettings() {
        for (int i = 0; i < SettingsConstants.Players.MAX_PLAYERS; i++) {
            playerPanels[i].checkBox.setSelected(false);
            playerPanels[i].nameField.setText(SettingsConstants.Players.PLAYER_NAME_PREFIX + (i + 1));
            playerPanels[i].colorButton.setBackground(defaultColors[i]);
            playerPanels[i].leftKeyButton.setText(String.valueOf(defaultLeftControlKeys[i]));
            playerPanels[i].rightKeyButton.setText(String.valueOf(defaultRightControlKeys[i]));
            playerPanels[i].disableRow();
        }

        for (int i = 0; i < SettingsConstants.Players.DEFAULT_ACTIVE_PLAYERS; i++) {
            playerPanels[i].checkBox.setEnabled(true);
            playerPanels[i].checkBox.setSelected(true);
            playerPanels[i].enableRow();
        }

        PlayerSettingsPanel.allCheckBox.setSelected(false);
        PlayerSettingsPanel.allCheckBox.setEnabled(true);

        optionsControlPanel.setSpeedToDefault();
    }

    private void handlePlayerActions(ActionEvent e) {
        for (PlayerSettingsPanel panel : playerPanels) {
            if (e.getSource() == panel.colorButton) {
                updatePlayerColor(panel);
            } else if (e.getSource() == panel.leftKeyButton) {
                promptAndUpdateControlKey(panel, panel.leftKeyButton, SettingsConstants.Text.ENTER_LEFT_KEY);
            } else if (e.getSource() == panel.rightKeyButton) {
                promptAndUpdateControlKey(panel, panel.rightKeyButton, SettingsConstants.Text.ENTER_RIGHT_KEY);
            } else if (e.getSource() == panel.checkBox) {
                updatePanelEnabledState(panel);
            }
        }
    }

    private void updatePlayerColor(PlayerSettingsPanel panel) {
        Color selectedColor = JColorChooser.showDialog(this, SettingsConstants.Text.CHOOSE_PLAYER_COLOR, panel.colorButton.getBackground());
        if (selectedColor != null) {
            panel.colorButton.setBackground(selectedColor);
        }
    }

    private void promptAndUpdateControlKey(PlayerSettingsPanel panel, JButton keyButton, String promptPrefix) {
        String newKey = JOptionPane.showInputDialog(
            this,
            promptPrefix + panel.nameField.getText(),
            keyButton.getText()
        );
        updateControlKeyFromInput(newKey, keyButton);
    }

    private void updatePanelEnabledState(PlayerSettingsPanel panel) {
        if (panel.checkBox.isSelected()) {
            panel.enableRow();
        } else {
            panel.disableRow();
        }
    }

    private void updateControlKeyFromInput(String newKey, JButton keyButton) {
        if (newKey == null) {
            return;
        }
        if (!presenter.isValidKey(newKey)) {
            JOptionPane.showMessageDialog(
                this,
                SettingsConstants.Text.INVALID_CONTROL_KEY_MESSAGE,
                SettingsConstants.Text.INVALID_CONTROL_KEY_TITLE,
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        keyButton.setText(String.valueOf(presenter.extractKey(newKey)));
    }
}
