package de.drachenpapa.drak.settings;

import com.formdev.flatlaf.FlatLightLaf;
import de.drachenpapa.drak.Drak;
import de.drachenpapa.drak.game.logic.GameEngine;
import de.drachenpapa.drak.game.logic.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static de.drachenpapa.drak.Drak.GAME_TITLE;

/**
 * Main panel for configuring all game and player settings.
 * Combines option and player panels into a single settings UI.
 */
public class SettingsUI extends JFrame {

    private static final int MAX_PLAYERS = 6;
    private final char[] defaultLeftControlKeys;
    private final char[] defaultRightControlKeys;
    private final Color[] defaultColors;
    private final OptionsControlPanel optionsControlPanel;
    private final PlayerSettingsPanel[] playerPanels;
    private final JCheckBox allCheckBox;
    private final List<Player> players = new ArrayList<>(MAX_PLAYERS);

    public SettingsUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {
            // FlatLaf could not be initialized; Swing default look-and-feel is used instead
        }
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(5, 10, 5, 10));

        setTitle(GAME_TITLE);
        Drak.applyAppIcon(this);
        JPanel mainPanel = new JPanel();

        defaultColors = new Color[]{
            new Color(255, 0, 0), new Color(0, 255, 0),
            new Color(0, 0, 255), new Color(255, 0, 255),
            new Color(0, 255, 255), new Color(255, 255, 0)
        };
        defaultLeftControlKeys = new char[]{'1', 'y', 'b', ',', 'o', '2'};
        defaultRightControlKeys = new char[]{'q', 'x', 'n', '.', '0', '3'};

        setSize(600, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(205, 205, 205));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        playerPanels = new PlayerSettingsPanel[MAX_PLAYERS];
        for (int i = 0; i < MAX_PLAYERS; i++) {
            playerPanels[i] = new PlayerSettingsPanel(i, defaultColors[i], defaultLeftControlKeys[i], defaultRightControlKeys[i]);
            mainPanel.add(playerPanels[i]);
        }

        allCheckBox = PlayerSettingsPanel.addAllSelector(mainPanel, playerPanels);

        optionsControlPanel = new OptionsControlPanel(
            GameEngine.DEFAULT_SPEED_LEVEL,
            GameEngine.MIN_SPEED_LEVEL,
            GameEngine.MAX_SPEED_LEVEL,
            this::startGame,
            this::loadDefaultPlayerSettings);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mainPanel.add(optionsControlPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        loadDefaultPlayerSettings();

        for (PlayerSettingsPanel panel : playerPanels) {
            registerPlayerPanelListeners(panel);
        }

        add(mainPanel);
        setVisible(true);
    }

    private void startGame() {
        generatePlayers();
        if (!isValidPlayerSetup()) {
            return;
        }
        int speedLevel = optionsControlPanel.getSpeed();
        setVisible(false);
        GameEngine gameEngine = new GameEngine(players, speedLevel, () -> setVisible(true));
        gameEngine.startGame();
    }

    private void loadDefaultPlayerSettings() {
        for (int i = 0; i < MAX_PLAYERS; i++) {
            playerPanels[i].checkBox.setSelected(false);
            playerPanels[i].nameField.setText("Player " + (i + 1));
            playerPanels[i].colorButton.setBackground(defaultColors[i]);
            playerPanels[i].leftKeyButton.setText(String.valueOf(defaultLeftControlKeys[i]));
            playerPanels[i].rightKeyButton.setText(String.valueOf(defaultRightControlKeys[i]));
            playerPanels[i].disableRow();
        }

        playerPanels[0].checkBox.setEnabled(true);
        playerPanels[0].checkBox.setSelected(true);
        playerPanels[0].enableRow();

        playerPanels[1].checkBox.setEnabled(true);
        playerPanels[1].checkBox.setSelected(true);
        playerPanels[1].enableRow();

        allCheckBox.setSelected(false);
        allCheckBox.setEnabled(true);

        optionsControlPanel.setSpeedToDefault();
    }

    private void generatePlayers() {
        players.clear();
        for (PlayerSettingsPanel panel : playerPanels) {
            if (panel.checkBox.isSelected()) {
                String name = panel.nameField.getText();
                Color color = panel.colorButton.getBackground();
                char leftKey = panel.leftKeyButton.getText().charAt(0);
                char rightKey = panel.rightKeyButton.getText().charAt(0);
                players.add(new Player(name, color, leftKey, rightKey));
            }
        }
    }

    private boolean isValidPlayerSetup() {
        if (players.size() < 2) {
            JOptionPane.showMessageDialog(this, "Please select at least 2 players.", GAME_TITLE, JOptionPane.WARNING_MESSAGE);
            return false;
        }
        for (Player player : players) {
            if (player.getPlayerName().isBlank()) {
                JOptionPane.showMessageDialog(this, "Player names must not be empty.", GAME_TITLE, JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void registerPlayerPanelListeners(PlayerSettingsPanel panel) {
        panel.checkBox.addActionListener(e -> {
            if (panel.checkBox.isSelected()) {
                panel.enableRow();
            } else {
                panel.disableRow();
            }
        });
        panel.colorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(this, "Choose Player Color", panel.colorButton.getBackground());
            if (selectedColor != null) {
                panel.colorButton.setBackground(selectedColor);
            }
        });
        panel.leftKeyButton.addActionListener(e -> {
            String newKey = JOptionPane.showInputDialog(this, "Enter new left control key for " + panel.nameField.getText(), panel.leftKeyButton.getText());
            if (newKey != null && !newKey.isEmpty()) {
                panel.leftKeyButton.setText(newKey);
            }
        });
        panel.rightKeyButton.addActionListener(e -> {
            String newKey = JOptionPane.showInputDialog(this, "Enter new right control key for " + panel.nameField.getText(), panel.rightKeyButton.getText());
            if (newKey != null && !newKey.isEmpty()) {
                panel.rightKeyButton.setText(newKey);
            }
        });
    }
}
