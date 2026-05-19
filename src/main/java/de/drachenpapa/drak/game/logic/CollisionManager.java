package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.CollisionSettings;
import de.drachenpapa.drak.game.config.DisplaySettings;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Handles collision detection and collision-related actions for all players.
 * Checks for self-collisions, collisions with other curves, and manages collision consequences.
 */
class CollisionManager {

    private final List<Player> players;
    private final List<Point[]> curvePoints;
    private final PlayerManager playerManager;

    CollisionManager(List<Player> players, List<Point[]> curvePoints, PlayerManager playerManager) {
        this.players = players;
        this.curvePoints = curvePoints;
        this.playerManager = playerManager;
    }

    void handleCollision(int playerIndex) {
        players.get(playerIndex).setAlive(false);
        playerManager.increasePointsForAlivePlayers();
    }

    boolean isCollisionDetected(Curve curve) {
        wrapCurveIfNeeded(curve);
        return detectCurveCollision(curve);
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

    private boolean detectCurveCollision(Curve curve) {
        Point[] movementPoints = buildSegmentPoints(
            curve.getPreviousXPosition(),
            curve.getPreviousYPosition(),
            curve.getXPosition(),
            curve.getYPosition()
        );

        for (Point point : movementPoints) {
            int x = point.x;
            int y = point.y;
            if (isOutOfBounds(x, y) || isSelfCollision(curve, x, y) || isOtherCurveCollision(x, y)) {
                return true;
            }
        }

        curvePoints.add(movementPoints);
        return false;
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

    private boolean isOtherCurveCollision(int x, int y) {
        return curvePoints.stream()
            .flatMap(Arrays::stream)
            .anyMatch(p -> isPointCollision(p, x, y));
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
