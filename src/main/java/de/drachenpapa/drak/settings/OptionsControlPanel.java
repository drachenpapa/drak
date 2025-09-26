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

    OptionsControlPanel(int initialSpeed, int minSpeed, int maxSpeed, ActionListener actionListener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(205, 205, 205));
        setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 9));

        JLabel speedLabel = new JLabel("Speed Level:");
        speedLabel.setPreferredSize(new Dimension(90, 25));
        speedSpinner = new JSpinner(new SpinnerNumberModel(initialSpeed, minSpeed, maxSpeed, 1));
        speedSpinner.setPreferredSize(new Dimension(40, 25));

        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        speedPanel.setOpaque(false);
        speedPanel.add(speedLabel);
        speedPanel.add(speedSpinner);

        JButton loadDefaultsButton = new JButton("Load Defaults");
        loadDefaultsButton.setFocusPainted(false);
        loadDefaultsButton.setBackground(new Color(180, 180, 180));
        loadDefaultsButton.addActionListener(actionListener);

        JButton startButton = new JButton("Start Game");
        startButton.setPreferredSize(new Dimension(140, 35));
        startButton.setMaximumSize(new Dimension(140, 35));
        startButton.setFocusPainted(false);
        startButton.setBackground(new Color(80, 150, 240));
        startButton.setForeground(Color.WHITE);
        startButton.setFont(startButton.getFont().deriveFont(Font.BOLD));
        startButton.addActionListener(actionListener);

        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.setOpaque(false);
        topRow.add(speedPanel);
        topRow.add(Box.createHorizontalGlue());
        topRow.add(loadDefaultsButton);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(startButton);

        add(topRow);
        add(Box.createRigidArea(new Dimension(0, 8)));
        add(buttonRow);
    }

    int getSpeed() {
        return (int) speedSpinner.getValue();
    }

    void setSpeedToDefault() {
        speedSpinner.setValue(3);
    }
}
