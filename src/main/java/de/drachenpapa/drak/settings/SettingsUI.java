package de.drachenpapa.drak.settings;

import com.formdev.flatlaf.FlatLightLaf;
import de.drachenpapa.drak.game.logic.GameEngine;
import de.drachenpapa.drak.game.logic.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static de.drachenpapa.drak.Drak.GAME_TITLE;

/**
 * Main panel for configuring all game and player settings.
 * Combines option and player panels into a single settings UI.
 */
public class SettingsUI extends JFrame implements ActionListener {

    private final char[] defaultLeftControlKeys;
    private final char[] defaultRightControlKeys;
    private final int maxPlayers = 6;
    private final Color[] defaultColors;
    private final OptionsControlPanel optionsControlPanel;
    private final PlayerSettingsPanel[] playerPanels;
    private final List<Player> players = new ArrayList<>(maxPlayers);

    public SettingsUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(5, 10, 5, 10));

        setTitle(GAME_TITLE);
        try {
            Image logo = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/logo.png"));
            setIconImage(logo);
        } catch (Exception ignored) { }
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        playerPanels = new PlayerSettingsPanel[maxPlayers];
        for (int i = 0; i < maxPlayers; i++) {
            playerPanels[i] = new PlayerSettingsPanel(i, defaultColors[i], defaultLeftControlKeys[i], defaultRightControlKeys[i]);
            mainPanel.add(playerPanels[i]);
        }

        PlayerSettingsPanel.addAllSelector(mainPanel, playerPanels);

        optionsControlPanel = new OptionsControlPanel(3, 1, 5, this);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mainPanel.add(optionsControlPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        loadDefaultPlayerSettings();

        for (int i = 0; i < maxPlayers; i++) {
            playerPanels[i].checkBox.addActionListener(this);
            playerPanels[i].colorButton.addActionListener(this);
            playerPanels[i].leftKeyButton.addActionListener(this);
            playerPanels[i].rightKeyButton.addActionListener(this);
        }

        add(mainPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Start Game")) {
            generatePlayers();
            int speedLevel = optionsControlPanel.getSpeed();
            GameEngine gameEngine = new GameEngine(players, speedLevel);
            gameEngine.startGame();
        } else if (e.getActionCommand().equals("Load Defaults")) {
            loadDefaultPlayerSettings();
        } else {
            handlePlayerActions(e);
        }
    }

    private void loadDefaultPlayerSettings() {
        for (int i = 0; i < maxPlayers; i++) {
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

        PlayerSettingsPanel.allCheckBox.setSelected(false);
        PlayerSettingsPanel.allCheckBox.setEnabled(true);

        optionsControlPanel.setSpeedToDefault();
    }

    private void generatePlayers() {
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

    private void handlePlayerActions(ActionEvent e) {
        for (PlayerSettingsPanel panel : playerPanels) {
            if (e.getSource() == panel.colorButton) {
                Color selectedColor = JColorChooser.showDialog(this, "Choose Player Color", panel.colorButton.getBackground());
                if (selectedColor != null) {
                    panel.colorButton.setBackground(selectedColor);
                }
            } else if (e.getSource() == panel.leftKeyButton) {
                String newKey = JOptionPane.showInputDialog(this, "Enter new left control key for " + panel.nameField.getText(), panel.leftKeyButton.getText());
                if (newKey != null && !newKey.isEmpty()) {
                    panel.leftKeyButton.setText(newKey);
                }
            } else if (e.getSource() == panel.rightKeyButton) {
                String newKey = JOptionPane.showInputDialog(this, "Enter new right control key for " + panel.nameField.getText(), panel.rightKeyButton.getText());
                if (newKey != null && !newKey.isEmpty()) {
                    panel.rightKeyButton.setText(newKey);
                }
            } else if (e.getSource() == panel.checkBox) {
                if (panel.checkBox.isSelected()) {
                    panel.enableRow();
                } else {
                    panel.disableRow();
                }
            }
        }
    }
}
