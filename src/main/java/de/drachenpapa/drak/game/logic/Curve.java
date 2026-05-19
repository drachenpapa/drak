package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.CurvePhysicsSettings;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Represents a player's curve on the game field.
 * Handles movement, direction, and gap logic for the curve.
 */
@Getter
@Setter
public class Curve {

    private static final RandomGenerator DEFAULT_RNG = RandomGenerator.getDefault();
    private final RandomGenerator randomGenerator;
    private final List<Point> points = new ArrayList<>();
    private double directionAngle;
    private int xPosition;
    private int yPosition;
    private int previousXPosition;
    private int previousYPosition;
    private int gapLengthCounter;
    private long lastGapTimestamp;
    private long gapInterval;
    private boolean isGapActive;

    Curve(int xPosition, int yPosition, double directionAngle, long gapInterval) {
        this(xPosition, yPosition, directionAngle, gapInterval, DEFAULT_RNG);
    }

    Curve(int xPosition, int yPosition, double directionAngle, long gapInterval, RandomGenerator randomGenerator) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.previousXPosition = xPosition;
        this.previousYPosition = yPosition;
        this.directionAngle = directionAngle;
        this.gapInterval = gapInterval;
        this.isGapActive = false;
        this.gapLengthCounter = 0;
        this.randomGenerator = randomGenerator;
        this.lastGapTimestamp = System.currentTimeMillis();
        addPoint(xPosition, yPosition);
    }

    void move() {
        previousXPosition = xPosition;
        previousYPosition = yPosition;
        double radians = Math.toRadians(directionAngle);
        xPosition += (int) (Math.cos(radians) * CurvePhysicsSettings.STEP_SIZE);
        yPosition -= (int) (Math.sin(radians) * CurvePhysicsSettings.STEP_SIZE);
        addPoint(xPosition, yPosition);
    }

    void addPoint(int x, int y) {
        points.add(new Point(x, y));
        trimOldPointsIfNeeded();
    }

    private void trimOldPointsIfNeeded() {
        if (points.size() <= CurvePhysicsSettings.MAX_STORED_POINTS) {
            return;
        }
        int trimCount = Math.min(
            CurvePhysicsSettings.POINT_TRIM_CHUNK_SIZE,
            points.size() - CurvePhysicsSettings.MAX_STORED_POINTS
        );
        points.subList(0, trimCount).clear();
    }

    void turnLeft() {
        directionAngle = (directionAngle + CurvePhysicsSettings.TURN_ANGLE_DEGREES) % CurvePhysicsSettings.ANGLE_FULL_CIRCLE;
    }

    void turnRight() {
        directionAngle = (directionAngle - CurvePhysicsSettings.TURN_ANGLE_DEGREES + CurvePhysicsSettings.ANGLE_FULL_CIRCLE) % CurvePhysicsSettings.ANGLE_FULL_CIRCLE;
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
        gapInterval = randomGenerator.nextLong(
            CurvePhysicsSettings.MIN_GAP_INTERVAL_MS,
            CurvePhysicsSettings.MAX_GAP_INTERVAL_MS
        );
        gapLengthCounter = randomGenerator.nextInt(
            CurvePhysicsSettings.MIN_GAP_LENGTH,
            CurvePhysicsSettings.MAX_GAP_LENGTH + 1
        );
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
