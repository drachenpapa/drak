package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.CollisionSettings;
import de.drachenpapa.drak.game.config.DisplaySettings;

import java.awt.*;
import java.util.List;

/**
 * Handles collision detection and collision-related actions for all players.
 * Uses an occupancy grid (O(1) lookup) for cross-curve collision detection,
 * and the per-curve point list for recent self-collision skip logic.
 */
class CollisionManager {

    private final PlayerManager playerManager;

    CollisionManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    void handleCollision(int playerIndex) {
        playerManager.getPlayers().get(playerIndex).setAlive(false);
        playerManager.increasePointsForAlivePlayers();
    }

    boolean isCollisionDetected(Curve curve, int playerIndex) {
        wrapCurveIfNeeded(curve);
        return detectCurveCollision(curve, playerIndex);
    }

    private void wrapCurveIfNeeded(Curve curve) {
        double x = curve.getXPosition();
        double y = curve.getYPosition();

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

    private boolean detectCurveCollision(Curve curve, int playerIndex) {
        Point[] movementPoints = buildSegmentPoints(
            curve.getPreviousXPosition(),
            curve.getPreviousYPosition(),
            curve.getXPosition(),
            curve.getYPosition()
        );

        for (Point point : movementPoints) {
            int x = point.x;
            int y = point.y;
            if (isOutOfBounds(x, y) || isSelfCollision(curve, x, y) || isOtherTrailCollision(x, y, playerIndex)) {
                return true;
            }
        }

        markPointsInGrid(movementPoints, playerIndex);
        return false;
    }

    private void markPointsInGrid(Point[] points, int playerIndex) {
        int ownerId = playerIndex + 1;
        for (Point p : points) {
            if (!isOutOfBounds(p.x, p.y)) {
                playerManager.markTrailOwner(p.x, p.y, ownerId);
            }
        }
    }

    private Point[] buildSegmentPoints(int fromX, int fromY, int toX, int toY) {
        int dx = toX - fromX;
        int dy = toY - fromY;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps > CollisionSettings.MAX_INTERPOLATION_STEPS_PER_TICK) {
            return new Point[]{new Point(toX, toY)};
        }
        if (steps == 0) {
            return new Point[]{new Point(toX, toY)};
        }

        Point[] points = new Point[steps + 1];
        for (int i = 0; i <= steps; i++) {
            double ratio = (double) i / steps;
            int x = (int) Math.round(fromX + (dx * ratio));
            int y = (int) Math.round(fromY + (dy * ratio));
            points[i] = new Point(x, y);
        }
        return points;
    }

    private boolean isOutOfBounds(int x, int y) {
        return x < 0
            || x >= DisplaySettings.PLAY_AREA_WIDTH
            || y < 0
            || y >= DisplaySettings.PLAY_AREA_HEIGHT;
    }

    private boolean isSelfCollision(Curve curve, int x, int y) {
        List<Point> points = curve.getPoints();
        int len = points.size();

        return points.stream()
            .limit(Math.max(0, len - CollisionSettings.SELF_COLLISION_SKIP))
            .anyMatch(p -> isPointCollision(p, x, y));
    }

    private boolean isOtherTrailCollision(int x, int y, int playerIndex) {
        int ownerToIgnore = playerIndex + 1;
        int radius = CollisionSettings.CURVE_WIDTH;
        int minX = Math.max(0, x - radius);
        int maxX = Math.min(DisplaySettings.PLAY_AREA_WIDTH - 1, x + radius);
        int minY = Math.max(0, y - radius);
        int maxY = Math.min(DisplaySettings.PLAY_AREA_HEIGHT - 1, y + radius);

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {
                int ownerId = playerManager.getTrailOwner(px, py);
                if (ownerId == 0 || ownerId == ownerToIgnore) {
                    continue;
                }
                if (isPointCollision(new Point(px, py), x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPointCollision(Point p, int x, int y) {
        if (Math.abs(p.x - x) <= CollisionSettings.CURVE_WIDTH
            && Math.abs(p.y - y) <= CollisionSettings.CURVE_WIDTH) {
            double deltaX = (double) p.x - x;
            double deltaY = (double) p.y - y;
            double dist = Math.hypot(deltaX, deltaY);
            return dist < CollisionSettings.CURVE_WIDTH;
        }

        return false;
    }
}
