package de.drachenpapa.drak.game.logic;

import de.drachenpapa.drak.TestReflectionUtils;
import de.drachenpapa.drak.game.view.GameWindow;
import de.drachenpapa.drak.game.view.GameWindowFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("GameEngine Integration")
class GameEngineIT {

    @Test
    @DisplayName("does not trigger immediate chain scoring on first update tick")
    void doesNotTriggerImmediateChainScoringOnFirstUpdateTick() {
        GameConfig config = new GameConfig(3, List.of(
            new PlayerConfig("P1", Color.RED, 'a', 's'),
            new PlayerConfig("P2", Color.GREEN, 'k', 'l'),
            new PlayerConfig("P3", Color.BLUE, 'n', 'm')
        ));

        GameWindowFactory windowFactory = (panel, engine) -> mock(GameWindow.class);
        GameEngine engine = new GameEngine(config, windowFactory);

        List<Player> players = engine.getPlayers();
        players.get(0).setCurve(new Curve(100, 100, 0, Long.MAX_VALUE));
        players.get(1).setCurve(new Curve(200, 100, 0, Long.MAX_VALUE));
        players.get(2).setCurve(new Curve(300, 100, 0, Long.MAX_VALUE));

        GameStateManager stateManager = (GameStateManager) TestReflectionUtils.getField(engine, "gameStateManager");
        stateManager.setGameState(GameState.RUNNING);

        TestReflectionUtils.invokeMethod(engine, "updateGameState");

        assertThat(players)
            .extracting(Player::isAlive)
            .containsOnly(true);
        assertThat(players)
            .extracting(Player::getScore)
            .containsExactly(0, 0, 0);
    }
}
