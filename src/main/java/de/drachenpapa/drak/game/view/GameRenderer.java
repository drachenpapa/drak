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

    private static final int SCORE_PANEL_X = 682;
    private static final int SCORE_TEXT_X = 690;
    private static final int SCORE_FIRST_NAME_Y = 30;
    private static final int SCORE_FIRST_PTS_Y = 50;
    private static final int SCORE_ROW_HEIGHT = 50;
    private static final int STATS_TITLE_X = 200;
    private static final int STATS_TITLE_Y = 100;
    private static final int STATS_FIRST_ROW_Y = 175;
    private static final int STATS_ROW_HEIGHT = 75;
    private static final int STATS_SCORE_X = 500;

    public void drawGame(Graphics g, Image gameFieldImage, List<Player> players, GameState state) {
        g.drawImage(gameFieldImage, 0, 0, null);
        switch (state) {
            case GAME_OVER -> drawFinalStatistics(g, players);
            case STARTED -> drawStartScreen(g, players);
            case RUNNING, PAUSED -> drawScores(g, players);
            case READY_FOR_NEXT_ROUND -> { /* brief intermediate state — no overlay needed */ }
        }
    }

    void drawGameField(Graphics g) {
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
        g.fillRect(SCORE_PANEL_X, 0, GameEngine.WINDOW_WIDTH - SCORE_PANEL_X, GameEngine.WINDOW_HEIGHT);

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            g.setColor(player.getColor());
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            g.drawString(player.getPlayerName(), SCORE_TEXT_X, SCORE_FIRST_NAME_Y + (i * SCORE_ROW_HEIGHT));
            g.drawString(player.getScore() + " pts", SCORE_TEXT_X, SCORE_FIRST_PTS_Y + (i * SCORE_ROW_HEIGHT));
        }
    }

    void drawFinalStatistics(Graphics g, List<Player> players) {
        g.setColor(Color.black);
        g.fillRect(0, 0, GameEngine.WINDOW_WIDTH, GameEngine.WINDOW_HEIGHT);
        g.setColor(Color.white);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 72));
        g.drawString("Final Scores", STATS_TITLE_X, STATS_TITLE_Y);

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            g.setColor(player.getColor());
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
            g.drawString(player.getPlayerName(), STATS_TITLE_X, STATS_FIRST_ROW_Y + (i * STATS_ROW_HEIGHT));
            g.drawString(player.getScore() + " pts", STATS_SCORE_X, STATS_FIRST_ROW_Y + (i * STATS_ROW_HEIGHT));
        }
    }
}
