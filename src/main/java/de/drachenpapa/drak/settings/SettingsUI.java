package de.drachenpapa.drak.settings;

import com.formdev.flatlaf.FlatLightLaf;
import de.drachenpapa.drak.game.logic.GameConfig;
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

    private static final int MAX_PLAYERS = 6;
    private static final int DEFAULT_SPEED = 3;
    private static final String START_GAME_COMMAND = "Start Game";
    private static final String LOAD_DEFAULTS_COMMAND = "Load Defaults";

    private final char[] defaultLeftControlKeys;
    private final char[] defaultRightControlKeys;
    private final Color[] defaultColors;
    private final OptionsControlPanel optionsControlPanel;
    private final PlayerSettingsPanel[] playerPanels;
    private final transient SettingsPlayerAssembler playerAssembler;
    private final transient ControlKeyInputValidator keyInputValidator;

    public SettingsUI() {
        this(new SettingsPlayerAssembler(), new ControlKeyInputValidator());
    }

    SettingsUI(SettingsPlayerAssembler playerAssembler, ControlKeyInputValidator keyInputValidator) {
        this.playerAssembler = Objects.requireNonNull(playerAssembler);
        this.keyInputValidator = Objects.requireNonNull(keyInputValidator);
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Failed to initialize custom look and feel; falling back to default.", ex);
        }
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(5, 10, 5, 10));

        setTitle(GAME_TITLE);
        URL logoUrl = getClass().getResource("/logo.png");
        if (logoUrl != null) {
            setIconImage(Toolkit.getDefaultToolkit().getImage(logoUrl));
        } else {
            logger.log(Level.WARNING, "Logo resource /logo.png not found – window icon will not be set");
        }
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

        PlayerSettingsPanel.addAllSelector(mainPanel, playerPanels);

        optionsControlPanel = new OptionsControlPanel(DEFAULT_SPEED, 1, 5, this);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mainPanel.add(optionsControlPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        loadDefaultPlayerSettings();

        for (int i = 0; i < MAX_PLAYERS; i++) {
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
        if (START_GAME_COMMAND.equals(e.getActionCommand())) {
            int speedLevel = optionsControlPanel.getSpeed();
            GameConfig gameConfig = new GameConfig(speedLevel, playerAssembler.createSelectedPlayerConfigs(playerPanels));
            GameEngine gameEngine = new GameEngine(gameConfig);
            gameEngine.startGame();
        } else if (LOAD_DEFAULTS_COMMAND.equals(e.getActionCommand())) {
            loadDefaultPlayerSettings();
        } else {
            handlePlayerActions(e);
        }
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

        PlayerSettingsPanel.allCheckBox.setSelected(false);
        PlayerSettingsPanel.allCheckBox.setEnabled(true);

        optionsControlPanel.setSpeedToDefault();
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
                updateControlKeyFromInput(newKey, panel.leftKeyButton);
            } else if (e.getSource() == panel.rightKeyButton) {
                String newKey = JOptionPane.showInputDialog(this, "Enter new right control key for " + panel.nameField.getText(), panel.rightKeyButton.getText());
                updateControlKeyFromInput(newKey, panel.rightKeyButton);
            } else if (e.getSource() == panel.checkBox) {
                if (panel.checkBox.isSelected()) {
                    panel.enableRow();
                } else {
                    panel.disableRow();
                }
            }
        }
    }

    private void updateControlKeyFromInput(String newKey, JButton keyButton) {
        if (newKey == null) {
            return;
        }
        if (keyInputValidator.isInvalidKeyInput(newKey)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter exactly one non-whitespace character.",
                    "Invalid Control Key",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        keyButton.setText(String.valueOf(keyInputValidator.toControlKey(newKey)));
    }
}
