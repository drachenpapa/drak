package de.drachenpapa.drak.settings;

import de.drachenpapa.drak.game.logic.PlayerConfig;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

/**
 * Panel for configuring individual player settings.
 * Allows customization of player controls and colors.
 */
class PlayerSettingsPanel extends JPanel {

    final JCheckBox checkBox;
    final JLabel label;
    final JTextField nameField;
    final JButton colorButton;
    final JButton leftKeyButton;
    final JButton rightKeyButton;

    PlayerSettingsPanel(int playerIndex, Color defaultColor, char defaultLeftKey, char defaultRightKey) {
        initializePanelLayout();

        checkBox = new JCheckBox();
        label = createDisabledLabel(playerIndex);
        nameField = createPlayerNameField(playerIndex);
        colorButton = createDisabledButton(SettingsConstants.Text.COLOR_BUTTON, defaultColor);
        leftKeyButton = createDisabledButton(String.valueOf(defaultLeftKey), null);
        rightKeyButton = createDisabledButton(String.valueOf(defaultRightKey), null);

        addRowComponents();
    }

    private static Component createHorizontalSpacer(int width) {
        return Box.createRigidArea(new Dimension(width, 0));
    }

    /**
     * Creates and registers the "select all" row at the bottom of the parent panel.
     *
     * @return the JCheckBox instance so the caller can manage its state.
     */
    static JCheckBox addAllSelector(JPanel parent, PlayerSettingsPanel[] panels) {
        JCheckBox allCheckBox = new JCheckBox();
        JLabel allLabel = new JLabel(SettingsConstants.Text.ALL_LABEL);

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
        return allCheckBox;
    }

    private void initializePanelLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, SettingsConstants.Ui.PLAYER_FLOW_GAP, SettingsConstants.Ui.PLAYER_FLOW_GAP));
        setBackground(SettingsConstants.Ui.PANEL_BACKGROUND);
    }

    private JLabel createDisabledLabel(int playerIndex) {
        JLabel playerLabel = new JLabel(SettingsConstants.Players.PLAYER_NAME_PREFIX + (playerIndex + 1) + ":");
        playerLabel.setEnabled(false);
        return playerLabel;
    }

    private JTextField createPlayerNameField(int playerIndex) {
        JTextField field = new JTextField(SettingsConstants.Players.PLAYER_NAME_PREFIX + (playerIndex + 1));
        field.setPreferredSize(new Dimension(SettingsConstants.Ui.NAME_FIELD_WIDTH, SettingsConstants.Ui.NAME_FIELD_HEIGHT));
        field.setToolTipText("Max. %d Zeichen".formatted(PlayerConfig.MAX_PLAYER_NAME_LENGTH));
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new MaxLengthDocumentFilter(PlayerConfig.MAX_PLAYER_NAME_LENGTH));
        field.setEditable(false);
        return field;
    }

    private JButton createDisabledButton(String text, Color background) {
        JButton button = new JButton(text);
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
            String replacement = text == null ? "" : text;
            int newLength = fb.getDocument().getLength() - length + replacement.length();
            if (newLength <= maxLength) {
                super.replace(fb, offset, length, replacement, attrs);
            }
        }
    }
}
