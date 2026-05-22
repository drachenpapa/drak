package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.CollisionSettings;
import de.drachenpapa.drak.game.config.DisplaySettings;

import java.awt.*;
import java.util.Objects;

/**
 * Handles collision detection and collision-related actions for all players.
 * Uses an occupancy grid (O(1) lookup) for all trail collision detection.
 * Own-trail cells are skipped only within the recent grace-zone buffer held by the curve.
 *
 * <p>CQS note: {@link #wrapCurvePosition(Curve)} is a command (mutates curve position);
 * {@link #isCollisionDetected(Curve, int)} is a pure query (no side effects).
 * Callers must invoke {@code wrapCurvePosition} explicitly before {@code isCollisionDetected}.
 */
class CollisionManager {

    private final PlayerManager playerManager;

    CollisionManager(PlayerManager playerManager) {
        this.playerManager = Objects.requireNonNull(playerManager, "playerManager");
    }

    void handleCollision(int playerIndex) {
        var player = playerManager.getPlayers().get(playerIndex);
        player.kill();
        playerManager.increasePointsForAlivePlayers();
    }

    void wrapCurvePosition(Curve curve) {
        int x = curve.getXPosition();
        int y = curve.getYPosition();

        if (x >= DisplaySettings.PLAY_AREA_WIDTH) {
            curve.setXPosition(0);
            curve.setPreviousXPosition(0);
        } else if (x < 0) {
            curve.setXPosition(DisplaySettings.PLAY_AREA_WIDTH - 1);
            curve.setPreviousXPosition(DisplaySettings.PLAY_AREA_WIDTH - 1);
        }

        if (y >= DisplaySettings.PLAY_AREA_HEIGHT) {
            curve.setYPosition(0);
            curve.setPreviousYPosition(0);
        } else if (y < 0) {
            curve.setYPosition(DisplaySettings.PLAY_AREA_HEIGHT - 1);
            curve.setPreviousYPosition(DisplaySettings.PLAY_AREA_HEIGHT - 1);
        }
    }

    boolean isCollisionDetected(Curve curve, int playerIndex) {
        var movementPoints = buildSegmentPoints(
            curve.getPreviousXPosition(),
            curve.getPreviousYPosition(),
            curve.getXPosition(),
            curve.getYPosition()
        );

        for (var point : movementPoints) {
            if (DisplaySettings.isOutOfBounds(point.x, point.y)
                || isTrailCollision(curve, point.x, point.y, playerIndex)) {
                return true;
            }
        }

        markPointsInGrid(movementPoints, playerIndex);
        curve.addGraceSegment(movementPoints);
        return false;
    }

    private void markPointsInGrid(Point[] points, int playerIndex) {
        int ownerId = playerIndex + 1;
        for (var p : points) {
            if (!DisplaySettings.isOutOfBounds(p.x, p.y)) {
                playerManager.markTrailOwner(p.x, p.y, ownerId);
            }
        }
    }

    private Point[] buildSegmentPoints(int fromX, int fromY, int toX, int toY) {
        int dx = toX - fromX;
        int dy = toY - fromY;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps > CollisionSettings.MAX_INTERPOLATION_STEPS_PER_TICK || steps == 0) {
            return new Point[]{new Point(toX, toY)};
        }

        var points = new Point[steps + 1];
        for (int i = 0; i <= steps; i++) {
            double ratio = (double) i / steps;
            int x = (int) Math.round(fromX + (dx * ratio));
            int y = (int) Math.round(fromY + (dy * ratio));
            points[i] = new Point(x, y);
        }
        return points;
    }

    private boolean isTrailCollision(Curve curve, int x, int y, int playerIndex) {
        int ownerId = playerIndex + 1;
        int radius = CollisionSettings.CURVE_WIDTH;
        int minX = Math.max(0, x - radius);
        int maxX = Math.min(DisplaySettings.PLAY_AREA_WIDTH - 1, x + radius);
        int minY = Math.max(0, y - radius);
        int maxY = Math.min(DisplaySettings.PLAY_AREA_HEIGHT - 1, y + radius);

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {
                int cellOwner = playerManager.getTrailOwner(px, py);
                if (cellOwner == 0) continue;
                if (cellOwner == ownerId && curve.isRecentGracePoint(px, py)) continue;
                if (isPointCollision(px, py, x, y)) return true;
            }
        }
        return false;
    }

    private boolean isPointCollision(int px, int py, int x, int y) {
        return Math.hypot((double) px - x, (double) py - y) < CollisionSettings.CURVE_WIDTH;
    }
}
