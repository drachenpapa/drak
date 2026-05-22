package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.BalanceSettings;
import de.drachenpapa.drak.game.config.CollisionSettings;
import de.drachenpapa.drak.game.config.CurvePhysicsSettings;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedCollection;
import java.util.random.RandomGenerator;

/**
 * Represents a player's curve on the game field.
 * Handles movement, direction, and gap logic for the curve.
 */
public class Curve {

    private final RandomGenerator randomGenerator;
    private final ArrayDeque<Point> points = new ArrayDeque<>();
    /**
     * Grace-zone ring buffer tracking recently placed points to avoid self-collision
     * false positives at the start of a new trail segment.
     *
     * <p>Invariant: {@code recentGraceKeys.size() <= CollisionSettings.GRACE_ZONE_CAPACITY}.
     * {@code graceKeyCounts} holds the occurrence count for every key currently inside
     * {@code recentGraceKeys}: i.e. it is the multiset representation of that deque.
     * When a key is evicted from the front of the deque its count is decremented and
     * removed from the map once it reaches zero.
     */
    private final ArrayDeque<Long> recentGraceKeys = new ArrayDeque<>();
    private final Map<Long, Integer> graceKeyCounts = new HashMap<>();
    private final int tickIntervalMs;
    private double directionAngle;
    @Getter
    private int xPosition;
    @Getter
    private int yPosition;
    @Getter
    private int previousXPosition;
    @Getter
    private int previousYPosition;
    private int gapLengthCounter;
    private int ticksSinceLastGap;
    @Getter
    private int gapIntervalTicks;
    private boolean isGapActive;

    Curve(int xPosition, int yPosition, double directionAngle, int gapIntervalTicks) {
        this(xPosition, yPosition, directionAngle, gapIntervalTicks, BalanceSettings.DEFAULT_TICK_INTERVAL_MS);
    }

    Curve(int xPosition, int yPosition, double directionAngle, int gapIntervalTicks, int tickIntervalMs) {
        this(xPosition, yPosition, directionAngle, gapIntervalTicks, tickIntervalMs, RandomGenerator.getDefault());
    }

    Curve(int xPosition, int yPosition, double directionAngle, int gapIntervalTicks, int tickIntervalMs, RandomGenerator randomGenerator) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.previousXPosition = xPosition;
        this.previousYPosition = yPosition;
        this.directionAngle = directionAngle;
        this.gapIntervalTicks = gapIntervalTicks;
        this.tickIntervalMs = Math.max(1, tickIntervalMs);
        this.isGapActive = false;
        this.gapLengthCounter = 0;
        this.ticksSinceLastGap = 0;
        this.randomGenerator = Objects.requireNonNull(randomGenerator, "randomGenerator");
        addPoint(xPosition, yPosition);
    }

    private static long encodeGraceKey(int x, int y) {
        return ((long) x << 32) | (y & 0xFFFFFFFFL);
    }

    double getDirectionAngle() {
        return directionAngle;
    }

    SequencedCollection<Point> getPoints() {
        return Collections.unmodifiableSequencedCollection(points);
    }

    boolean isGapActive() {
        return isGapActive;
    }

    void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    void setPreviousXPosition(int previousXPosition) {
        this.previousXPosition = previousXPosition;
    }

    void setPreviousYPosition(int previousYPosition) {
        this.previousYPosition = previousYPosition;
    }

    void move() {
        previousXPosition = xPosition;
        previousYPosition = yPosition;
        var radians = Math.toRadians(directionAngle);
        xPosition += (int) Math.round(Math.cos(radians) * CurvePhysicsSettings.STEP_SIZE);
        yPosition -= (int) Math.round(Math.sin(radians) * CurvePhysicsSettings.STEP_SIZE);
        addPoint(xPosition, yPosition);
    }

    void addPoint(int x, int y) {
        var p = new Point(x, y);
        points.addLast(p);
        trimOldPointsIfNeeded();
    }

    void addGraceSegment(Point[] segment) {
        for (var p : segment) {
            long key = encodeGraceKey(p.x, p.y);
            recentGraceKeys.addLast(key);
            graceKeyCounts.merge(key, 1, Integer::sum);
        }
        while (recentGraceKeys.size() > CollisionSettings.GRACE_ZONE_CAPACITY) {
            long evicted = recentGraceKeys.removeFirst();
            graceKeyCounts.compute(evicted, (k, v) -> (v == null || v <= 1) ? null : v - 1);
        }
    }

    boolean isRecentGracePoint(int x, int y) {
        return graceKeyCounts.containsKey(encodeGraceKey(x, y));
    }

    private void trimOldPointsIfNeeded() {
        while (points.size() > CurvePhysicsSettings.MAX_STORED_POINTS) {
            points.removeFirst();
        }
    }

    void turnLeft() {
        directionAngle = (directionAngle + CurvePhysicsSettings.TURN_ANGLE_DEGREES) % CurvePhysicsSettings.ANGLE_FULL_CIRCLE;
    }

    void turnRight() {
        directionAngle = (directionAngle - CurvePhysicsSettings.TURN_ANGLE_DEGREES + CurvePhysicsSettings.ANGLE_FULL_CIRCLE) % CurvePhysicsSettings.ANGLE_FULL_CIRCLE;
    }

    void tickGap() {
        ticksSinceLastGap++;
        if (ticksSinceLastGap >= gapIntervalTicks) {
            startNewGap();
        }
        if (isGapActive) {
            continueGap();
        }
    }

    private void startNewGap() {
        isGapActive = true;
        ticksSinceLastGap = 0;
        long intervalMs = randomGenerator.nextLong(
            CurvePhysicsSettings.MIN_GAP_INTERVAL_MS,
            CurvePhysicsSettings.MAX_GAP_INTERVAL_MS
        );
        gapIntervalTicks = Math.max(1, (int) (intervalMs / tickIntervalMs));
        gapLengthCounter = randomGenerator.nextInt(
            CurvePhysicsSettings.MIN_GAP_LENGTH,
            CurvePhysicsSettings.MAX_GAP_LENGTH + 1
        );
    }

    private void continueGap() {
        if (gapLengthCounter > 0) {
            gapLengthCounter--;
        } else {
            isGapActive = false;
        }
    }
}
