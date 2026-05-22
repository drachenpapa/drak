package de.drachenpapa.drak.game.view;

import de.drachenpapa.drak.game.config.DisplaySettings;
import de.drachenpapa.drak.game.config.RenderSettings;
import de.drachenpapa.drak.game.config.UiSettings;
import de.drachenpapa.drak.game.logic.Curve;
import de.drachenpapa.drak.game.logic.GameState;
import de.drachenpapa.drak.game.logic.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

/**
 * Responsible for all rendering operations in the game.
 * Draws the game field, player curves, scores, and statistics.
 */
public class GameRenderer {

    private final Font scoreFont = new Font(Font.SANS_SERIF, Font.BOLD, UiSettings.SCORE_FONT_SIZE);
    private final Font finalTitleFont = new Font(Font.SANS_SERIF, Font.BOLD, UiSettings.FINAL_TITLE_FONT_SIZE);
    private final Font finalRowFont = new Font(Font.SANS_SERIF, Font.BOLD, UiSettings.FINAL_ROW_FONT_SIZE);

    public void drawGame(Graphics g, Image gameFieldImage, List<Player> players, GameState state) {
        g.drawImage(gameFieldImage, 0, 0, null);
        switch (state) {
            case GAME_OVER -> drawFinalStatistics(g, players);
            case STARTED, RUNNING, PAUSED, READY_FOR_NEXT_ROUND -> drawScores(g, players);
        }
    }

    public void drawGameField(Graphics g) {
        g.setColor(RenderSettings.GAME_FIELD_BACKGROUND);
        g.fillRect(0, 0, DisplaySettings.PLAY_AREA_WIDTH, DisplaySettings.PLAY_AREA_HEIGHT);
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

    public void clearGameField(BufferedImage gameFieldImage) {
        Objects.requireNonNull(gameFieldImage, "gameFieldImage must not be null");
        Graphics2D g2 = gameFieldImage.createGraphics();
        try {
            g2.setColor(RenderSettings.GAME_FIELD_BACKGROUND);
            g2.fillRect(0, 0, gameFieldImage.getWidth(), gameFieldImage.getHeight());
        } finally {
            g2.dispose();
        }
    }

    public void drawScores(Graphics g, List<Player> players) {
        g.setColor(RenderSettings.SCORE_PANEL_BACKGROUND);
        g.fillRect(DisplaySettings.SCORE_PANEL_X, 0, DisplaySettings.SCORE_PANEL_WIDTH, DisplaySettings.WINDOW_HEIGHT);

        for (int i = 0; i < players.size(); i++) {
            drawScoreRow(g, players.get(i), i);
        }
    }

    public void drawFinalStatistics(Graphics g, List<Player> players) {
        g.setColor(RenderSettings.FINAL_SCREEN_BACKGROUND);
        g.fillRect(0, 0, DisplaySettings.WINDOW_WIDTH, DisplaySettings.WINDOW_HEIGHT);
        g.setColor(RenderSettings.FINAL_SCREEN_TEXT);
        g.setFont(finalTitleFont);
        g.drawString(UiSettings.FINAL_SCORES_TITLE, UiSettings.FINAL_TITLE_X, UiSettings.FINAL_TITLE_Y);

        for (int i = 0; i < players.size(); i++) {
            drawFinalScoreRow(g, players.get(i), i);
        }
    }

    private void drawScoreRow(Graphics g, Player player, int rowIndex) {
        g.setColor(player.getColor());
        g.setFont(scoreFont);
        var baseY = UiSettings.SCORE_NAME_BASE_Y + (rowIndex * UiSettings.SCORE_ROW_OFFSET_Y);
        g.drawString(player.getPlayerName(), UiSettings.SCORE_NAME_X, baseY);
        g.drawString(player.getScore() + UiSettings.SCORE_SUFFIX, UiSettings.SCORE_NAME_X, baseY + UiSettings.SCORE_POINTS_OFFSET_Y);
    }

    private void drawFinalScoreRow(Graphics g, Player player, int rowIndex) {
        g.setColor(player.getColor());
        g.setFont(finalRowFont);
        g.drawString(
            player.getPlayerName(),
            UiSettings.FINAL_ROW_NAME_X,
            UiSettings.FINAL_ROW_BASE_Y + (rowIndex * UiSettings.FINAL_ROW_OFFSET_Y)
        );
        g.drawString(
            player.getScore() + UiSettings.SCORE_SUFFIX,
            UiSettings.FINAL_ROW_POINTS_X,
            UiSettings.FINAL_ROW_BASE_Y + (rowIndex * UiSettings.FINAL_ROW_OFFSET_Y)
        );
    }
}
