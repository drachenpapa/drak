package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@DisplayName("CollisionManager")
@ExtendWith(MockitoExtension.class)
class CollisionManagerTest {

    @Mock
    private PlayerManager playerManager;

    private List<Player> players;
    private List<Point[]> curvePoints;
    private CollisionManager collisionManager;

    @BeforeEach
    void setUp() {
        players = List.of(
                new Player("Player 1", Color.RED, '1', 'q'),
                new Player("Player 2", Color.GREEN, 'y', 'x'));
        players.get(0).setAlive(true);
        players.get(1).setAlive(true);
        curvePoints = new ArrayList<>();
        collisionManager = new CollisionManager(players, curvePoints, playerManager);
    }

    @Nested
    @DisplayName("handleCollision()")
    class HandleCollision {

        @Test
        @DisplayName("marks player as dead and increases points for alive players")
        void marksPlayerDeadAndIncreasesPoints() {
            collisionManager.handleCollision(0);

            assertThat(players.getFirst().isAlive()).isFalse();
            verify(playerManager).increasePointsForAlivePlayers();
        }
    }

    @Nested
    @DisplayName("isCollisionDetected()")
    class IsCollisionDetected {

        @Test
        @DisplayName("wraps x-position and returns false when curve exits play area")
        void wrapsPositionAndReturnsFalseOnBoundaryExit() {
            Curve curve = new Curve(700, 10, 0, 1000);

            boolean collision = collisionManager.isCollisionDetected(curve);

            assertThat(collision).isFalse();
            assertThat(curve.getXPosition()).isEqualTo(0);
        }

        @Test
        @DisplayName("returns true on self-collision")
        void returnsTrueOnSelfCollision() {
            Curve curve = new Curve(10, 10, 0, 1000);
            curve.addPoint(10, 10);
            for (int i = 0; i < 11; i++) {
                curve.addPoint(10, 10);
            }

            boolean collision = collisionManager.isCollisionDetected(curve);

            assertThat(collision).isTrue();
        }

        @Test
        @DisplayName("returns true when hitting another curve")
        void returnsTrueOnOtherCurveCollision() {
            Curve curve = new Curve(20, 20, 0, 1000);
            curvePoints.add(new Point[]{new Point(20, 20)});

            boolean collision = collisionManager.isCollisionDetected(curve);

            assertThat(collision).isTrue();
        }
    }
}
