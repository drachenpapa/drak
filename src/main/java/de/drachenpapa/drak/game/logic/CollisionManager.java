package de.drachenpapa.drak.game.logic;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static de.drachenpapa.drak.game.logic.GameEngine.PLAY_AREA_HEIGHT;
import static de.drachenpapa.drak.game.logic.GameEngine.PLAY_AREA_WIDTH;

/**
 * Handles collision detection and collision-related actions for all players.
 * Checks for self-collisions, collisions with other curves, and manages collision consequences.
 */
class CollisionManager {

    private static final int CURVE_WIDTH = 3;
    private static final int SELF_COLLISION_SKIP = 10;

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

        if (x >= PLAY_AREA_WIDTH) {
            curve.setXPosition(0);
            curve.setPreviousXPosition(0);
        } else if (x < 0) {
            curve.setXPosition(PLAY_AREA_WIDTH - 1);
            curve.setPreviousXPosition(PLAY_AREA_WIDTH - 1);
        }

        if (y >= PLAY_AREA_HEIGHT) {
            curve.setYPosition(0);
            curve.setPreviousYPosition(0);
        } else if (y < 0) {
            curve.setYPosition(PLAY_AREA_HEIGHT - 1);
            curve.setPreviousYPosition(PLAY_AREA_HEIGHT - 1);
        }
    }

    private boolean detectCurveCollision(Curve curve) {
        int x = curve.getXPosition();
        int y = curve.getYPosition();

        if (isOutOfBounds(x, y) || isSelfCollision(curve, x, y) || isOtherCurveCollision(x, y)) {
            return true;
        }

        curvePoints.add(new Point[]{new Point(x, y)});
        return false;
    }

    private boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= PLAY_AREA_WIDTH || y < 0 || y >= PLAY_AREA_HEIGHT;
    }

    private boolean isSelfCollision(Curve curve, int x, int y) {
        List<Point> points = curve.getPoints();
        int len = points.size();

        return points.stream()
                .limit(Math.max(0, len - SELF_COLLISION_SKIP))
                .anyMatch(p -> isPointCollision(p, x, y));
    }

    private boolean isOtherCurveCollision(int x, int y) {
        return curvePoints.stream()
                .flatMap(Arrays::stream)
                .anyMatch(p -> isPointCollision(p, x, y));
    }

    private boolean isPointCollision(Point p, int x, int y) {
        if (Math.abs(p.x - x) <= CURVE_WIDTH && Math.abs(p.y - y) <= CURVE_WIDTH) {
            double dist = Math.hypot(p.x - x, p.y - y);
            return dist < CURVE_WIDTH;
        }

        return false;
    }
}
