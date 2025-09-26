package de.drachenpapa.drak.game.logic;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player's curve on the game field.
 * Handles movement, direction, and gap logic for the curve.
 */
@Getter
@Setter
public class Curve {

    private static final long MAX_GAP_INTERVAL = 6000L;
    private static final long MIN_GAP_INTERVAL = 1000L;
    private static final int MIN_GAP_LENGTH = 2;
    private static final int MAX_GAP_LENGTH = 4;
    private static final double STEP_SIZE = 6.0;
    private static final double TURN_ANGLE = 10.0;

    private double directionAngle;
    private int xPosition;
    private int yPosition;
    private int previousXPosition;
    private int previousYPosition;
    private int gapLengthCounter;
    private long lastGapTimestamp;
    private long gapInterval;
    private boolean isGapActive;
    private final List<Point> points = new ArrayList<>();

    Curve(int xPosition, int yPosition, double directionAngle, long gapInterval) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.previousXPosition = xPosition;
        this.previousYPosition = yPosition;
        this.directionAngle = directionAngle;
        this.gapInterval = gapInterval;
        this.isGapActive = false;
        this.gapLengthCounter = 0;
        this.lastGapTimestamp = System.currentTimeMillis();
        addPoint(xPosition, yPosition);
    }

    void move() {
        previousXPosition = xPosition;
        previousYPosition = yPosition;
        double radians = Math.toRadians(directionAngle);
        xPosition += (int) (Math.cos(radians) * STEP_SIZE);
        yPosition -= (int) (Math.sin(radians) * STEP_SIZE);
        addPoint(xPosition, yPosition);
    }

    void addPoint(int x, int y) {
        points.add(new Point(x, y));
    }

    void turnLeft() {
        directionAngle = (directionAngle + TURN_ANGLE) % 360;
    }

    void turnRight() {
        directionAngle = (directionAngle - TURN_ANGLE + 360) % 360;
    }

    boolean isGeneratingGap() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastGapTimestamp > gapInterval) {
            startNewGap(currentTime);
        }
        return isGapActive && continueGap();
    }

    private void startNewGap(long currentTime) {
        isGapActive = true;
        lastGapTimestamp = currentTime;
        gapInterval = MIN_GAP_INTERVAL + (long) (Math.random() * (MAX_GAP_INTERVAL - MIN_GAP_INTERVAL));
        gapLengthCounter = MIN_GAP_LENGTH + (int) (Math.random() * (MAX_GAP_LENGTH - MIN_GAP_LENGTH + 1));
    }

    private boolean continueGap() {
        if (gapLengthCounter > 0) {
            gapLengthCounter--;
            return true;
        } else {
            isGapActive = false;
            return false;
        }
    }
}
