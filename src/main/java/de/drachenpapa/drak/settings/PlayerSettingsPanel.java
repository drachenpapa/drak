package de.drachenpapa.drak.settings;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for configuring individual player settings.
 * Allows customization of player controls and colors.
 */
class PlayerSettingsPanel extends JPanel {

    static final JCheckBox allCheckBox = new JCheckBox();
    static final JLabel allLabel = new JLabel(SettingsConstants.Text.ALL_LABEL);

    final JCheckBox checkBox;
    final JLabel label;
    final JTextField nameField;
    final JButton colorButton;
    final JButton leftKeyButton;
    final JButton rightKeyButton;

    PlayerSettingsPanel(int playerIndex, Color defaultColor, char defaultLeftKey, char defaultRightKey) {
        setLayout(new FlowLayout(FlowLayout.LEFT, SettingsConstants.Ui.PLAYER_FLOW_GAP, SettingsConstants.Ui.PLAYER_FLOW_GAP));
        setBackground(SettingsConstants.Ui.PANEL_BACKGROUND);

        checkBox = new JCheckBox();

        label = new JLabel(SettingsConstants.Players.PLAYER_NAME_PREFIX + (playerIndex + 1) + ":");
        label.setEnabled(false);

        nameField = new JTextField(SettingsConstants.Players.PLAYER_NAME_PREFIX + (playerIndex + 1));
        nameField.setPreferredSize(new Dimension(SettingsConstants.Ui.NAME_FIELD_WIDTH, SettingsConstants.Ui.NAME_FIELD_HEIGHT));
        nameField.setEditable(false);

        colorButton = new JButton(SettingsConstants.Text.COLOR_BUTTON);
        colorButton.setPreferredSize(new Dimension(SettingsConstants.Ui.KEY_BUTTON_WIDTH, SettingsConstants.Ui.KEY_BUTTON_HEIGHT));
        colorButton.setEnabled(false);
        colorButton.setBackground(defaultColor);

        leftKeyButton = new JButton(String.valueOf(defaultLeftKey));
        leftKeyButton.setPreferredSize(new Dimension(SettingsConstants.Ui.KEY_BUTTON_WIDTH, SettingsConstants.Ui.KEY_BUTTON_HEIGHT));
        leftKeyButton.setEnabled(false);

        rightKeyButton = new JButton(String.valueOf(defaultRightKey));
        rightKeyButton.setPreferredSize(new Dimension(SettingsConstants.Ui.KEY_BUTTON_WIDTH, SettingsConstants.Ui.KEY_BUTTON_HEIGHT));
        rightKeyButton.setEnabled(false);

        add(checkBox);
        add(label);
        add(Box.createRigidArea(new Dimension(SettingsConstants.Ui.PLAYER_ROW_SPACER_LARGE, 0)));
        add(nameField);
        add(Box.createRigidArea(new Dimension(SettingsConstants.Ui.PLAYER_ROW_SPACER_LARGE, 0)));
        add(colorButton);
        add(Box.createRigidArea(new Dimension(SettingsConstants.Ui.PLAYER_ROW_SPACER_SMALL, 0)));
        add(leftKeyButton);
        add(rightKeyButton);
    }

    static void addAllSelector(JPanel parent, PlayerSettingsPanel[] panels) {
        allCheckBox.setPreferredSize(new Dimension(SettingsConstants.Ui.ALL_SELECTOR_SIZE, SettingsConstants.Ui.ALL_SELECTOR_SIZE));
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(Box.createRigidArea(new Dimension(SettingsConstants.Ui.ALL_SELECTOR_OFFSET, 0)));
        leftPanel.add(allCheckBox);
        leftPanel.add(allLabel);
        parent.add(leftPanel);

        allCheckBox.addActionListener(e -> {
            boolean allSelected = allCheckBox.isSelected();
            for (PlayerSettingsPanel panel : panels) {
                panel.checkBox.setSelected(allSelected);
                if (allSelected) {
                    panel.enableRow();
                } else {
                    panel.disableRow();
                }
            }
        });
    }

    void enableRow() {
        label.setEnabled(true);
        nameField.setEditable(true);
        colorButton.setEnabled(true);
        leftKeyButton.setEnabled(true);
        rightKeyButton.setEnabled(true);
    }

    void disableRow() {
        label.setEnabled(false);
        nameField.setEditable(false);
        colorButton.setEnabled(false);
        leftKeyButton.setEnabled(false);
        rightKeyButton.setEnabled(false);
    }
}
