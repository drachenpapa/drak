package de.drachenpapa.drak.game.view;

import de.drachenpapa.drak.game.logic.Curve;
import de.drachenpapa.drak.game.logic.GameEngine;
import de.drachenpapa.drak.game.logic.GameState;
import de.drachenpapa.drak.game.logic.Player;

import java.awt.*;
import java.util.List;

/**
 * Responsible for all rendering operations in the game.
 * Draws the game field, player curves, scores, and statistics.
 */
public class GameRenderer {

    public void drawGame(Graphics g, Image gameFieldImage, List<Player> players, GameState state, Runnable handleRoundTransition) {
        g.drawImage(gameFieldImage, 0, 0, null);
        handleRoundTransition.run();
        if (state == GameState.GAME_OVER) {
            drawFinalStatistics(g, players);
        } else if (state == GameState.STARTED) {
            drawStartScreen(g, players);
        } else if (state == GameState.RUNNING || state == GameState.PAUSED) {
            drawScores(g, players);
        }
    }

    public void drawGameField(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, GameEngine.PLAY_AREA_WIDTH, GameEngine.PLAY_AREA_HEIGHT);
    }

    public void drawPlayerCurve(Graphics g, Curve curve, Color color) {
        g.setColor(color);
        g.drawLine(
                curve.getPreviousXPosition(),
                curve.getPreviousYPosition(),
                curve.getXPosition(),
                curve.getYPosition()
        );
    }

    public void drawScorePanel(Graphics g, List<Player> players, Runnable checkForGameEnd) {
        drawScores(g, players);
        checkForGameEnd.run();
    }

    public void clearGameField(Image gameFieldImage) {
        Graphics2D g2 = (Graphics2D) gameFieldImage.getGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, GameEngine.PLAY_AREA_WIDTH, GameEngine.PLAY_AREA_HEIGHT);
        g2.dispose();
    }

    void drawStartScreen(Graphics g, List<Player> players) {
        drawGameField(g);
        drawScores(g, players);
    }

    void drawScores(Graphics g, List<Player> players) {
        g.setColor(Color.gray);
        g.fillRect(682, 0, 118, GameEngine.WINDOW_HEIGHT);

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            g.setColor(player.getColor());
            g.setFont(new Font("SANS_SERIF", Font.BOLD, 16));
            g.drawString(player.getPlayerName(), 690, 30 + (i * 50));
            g.drawString(player.getScore() + " pts", 690, 50 + (i * 50));
        }
    }

    void drawFinalStatistics(Graphics g, List<Player> players) {
        g.setColor(Color.black);
        g.fillRect(0, 0, GameEngine.WINDOW_WIDTH, GameEngine.WINDOW_HEIGHT);
        g.setColor(Color.white);
        g.setFont(new Font("SANS_SERIF", Font.BOLD, 72));
        g.drawString("Final Scores", 200, 100);

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            g.setColor(player.getColor());
            g.setFont(new Font("SANS_SERIF", Font.BOLD, 36));
            g.drawString(player.getPlayerName(), 200, 175 + (i * 75));
            g.drawString(player.getScore() + " pts", 500, 175 + (i * 75));
        }
    }
}
