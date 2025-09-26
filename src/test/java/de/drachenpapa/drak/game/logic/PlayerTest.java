package de.drachenpapa.drak.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class PlayerTest {

    private Player player;
    private Curve newCurve;

    @BeforeEach
    public void setUp() {
        player = new Player("Player 1", Color.RED, '1', 'q');
        newCurve = new Curve(200, 300, 90, 5);
    }

    @Test
    public void testInitialPlayerName() {
        assertThat("Player name should match the expected value", player.getPlayerName(), is("Player 1"));
    }

    @Test
    public void testInitialColor() {
        assertThat("Player color should match the expected value", player.getColor(), is(Color.RED));
    }

    @Test
    public void testInitialCurve() {
        assertThat("Player's curve should not be null upon initialization", player.getCurve(), is(notNullValue()));
    }

    @Test
    public void testSetCurve() {
        player.setCurve(newCurve);
        assertThat("Player's curve should match the newly set curve", player.getCurve(), is(newCurve));
    }

    @Test
    public void testLeftKeyPressed() {
        assertThat("Left key should not be pressed initially", player.isLeftKeyPressed(), is(false));
        player.setLeftKeyPressed(true);
        assertThat("Left key should be pressed after being set", player.isLeftKeyPressed(), is(true));
    }

    @Test
    public void testRightKeyPressed() {
        assertThat("Right key should not be pressed initially", player.isRightKeyPressed(), is(false));
        player.setRightKeyPressed(true);
        assertThat("Right key should be pressed after being set", player.isRightKeyPressed(), is(true));
    }

    @Test
    public void testIncreaseScore() {
        int initialScore = player.getScore();
        player.increaseScore();
        assertThat("Player's score should increase by 1 after calling increaseScore", player.getScore(), is(initialScore + 1));
    }
}
