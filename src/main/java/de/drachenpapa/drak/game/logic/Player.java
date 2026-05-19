package de.drachenpapa.drak.game.logic;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
    private final AtomicInteger score = new AtomicInteger(0);
    private final AtomicReference<Curve> curve = new AtomicReference<>();
    private final RandomGenerator curveRandomGenerator;
    @Getter
    @Setter
    private volatile boolean leftKeyPressed = false;
    @Getter
    @Setter
    private volatile boolean rightKeyPressed = false;
    @Getter
    @Setter
    private volatile boolean alive = true;

    public Player(String playerName, Color color, char leftKey, char rightKey) {
        this(playerName, color, leftKey, rightKey, RandomGenerator.getDefault());
    }

    Player(String playerName, Color color, char leftKey, char rightKey, RandomGenerator curveRandomGenerator) {
        this.playerName = playerName;
        this.color = color;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.curveRandomGenerator = Objects.requireNonNull(curveRandomGenerator, "curveRandomGenerator");
        this.curve.set(createNewCurve());
    }

    void increaseScore() {
        score.incrementAndGet();
    }

    public int getScore() {
        return score.get();
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public Curve getCurve() {
        return curve.get();
    }

    public void setCurve(Curve curve) {
        this.curve.set(Objects.requireNonNull(curve, "curve"));
    }

    void resetCurve() {
        curve.set(createNewCurve());
    }

    private Curve createNewCurve() {
        return CurveFactory.createRandomCurve(curveRandomGenerator);
    }
}
