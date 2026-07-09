# Architecture

## 1. Project Overview

**Drak** is a desktop multiplayer game written in Java 21 using Swing. It is a remake of the classic *Zatacka* / *Achtung, die Kurve!* where each player steers a continuously moving curve and tries to outlast opponents by avoiding collisions. The project is feature-complete and intended for maintenance only.

- **Language**: Java 21
- **UI framework**: Swing + [FlatLaf](https://www.formdev.com/flatlaf/) (look and feel)
- **Build tool**: Maven
- **Entry point**: `de.drachenpapa.drak.Drak`

---

## 2. Main Responsibilities

1. **Settings UI** – Let players configure names, colors, and control keys before starting a game.
2. **Game loop** – Drive player movement, gap generation, and collision detection at a fixed tick rate.
3. **Collision detection** – Detect self-collisions, curve-to-curve collisions, and handle boundary wrapping.
4. **Rendering** – Draw the game field, player curves, scores, and end screens onto a Swing panel.
5. **Input handling** – Translate keyboard events into per-player left/right turn actions.
6. **State management** – Track and transition between game states (running, paused, next round, game over).

---

## 3. High-Level Architecture

```
┌──────────────────────────────────────────────────────────┐
│  Drak.main()                                             │
│    └── SettingsUI (JFrame)                               │
│          Configures players and speed, then starts game  │
└──────────────────────┬───────────────────────────────────┘
                       │ new GameEngine(players, speed)
                       ▼
┌──────────────────────────────────────────────────────────┐
│  GameEngine                                              │
│  ┌───────────────┐  ┌──────────────────┐                 │
│  │ PlayerManager │  │ GameStateManager │                 │
│  │  Player[]     │  │  GameState enum  │                 │
│  │  Curve        │  └──────────────────┘                 │
│  └───────────────┘                                       │
│  ┌────────────────────┐  ┌──────────────┐                │
│  │ CollisionManager   │  │ GameRenderer │                │
│  │ (reads curvePoints │  │ (draws to    │                │
│  │  from PlayerMgr)   │  │  Graphics/   │                │
│  └────────────────────┘  │  BufferedImg)│                │
│                           └──────────────┘                │
│  ┌──────────────────────────────────────┐                 │
│  │ GameWindow (JFrame)                  │                 │
│  │   └── GamePanel (JPanel/paintComp.)  │                 │
│  │         └── InputHandler (KeyAdapter)│                 │
│  └──────────────────────────────────────┘                 │
│                                                           │
│  javax.swing.Timer drives the game loop                  │
└──────────────────────────────────────────────────────────┘
```

The settings screen and the game screen are separate `JFrame` windows. The settings window hides when a game starts and reappears automatically when the game ends, via an `onGameEnd` callback passed from `SettingsUI` to `GameEngine`.

---

## 4. Packages and Responsibilities

### `de.drachenpapa.drak` (root)

| Class  | Responsibility                                                           |
|--------|--------------------------------------------------------------------------|
| `Drak` | Main entry point. Creates `SettingsUI`. Holds the `GAME_TITLE` constant. |

### `de.drachenpapa.drak.settings`

Pre-game configuration UI. Responsible only for collecting player setup and handing it off to `GameEngine`. Has no knowledge of game rules.

| Class                 | Responsibility                                                                                                         |
|-----------------------|------------------------------------------------------------------------------------------------------------------------|
| `SettingsUI`          | Main settings window (`JFrame`). Orchestrates the other panels, handles button actions, creates `GameEngine` on start. |
| `PlayerSettingsPanel` | One row per player (checkbox, name, color, keys). Package-private.                                                     |
| `OptionsControlPanel` | Speed spinner and action buttons (Start, Load Defaults). Package-private.                                              |

### `de.drachenpapa.drak.game.logic`

All game rules and state. No Swing rendering code, except for `javax.swing.Timer` used for the game loop and the round-transition delay.

| Class              | Visibility      | Responsibility                                                                                                                                             |
|--------------------|-----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `GameEngine`       | `public`        | Central coordinator. Owns the `javax.swing.Timer` game loop. Delegates to all other logic components.                                                      |
| `Player`           | `public`        | Holds a player's name, color, control keys, score, alive-state, and current `Curve`.                                                                       |
| `PlayerManager`    | `public`        | Owns the player list and the shared `curvePoints` collection. Handles score updates and round resets.                                                      |
| `Curve`            | `public`        | Represents one player's trail. Handles movement (position, direction), gap generation, and stores all past positions.                                      |
| `CollisionManager` | package-private | Detects self-collision, curve-to-curve collision, and wraps curves at play-area boundaries. Writes non-collided points into the shared `curvePoints` list. |
| `GameStateManager` | `public`        | Manages the `GameState` enum value and handles round-transition timing (via a short `javax.swing.Timer`).                                                  |
| `GameState`        | `public`        | Enum: `STARTED`, `RUNNING`, `PAUSED`, `READY_FOR_NEXT_ROUND`, `GAME_OVER`.                                                                                 |
| `InputHandler`     | `public`        | `KeyAdapter` that maps key events to per-player turn flags; also handles Escape → quit.                                                                    |

### `de.drachenpapa.drak.game.view`

Swing UI for the running game. Depends on `game.logic` types but does not modify game state directly (except for a known design issue, see §12).

| Class                      | Visibility      | Responsibility                                                                                                        |
|----------------------------|-----------------|-----------------------------------------------------------------------------------------------------------------------|
| `GamePanel`                | `public`        | `JPanel` subclass. Overrides `paintComponent` to delegate all drawing to `GameRenderer`.                              |
| `GameRenderer`             | `public`        | Stateless drawing helper. Draws the game field image, player curves, score panel, start screen, and final statistics. |
| `GameWindow`               | package-private | Wraps `JFrame`. Configures the window, attaches `InputHandler`, hides the cursor.                                     |
| `GameWindowFactory`        | `public`        | Interface for creating `GameWindow`. Exists to allow tests to inject a mock window.                                   |
| `DefaultGameWindowFactory` | `public`        | Production implementation of `GameWindowFactory`.                                                                     |

---

## 5. Entry Points

### Application start

```
Drak.main(String[] args)
  └── new SettingsUI()   // opens the settings JFrame
```

### Game start (triggered by "Start Game" button)

```
SettingsUI.startGame()
  ├── isValidPlayerSetup()    // at least 2 enabled players, no empty names
  ├── generatePlayers()       // collects Player objects from UI panels
  ├── setVisible(false)       // hides the settings window
  └── new GameEngine(players, speed, onGameEnd)
        └── .startGame()     // sets state RUNNING, starts javax.swing.Timer
              onGameEnd = () -> setVisible(true)  // shown again when game ends
```

### Game loop tick (every `gameSpeed` ms, on the Swing EDT)

```
javax.swing.Timer fires
  ├── updateGameState()
  │     ├── updateCurvesAndDraw()   // collision check + draw curves onto BufferedImage
  │     └── updatePlayerMovements() // move + apply turn input
  ├── handleRoundTransition()       // triggers pause + reset if round ended
  └── gamePanel.repaint()
        └── GamePanel.paintComponent()
              └── GameRenderer.drawGame(...)
                    └── draws overlay based on GameState (switch expression)
```

---

## 6. Data Flow

### Startup

```
SettingsUI
  → reads PlayerSettingsPanel fields (name, color, left/right key)
  → creates List<Player>
  → passes List<Player> + speed to GameEngine constructor
```

### Per-tick game loop

```
InputHandler (key events, EDT)
  → sets player.leftKeyPressed / rightKeyPressed

javax.swing.Timer (EDT)
  → GameEngine.updateCurvesAndDraw()
      → CollisionManager.isCollisionDetected(curve)
          → CollisionManager.wrapCurveIfNeeded()   // mutates curve position
          → checks curve.getPoints()               // self-collision
          → checks PlayerManager.curvePoints       // other-curve collision
          → if no collision: appends point to curvePoints
      → if collision: CollisionManager.handleCollision()
          → player.setAlive(false)
          → PlayerManager.increasePointsForAlivePlayers()
          → GameStateManager.checkForGameEnd()
      → else: GameRenderer.drawPlayerCurve()       // draws onto BufferedImage
  → GameEngine.updatePlayerMovements()
      → curve.move()
      → curve.turnLeft() / turnRight() based on key flags

  → GameEngine.handleRoundTransition()
      → GameStateManager.handleRoundTransition(resetRound)   // if round ended

  → gamePanel.repaint()
      → GamePanel.paintComponent()
          → GameRenderer.drawGame()
              → draws BufferedImage onto panel Graphics
              → draws overlay (scores / start / final stats) via switch on GameState
```

### Round transition

```
GameStateManager.checkForGameEnd()
  → if winningScore reached: state = GAME_OVER
  → else if aliveCount <= 1: state = READY_FOR_NEXT_ROUND

GameStateManager.handleRoundTransition(resetRound)  // called from game loop
  → if READY_FOR_NEXT_ROUND:
      state = PAUSED
      new javax.swing.Timer(1000ms, ...) fires once:
        → resetRound.run()          // PlayerManager.resetForNextRound()
        → state = RUNNING
```

### Collision point storage

| Collection                    | Owner                                              | Purpose                                                                          |
|-------------------------------|----------------------------------------------------|----------------------------------------------------------------------------------|
| `Curve.points`                | Each `Curve`                                       | All past positions of that curve → used for **self-collision**                   |
| `PlayerManager.curvePoints`   | `PlayerManager` / shared ref in `CollisionManager` | Non-collided positions from **all** players → used for **other-curve collision** |

Both collections grow unboundedly during a round and are cleared in `PlayerManager.resetForNextRound()`.

---

## 7. External Dependencies

| Dependency                                           | Scope   | Purpose                                                                |
|------------------------------------------------------|---------|------------------------------------------------------------------------|
| `com.formdev:flatlaf`                                | runtime | Modern Swing look and feel                                             |
| `org.projectlombok:lombok`                           | compile | `@Getter`, `@Setter`, `@RequiredArgsConstructor` to reduce boilerplate |
| `org.junit.jupiter:junit-jupiter`                    | test    | Unit test framework                                                    |
| `org.mockito:mockito-core` + `mockito-junit-jupiter` | test    | Mocking in unit tests                                                  |
| `org.assertj:assertj-core`                           | test    | Fluent assertions                                                      |

No database, no network, no file I/O (other than loading `logo.png` from the classpath).

---

## 8. Configuration Approach

There is no external configuration file. All settings are configured at runtime through the `SettingsUI` before the game starts:

| Setting                 | UI control                                   | Passed to                                                             |
|-------------------------|----------------------------------------------|-----------------------------------------------------------------------|
| Player name             | `JTextField` in `PlayerSettingsPanel`        | `Player(name, ...)` constructor                                       |
| Player color            | Color picker button in `PlayerSettingsPanel` | `Player(..., color, ...)` constructor                                 |
| Left/right control keys | Key buttons in `PlayerSettingsPanel`         | `Player(..., leftKey, rightKey)` constructor                          |
| Game speed (1–5)        | `JSpinner` in `OptionsControlPanel`          | `GameEngine(players, speed)` → `gameSpeed = SPEED_INTERVAL_MS * (MAX_SPEED_LEVEL + 1 - speed)` ms/tick |

Constants that affect gameplay (window size, play area dimensions, curve step size, turn angle, gap durations, collision width) are defined as `static final` fields directly in their respective classes:

- `GameEngine`: `WINDOW_WIDTH`, `WINDOW_HEIGHT`, `PLAY_AREA_WIDTH`, `PLAY_AREA_HEIGHT`
- `Curve`: `MAX_GAP_INTERVAL`, `MIN_GAP_INTERVAL`, `MIN_GAP_LENGTH`, `MAX_GAP_LENGTH`, `STEP_SIZE`, `TURN_ANGLE`
- `CollisionManager`: `CURVE_WIDTH`, `SELF_COLLISION_SKIP`

Also, `GameEngine` exposes `MIN_SPEED_LEVEL`, `MAX_SPEED_LEVEL`, and `DEFAULT_SPEED_LEVEL` as public constants used by `SettingsUI` and `OptionsControlPanel`.

The winning score is derived automatically: `(playerCount - 1) * WINNING_SCORE_PER_PLAYER` (constant = 10). It is not user-configurable.

---

## 9. Error Handling Approach

Error handling is minimal and appropriate for a desktop game with no network or persistence layer:

- **Logo loading failures** are silently swallowed (`catch (Exception ignored)`) in both `SettingsUI` and `GameWindow`. The game continues without an icon.
- **FlatLaf initialization failure** is silently caught (`catch (Exception ignored)`) with a comment explaining the fallback. The default Swing look and feel is used instead.
- There is no global exception handler. Uncaught exceptions would surface as default JVM stack traces on stderr.
- Basic player setup validation (`isValidPlayerSetup()`) is performed before starting the game: at least two players must be enabled and no name may be empty.

---

## 10. Testing Approach

Tests cover the entire `game.logic` package. There are no tests for `game.view` or `settings`.

### Test structure

All tests use JUnit 5 (`@Nested` + `@DisplayName`) for readable output, Mockito for mocking dependencies, and AssertJ for assertions.

| Test class             | What is tested                                                                       |
|------------------------|--------------------------------------------------------------------------------------|
| `CurveTest`            | Movement, direction changes, point tracking, gap activation/deactivation             |
| `PlayerTest`           | Initial state, score increment, key flags, curve replacement                         |
| `PlayerManagerTest`    | Alive count, round reset, score increase for alive players                           |
| `CollisionManagerTest` | Boundary wrapping, self-collision, other-curve collision, collision consequence      |
| `GameStateManagerTest` | State transitions on win condition, alive count, round transition timing             |
| `InputHandlerTest`     | Key press/release routing to players, Escape → quit                                  |
| `GameEngineTest`       | `startGame`, `quitGame`, `handleRoundTransition` delegating to mocked sub-components |

### Testability enablers in the code

- **`GameWindowFactory` interface**: Allows `GameEngineTest` to inject a mock `GameWindow` so no real Swing frame is created during tests.
- **Package-private constructors**: `Curve`, `CollisionManager`, `GameStateManager`, `PlayerManager`, `GameWindow` all have package-private constructors, allowing tests in the same package to instantiate them without exposing them publicly.
- **`GameEngine` test constructor**: A package-private 5-argument constructor accepts pre-built sub-components (including mocked `GameStateManager` and `GamePanel`) directly, bypassing the normal constructor wiring. This removes the need for reflection utilities in `GameEngineTest`.

### What is not tested

- `GameRenderer` (stateless, but drawing logic is untested)
- `GamePanel` / `GameWindow` (Swing components; requires a display)
- `SettingsUI` / `PlayerSettingsPanel` / `OptionsControlPanel` (Swing UI)
- The game loop timing itself

---

## 11. Important Architectural Decisions

### Why `GameWindowFactory`?

`GameEngine`'s constructor creates all internal components including the `GameWindow` (a real `JFrame`). `GameWindowFactory` is the seam that allows tests to pass a mock factory, preventing the test suite from opening real windows on a headless CI runner.

### Why package-private for most of `game.logic`?

`CollisionManager`, `GameState`, `Curve`'s constructor, `GameStateManager`'s constructor etc. are package-private. This intentionally limits their surface area: only `GameEngine` and its peers within `game.logic` can instantiate or reference them directly. `PlayerManager` and `Player` are public because the `game.view` package needs to read player data for rendering.

### Shared mutable `curvePoints` reference

`PlayerManager.curvePoints` is passed by reference into `CollisionManager`. The collision manager both reads from and writes to this list during collision detection. This is a deliberate performance trade-off: no copying, but both classes are tightly coupled through a shared mutable list.

### Two separate point collections

Self-collision and other-curve collision are handled with different data sources (`Curve.points` and `PlayerManager.curvePoints` respectively). This allows skipping the most recent points of a curve for self-collision (`SELF_COLLISION_SKIP = 10`) without affecting other-curve detection.

### Game loop on the Swing EDT

The `javax.swing.Timer` in `GameEngine.startGame()` fires on the Event Dispatch Thread. This is correct for Swing: `repaint()` and all UI mutations happen on the same thread, avoiding threading issues.

### Why `onGameEnd` callback?

`SettingsUI` passes a `Runnable` (`onGameEnd`) to `GameEngine`. When the game window is closed (via `quitGame()`), this callback restores the settings window. This keeps lifecycle management decoupled: `GameEngine` has no reference to `SettingsUI` — it only knows it should call a callback on exit. The callback pattern avoids a direct reverse dependency from `game.logic` back into `settings`.

### `drawGame()` uses a switch expression

`GameRenderer.drawGame()` uses a Java 21 `switch` expression over the `GameState` enum. Unlike an `if/else` chain, the compiler enforces exhaustiveness — if a new `GameState` value is added, the switch will produce a compilation error until it is handled.

### Window dimensions as constants in `GameEngine`

`WINDOW_WIDTH`, `WINDOW_HEIGHT`, `PLAY_AREA_WIDTH`, and `PLAY_AREA_HEIGHT` are defined in `GameEngine` and referenced from `GameRenderer`, `GamePanel`, and `CollisionManager`. This makes `GameEngine` a central source for layout constants, which is a pragmatic choice for a fixed-size game.

---

## 12. Known Architectural Limitations and Technical Debt

### Unbounded point list growth

Both `Curve.points` and `PlayerManager.curvePoints` grow without bound during a round. At the maximum speed setting (`gameSpeed = 10 ms`), a curve adds 100 points per second. Self-collision detection iterates over all of these on every tick. For typical game durations this is not a problem, but it is a potential bottleneck for very long rounds.

---

## 13. What Should Remain Simple

- **No persistence layer.** There are no save files, high scores, or preferences to persist. Do not add one unless there is a concrete reason.
- **No networking.** The game is local multiplayer only. Do not add a networking layer.
- **No dependency injection framework.** `GameEngine`'s constructor wires all components. This is appropriate for the project size; a DI framework would add complexity without benefit.
- **No additional abstraction layers.** The current `logic` / `view` / `settings` split is sufficient. Do not introduce service layers, repositories, or event buses.
- **No plugin or extension system.** The project is feature-complete. Speculative extensibility would only add complexity.
- **Minimal dependencies.** The runtime dependency footprint is intentionally small (FlatLaf + Lombok). Keep it that way.
