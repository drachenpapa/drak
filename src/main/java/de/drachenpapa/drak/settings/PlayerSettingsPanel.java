package de.drachenpapa.drak.settings;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for configuring individual player settings.
 * Allows customization of player controls and colors.
 */
class PlayerSettingsPanel extends JPanel {

    static final JCheckBox allCheckBox = new JCheckBox();
    static final JLabel allLabel = new JLabel("All");

    final JCheckBox checkBox;
    final JLabel label;
    final JTextField nameField;
    final JButton colorButton;
    final JButton leftKeyButton;
    final JButton rightKeyButton;

    PlayerSettingsPanel(int playerIndex, Color defaultColor, char defaultLeftKey, char defaultRightKey) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));
        setBackground(new Color(205, 205, 205));

        checkBox = new JCheckBox();

        label = new JLabel("Player " + (playerIndex + 1) + ":");
        label.setEnabled(false);

        nameField = new JTextField("Player " + (playerIndex + 1));
        nameField.setPreferredSize(new Dimension(150, 25));
        nameField.setEditable(false);

        colorButton = new JButton("Color");
        colorButton.setPreferredSize(new Dimension(85, 25));
        colorButton.setEnabled(false);
        colorButton.setBackground(defaultColor);

        leftKeyButton = new JButton(String.valueOf(defaultLeftKey));
        leftKeyButton.setPreferredSize(new Dimension(85, 25));
        leftKeyButton.setEnabled(false);

        rightKeyButton = new JButton(String.valueOf(defaultRightKey));
        rightKeyButton.setPreferredSize(new Dimension(85, 25));
        rightKeyButton.setEnabled(false);

        add(checkBox);
        add(label);
        add(Box.createRigidArea(new Dimension(20, 0)));
        add(nameField);
        add(Box.createRigidArea(new Dimension(20, 0)));
        add(colorButton);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(leftKeyButton);
        add(rightKeyButton);
    }

    static void addAllSelector(JPanel parent, PlayerSettingsPanel[] panels) {
        allCheckBox.setPreferredSize(new Dimension(25, 25));
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(Box.createRigidArea(new Dimension(6, 0)));
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
