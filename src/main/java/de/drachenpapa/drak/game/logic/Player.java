package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.game.config.BalanceSettings;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * Represents a player in the game.
 * Holds name, color, control keys, score, and current state.
 * Manages the player's curve and input status during gameplay.
 */
public class Player {

    @Getter
    private final String playerName;
    @Getter
    private final Color color;
    @Getter
    private final char leftKey;
    @Getter
    private final char rightKey;
    private final RandomGenerator curveRandomGenerator;
    @Getter
    private int score = 0;
    @Getter
    private Curve curve;
    private final int tickIntervalMs;
    @Setter
    @Getter
    private boolean leftKeyPressed = false;
    @Getter
    @Setter
    private boolean rightKeyPressed = false;
    @Getter
    private boolean alive = true;

    public Player(String playerName, Color color, char leftKey, char rightKey) {
        this(playerName, color, leftKey, rightKey, BalanceSettings.DEFAULT_TICK_INTERVAL_MS, RandomGenerator.getDefault());
    }

    Player(String playerName, Color color, char leftKey, char rightKey, RandomGenerator curveRandomGenerator) {
        this(playerName, color, leftKey, rightKey, BalanceSettings.DEFAULT_TICK_INTERVAL_MS, curveRandomGenerator);
    }

    Player(String playerName, Color color, char leftKey, char rightKey, int tickIntervalMs, RandomGenerator curveRandomGenerator) {
        this.playerName = playerName;
        this.color = color;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.tickIntervalMs = tickIntervalMs;
        this.curveRandomGenerator = Objects.requireNonNull(curveRandomGenerator, "curveRandomGenerator");
        this.curve = createNewCurve();
    }


    void increaseScore() {
        score++;
    }


    void kill() {
        this.alive = false;
    }

    void revive() {
        this.alive = true;
    }

    void setCurve(Curve curve) {
        this.curve = Objects.requireNonNull(curve, "curve");
    }

    void resetCurve() {
        curve = createNewCurve();
    }

    private Curve createNewCurve() {
        return CurveFactory.createRandomCurve(curveRandomGenerator, tickIntervalMs);
    }
}
