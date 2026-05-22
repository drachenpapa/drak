package de.drachenpapa.drak.settings;

import de.drachenpapa.drak.game.logic.PlayerConfig;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel for configuring individual player settings.
 * Allows customization of player controls and colors.
 */
class PlayerSettingsPanel extends JPanel {

    private final JCheckBox checkBox;
    private final JLabel label;
    private final JTextField nameField;
    private final JButton colorButton;
    private final JButton leftKeyButton;
    private final JButton rightKeyButton;
    private Color selectedColor;

    PlayerSettingsPanel(int playerIndex, Color defaultColor, char defaultLeftKey, char defaultRightKey) {
        initializePanelLayout();

        checkBox = new JCheckBox();
        label = createDisabledLabel(playerIndex);
        nameField = createPlayerNameField(playerIndex);
        colorButton = createDisabledButton(SettingsConstants.Text.COLOR_BUTTON, defaultColor);
        leftKeyButton = createDisabledButton(String.valueOf(defaultLeftKey), null);
        rightKeyButton = createDisabledButton(String.valueOf(defaultRightKey), null);
        selectedColor = defaultColor;

        addRowComponents();
    }

    private static Component createHorizontalSpacer(int width) {
        return Box.createRigidArea(new Dimension(width, 0));
    }

    static JCheckBox addAllSelector(JPanel parent, PlayerSettingsPanel[] panels) {
        var allCheckBox = new JCheckBox();
        var allLabel = new JLabel(SettingsConstants.Text.ALL_LABEL);

        allCheckBox.setPreferredSize(new Dimension(SettingsConstants.Ui.ALL_SELECTOR_SIZE, SettingsConstants.Ui.ALL_SELECTOR_SIZE));
        var leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(Box.createRigidArea(new Dimension(SettingsConstants.Ui.ALL_SELECTOR_OFFSET, 0)));
        leftPanel.add(allCheckBox);
        leftPanel.add(allLabel);
        parent.add(leftPanel);

        allCheckBox.addActionListener(e -> {
            boolean allSelected = allCheckBox.isSelected();
            for (var panel : panels) {
                panel.setSelected(allSelected);
                if (allSelected) {
                    panel.enableRow();
                } else {
                    panel.disableRow();
                }
            }
        });
        return allCheckBox;
    }

    boolean isSelected() {
        return checkBox.isSelected();
    }

    void setSelected(boolean selected) {
        checkBox.setSelected(selected);
    }

    void setCheckBoxEnabled() {
        checkBox.setEnabled(true);
    }

    String getPlayerName() {
        return nameField.getText().strip();
    }

    void setPlayerName(String name) {
        nameField.setText(name);
    }

    Color getSelectedColor() {
        return selectedColor;
    }

    void setSelectedColor(Color color) {
        selectedColor = color;
        colorButton.setBackground(color);
    }

    String getLeftKey() {
        return leftKeyButton.getText();
    }

    void setLeftKey(String key) {
        leftKeyButton.setText(key);
    }

    String getRightKey() {
        return rightKeyButton.getText();
    }

    void setRightKey(String key) {
        rightKeyButton.setText(key);
    }

    void addCheckBoxListener(ActionListener listener) {
        checkBox.addActionListener(listener);
    }

    void addColorButtonListener(ActionListener listener) {
        colorButton.addActionListener(listener);
    }

    void addLeftKeyButtonListener(ActionListener listener) {
        leftKeyButton.addActionListener(listener);
    }

    void addRightKeyButtonListener(ActionListener listener) {
        rightKeyButton.addActionListener(listener);
    }

    private void initializePanelLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, SettingsConstants.Ui.PLAYER_FLOW_GAP, SettingsConstants.Ui.PLAYER_FLOW_GAP));
        setBackground(SettingsConstants.Ui.PANEL_BACKGROUND);
    }

    private JLabel createDisabledLabel(int playerIndex) {
        var playerLabel = new JLabel(SettingsConstants.Players.PLAYER_NAME_PREFIX + (playerIndex + 1) + ":");
        playerLabel.setEnabled(false);
        return playerLabel;
    }

    private JTextField createPlayerNameField(int playerIndex) {
        var field = new JTextField(SettingsConstants.Players.PLAYER_NAME_PREFIX + (playerIndex + 1));
        field.setPreferredSize(new Dimension(SettingsConstants.Ui.NAME_FIELD_WIDTH, SettingsConstants.Ui.NAME_FIELD_HEIGHT));
        field.setToolTipText("Max. %d characters".formatted(PlayerConfig.MAX_PLAYER_NAME_LENGTH));
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new MaxLengthDocumentFilter(PlayerConfig.MAX_PLAYER_NAME_LENGTH));
        field.setEditable(false);
        return field;
    }

    private JButton createDisabledButton(String text, Color background) {
        var button = new JButton(text);
        button.setPreferredSize(new Dimension(SettingsConstants.Ui.KEY_BUTTON_WIDTH, SettingsConstants.Ui.KEY_BUTTON_HEIGHT));
        button.setEnabled(false);
        if (background != null) {
            button.setBackground(background);
        }
        return button;
    }

    private void addRowComponents() {
        add(checkBox);
        add(label);
        add(createHorizontalSpacer(SettingsConstants.Ui.PLAYER_ROW_SPACER_LARGE));
        add(nameField);
        add(createHorizontalSpacer(SettingsConstants.Ui.PLAYER_ROW_SPACER_LARGE));
        add(colorButton);
        add(createHorizontalSpacer(SettingsConstants.Ui.PLAYER_ROW_SPACER_SMALL));
        add(leftKeyButton);
        add(rightKeyButton);
    }

    void enableRow() {
        setRowEnabled(true);
    }

    void disableRow() {
        setRowEnabled(false);
    }

    private void setRowEnabled(boolean enabled) {
        label.setEnabled(enabled);
        nameField.setEditable(enabled);
        colorButton.setEnabled(enabled);
        leftKeyButton.setEnabled(enabled);
        rightKeyButton.setEnabled(enabled);
    }

    private static final class MaxLengthDocumentFilter extends DocumentFilter {

        private final int maxLength;

        private MaxLengthDocumentFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) {
                return;
            }
            if (fb.getDocument().getLength() + string.length() <= maxLength) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            var replacement = text == null ? "" : text;
            int newLength = fb.getDocument().getLength() - length + replacement.length();
            if (newLength <= maxLength) {
                super.replace(fb, offset, length, replacement, attrs);
            }
        }
    }
}
