package de.drachenpapa.drak.settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel for configuring general game options.
 * Provides UI elements for adjusting global settings.
 */
class OptionsControlPanel extends JPanel {

    private final JSpinner speedSpinner;

    OptionsControlPanel(int initialSpeed, int minSpeed, int maxSpeed,
                        ActionListener onStart, ActionListener onLoadDefaults) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(SettingsConstants.Ui.PANEL_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 9));

        speedSpinner = createSpeedSpinner(initialSpeed, minSpeed, maxSpeed);
        JPanel speedPanel = createSpeedPanel(speedSpinner);
        JButton loadDefaultsButton = createLoadDefaultsButton(onLoadDefaults);

        JButton startButton = createStartButton(onStart);

        JPanel topRow = createTopRow(speedPanel, loadDefaultsButton);

        JPanel buttonRow = createButtonRow(startButton);

        add(topRow);
        add(Box.createRigidArea(new Dimension(0, SettingsConstants.Ui.OPTIONS_MAIN_SPACER)));
        add(buttonRow);
    }

    int getSpeed() {
        return (int) speedSpinner.getValue();
    }

    private JSpinner createSpeedSpinner(int initialSpeed, int minSpeed, int maxSpeed) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(initialSpeed, minSpeed, maxSpeed, 1));
        spinner.setPreferredSize(new Dimension(SettingsConstants.Ui.OPTIONS_SPEED_SPINNER_WIDTH, SettingsConstants.Ui.OPTIONS_SPEED_SPINNER_HEIGHT));
        return spinner;
    }

    private JPanel createSpeedPanel(JSpinner spinner) {
        JLabel speedLabel = new JLabel(SettingsConstants.Text.SPEED_LEVEL_LABEL);
        speedLabel.setPreferredSize(new Dimension(SettingsConstants.Ui.OPTIONS_SPEED_LABEL_WIDTH, SettingsConstants.Ui.OPTIONS_SPEED_LABEL_HEIGHT));

        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        speedPanel.setOpaque(false);
        speedPanel.add(speedLabel);
        speedPanel.add(spinner);
        return speedPanel;
    }

    private JButton createLoadDefaultsButton(ActionListener actionListener) {
        JButton loadDefaultsButton = new JButton(SettingsConstants.Text.LOAD_DEFAULTS_BUTTON);
        loadDefaultsButton.setFocusPainted(false);
        loadDefaultsButton.setBackground(SettingsConstants.Ui.LOAD_DEFAULTS_BUTTON_BACKGROUND);
        loadDefaultsButton.addActionListener(actionListener);
        return loadDefaultsButton;
    }

    private JPanel createTopRow(JPanel speedPanel, JButton loadDefaultsButton) {
        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.setOpaque(false);
        topRow.add(speedPanel);
        topRow.add(Box.createHorizontalGlue());
        topRow.add(loadDefaultsButton);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, SettingsConstants.Ui.OPTIONS_TOP_ROW_MAX_HEIGHT));
        return topRow;
    }

    private JPanel createButtonRow(JButton startButton) {
        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(startButton);
        return buttonRow;
    }

    private JButton createStartButton(ActionListener actionListener) {
        JButton startButton = new JButton(SettingsConstants.Text.START_GAME_BUTTON);
        startButton.setPreferredSize(new Dimension(SettingsConstants.Ui.OPTIONS_START_BUTTON_WIDTH, SettingsConstants.Ui.OPTIONS_START_BUTTON_HEIGHT));
        startButton.setMaximumSize(new Dimension(SettingsConstants.Ui.OPTIONS_START_BUTTON_WIDTH, SettingsConstants.Ui.OPTIONS_START_BUTTON_HEIGHT));
        startButton.setFocusPainted(false);
        startButton.setBackground(SettingsConstants.Ui.START_BUTTON_BACKGROUND);
        startButton.setForeground(Color.WHITE);
        startButton.setFont(startButton.getFont().deriveFont(Font.BOLD));
        startButton.addActionListener(actionListener);
        return startButton;
    }

    void setSpeedToDefault() {
        speedSpinner.setValue(SettingsConstants.Speed.DEFAULT);
    }
}
