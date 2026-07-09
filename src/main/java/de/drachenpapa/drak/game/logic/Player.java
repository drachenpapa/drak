package de.drachenpapa.drak.game.logic;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Random;

/**
 * Represents a player in the game.
 * Holds name, color, control keys, score, and current state.
 * Manages the player's curve and input status during gameplay.
 */
public class Player {

    private final Random random = new Random();

    @Getter
    private final String playerName;
    @Getter
    private final Color color;
    @Getter
    private final char leftKey;
    @Getter
    private final char rightKey;
    @Getter
    @Setter
    private Curve curve;
    @Getter
    @Setter
    private boolean leftKeyPressed = false;
    @Getter
    @Setter
    private boolean rightKeyPressed = false;
    @Getter
    @Setter
    private int score = 0;
    @Getter
    @Setter
    private boolean alive = true;

    public Player(String playerName, Color color, char leftKey, char rightKey) {
        this.playerName = playerName;
        this.color = color;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.curve = createNewCurve();
    }

    void increaseScore() {
        this.score++;
    }

    void resetCurve() {
        this.curve = createNewCurve();
    }

    private Curve createNewCurve() {
        int xPosition = 100 + random.nextInt(GameEngine.PLAY_AREA_WIDTH - 200);
        int yPosition = 100 + random.nextInt(GameEngine.PLAY_AREA_HEIGHT - 200);
        double direction = random.nextDouble() * 360;
        int gapInterval = random.nextInt(10) + 1;

        return new Curve(xPosition, yPosition, direction, gapInterval);
    }
}
