# Conventions

This document describes the conventions used in this project. Sections marked **[existing]** reflect patterns already established in the codebase. Sections marked **[recommended]** propose consistent rules where the code is silent or slightly inconsistent — these do not contradict any existing code.

---

## 1. General Coding Principles

**[existing]**

- Prefer clear, readable code over clever code.
- Keep methods short and focused on a single responsibility.
- Use the standard library before reaching for a dependency.
- Prefer explicit over implicit — avoid hidden side effects in methods that sound purely informational (e.g., draw methods should only draw).
- Package-private visibility is the default for internal classes; only promote to `public` when genuinely needed outside the package.

---

## 2. Project Structure Conventions

**[existing]**

```
src/
  main/
    java/de/drachenpapa/drak/
      Drak.java                  ← entry point only
      settings/                  ← pre-game configuration UI
      game/
        logic/                   ← all game rules, no Swing rendering
        view/                    ← all Swing rendering and windowing
    resources/
      logo.png
  test/
    java/de/drachenpapa/drak/
      game/logic/                ← mirrors main structure
docs/
scripts/
original-src/
```

**Rules:**
- `game.logic` must not depend on `game.view` (the dependency flows the other way).
- `settings` may depend on `game.logic` (to create `GameEngine`) but not on `game.view`.
- New game-rule code goes in `game.logic`. New rendering code goes in `game.view`.
- Tests mirror the package structure of the class under test and live in the same package (different source root) to access package-private members.

---

## 3. Naming Conventions

**[existing]**

| Element                    | Convention                               | Example                                |
|----------------------------|------------------------------------------|----------------------------------------|
| Classes                    | `PascalCase`                             | `GameEngine`, `PlayerManager`          |
| Interfaces                 | `PascalCase`                             | `GameWindowFactory`                    |
| Enums                      | `PascalCase`                             | `GameState`                            |
| Enum values                | `UPPER_SNAKE_CASE`                       | `READY_FOR_NEXT_ROUND`                 |
| Methods                    | `camelCase`, verb or verb-phrase         | `startGame()`, `isCollisionDetected()` |
| Fields                     | `camelCase`                              | `gameSpeed`, `curvePoints`             |
| Constants (`static final`) | `UPPER_SNAKE_CASE`                       | `PLAY_AREA_WIDTH`, `MAX_GAP_INTERVAL`  |
| Packages                   | lowercase, no underscores                | `de.drachenpapa.drak.game.logic`       |
| Test classes               | `<ClassName>Test`                        | `CollisionManagerTest`                 |
| `@Nested` test classes     | `PascalCase`, noun form of the method    | `HandleRoundTransition`, `QuitGame`    |
| Test methods               | `camelCase`, describes expected behavior | `setsGameOverAndClosesWindow`          |

**[recommended]**

- Boolean fields and methods should start with `is`, `has`, or `can`: `isAlive`, `isGapActive`, `isCollisionDetected()`.
- Factory methods should start with `create`: `createNewCurve()`. *(Already used in `Player`.)*
- Avoid abbreviations except for universally understood ones (`g` for `Graphics`, `g2` for `Graphics2D`).

---

## 4. Formatting and Style Conventions

**[existing]** — enforced via `.editorconfig`

- **Indentation**: 4 spaces (no tabs) for Java files.
- **Encoding**: UTF-8.
- **Line endings**: LF.
- **Trailing whitespace**: trimmed on save (except Markdown files).
- **Final newline**: every file ends with a newline.
- **Braces**: same-line opening brace (K&R style), consistent throughout the project.

**[existing]** — import style

- Use specific imports, not wildcard, for project and library classes.
- Wildcard imports (`import java.awt.*`) are acceptable for `java.awt` and `java.awt.event` (precedent already set in the codebase).
- Static imports are used for Mockito methods (`verify`, `when`, `any`) and AssertJ (`assertThat`) in tests.
- Use static imports for project constants where it improves readability (e.g., `import static de.drachenpapa.drak.game.logic.GameEngine.PLAY_AREA_WIDTH`).

**[recommended]**

- `javax.swing.Timer` should be imported normally rather than referenced by its fully qualified name inline.

---

## 5. Error Handling Conventions

**[existing]**

- Non-critical resource loads (e.g., icon from classpath) use `catch (Exception ignored)`. This is acceptable because the game is fully functional without the icon.
- Initialization failures that degrade-but-don't-break functionality (e.g., FlatLaf setup) use `catch (Exception ignored)` with a comment explaining the fallback:
  ```java
  } catch (Exception ignored) {
      // FlatLaf could not be initialized; Swing default look-and-feel is used instead
  }
  ```
- No custom exception classes exist; none are needed for the current scope.
- There is no global uncaught-exception handler.

**[recommended]**

- Do not swallow `Exception` silently for anything that could affect game behavior. Reserve `catch (Exception ignored)` for truly optional resources.
- When a `catch` block is intentionally empty or swallowing, add a short comment explaining why.
- Do not use exceptions for normal control flow.

---

## 6. Logging Conventions

**[existing]**

There is no logging framework in this project and no console output at runtime. All error conditions that are non-critical (missing icon, FlatLaf fallback) are silently caught with an explanatory comment.

**[recommended]**

- Do not introduce a logging framework (SLF4J, Log4j, etc.) unless there is a concrete operational need. The project has no server-side component, no deployment environment, and no log aggregation setup.
- Do not add `System.out.println(...)` or `System.err.println(...)` debug output; remove it before committing.

---

## 7. Testing Conventions

**[existing]**

- **Framework**: JUnit 5 (`junit-jupiter`). Use `@Test`, `@BeforeEach`, `@Nested`, `@DisplayName`.
- **Assertions**: AssertJ (`assertThat(...)`). Do not use JUnit's built-in `assertEquals` or Hamcrest matchers.
- **Mocking**: Mockito with `@ExtendWith(MockitoExtension.class)`, `@Mock`, `when(...)`, `verify(...)`.
- **Test structure**: group related tests with `@Nested`. Name the nested class after the method under test in noun/PascalCase form. Use `@DisplayName` on both the class and each test method.
  ```java
  @Nested
  @DisplayName("startGame()")
  class StartGame {
      @Test
      @DisplayName("sets game state to RUNNING")
      void setsGameStateToRunning() { ... }
  }
  ```
- **Setup**: use `@BeforeEach` with a method named `setUp()`. Nested classes may add their own `@BeforeEach` for further scoping (see `GameStateManagerTest`).
- **Test location**: test classes live in `src/test/java` under the same package as the class under test. This grants access to package-private members without reflection.
- **Naming**: test method names describe the expected behavior in camelCase, not the implementation.

**[recommended]**

- One logical assertion per test where practical. Multiple `assertThat(...)` calls are acceptable when they all verify a single behavior.
- Avoid `Thread.sleep()` in tests unless testing time-dependent behavior directly (already done in `CurveTest`). When used, keep the sleep duration as short as possible.
- Do not use `ReflectionTestUtils` or `java.lang.reflect` for new tests. Instead, expose a package-private constructor or setter to enable direct field injection within the package.
- Test behavior, not implementation. Avoid asserting on internal state unless it is directly relevant to the tested behavior.

---

## 8. Dependency Conventions

**[existing]**

- All dependency versions are declared as properties in `pom.xml` (e.g., `<flatlaf.version>`, `<lombok.version>`). Never hard-code version numbers directly in the `<dependency>` block.
- Runtime dependencies are kept minimal: FlatLaf and Lombok are the only non-JDK runtime dependencies.
- Test-scoped dependencies (`junit-jupiter`, `mockito-*`, `assertj-core`) must use `<scope>test</scope>`.
- Dependency updates are automated via Renovate. Do not manually bump versions unless Renovate is blocked or broken.
- GitHub Actions are pinned to commit SHAs with the version as a comment:
  ```yaml
  uses: actions/checkout@9c091bb21b7c1c1d1991bb908d89e4e9dddfe3e0 # v7
  ```

**[recommended]**

- Before adding a new dependency, verify:
  1. The standard library cannot solve the problem.
  2. The dependency is actively maintained.
  3. The added value outweighs the supply-chain and maintenance cost.

---

## 9. Configuration Conventions

**[existing]**

- There is no external configuration file (no `.properties`, `.yaml`, or `.json` config).
- All gameplay constants (`WINDOW_WIDTH`, `STEP_SIZE`, `MAX_GAP_INTERVAL`, etc.) are `static final` fields in the class most responsible for them.
- User-configurable settings (player names, colors, keys, game speed) are collected through the `SettingsUI` at startup and passed directly as constructor arguments. They are not persisted.
- The winning score formula `(playerCount - 1) * 10` is computed inline in `GameEngine`. It is not configurable by the user.

**[recommended]**

- Game-balance constants (step size, turn angle, gap lengths, collision width) should remain as named `static final` fields close to where they are used — do not centralize them into a config class unless there is a clear need.
- Do not add external configuration files or a preferences API unless persistence becomes a requirement.

---

## 10. Documentation Conventions

**[existing]**

- All public and package-private classes carry a short Javadoc comment describing their responsibility (3–4 lines). This is consistent across the entire codebase.
  ```java
  /**
   * Handles collision detection and collision-related actions for all players.
   * Checks for self-collisions, collisions with other curves, and manages collision consequences.
   */
  class CollisionManager {
  ```
- **No method-level Javadoc** is used — methods are expected to be self-documenting through clear naming.
- **No inline comments** for code that is already readable. Comments are reserved for non-obvious decisions.

**[recommended]**

- Class-level Javadoc is required for all new classes (public and package-private). Follow the existing pattern: one sentence on what the class is, one or two sentences on its main responsibilities.
- Avoid comments that restate the code (`// loop over players` above a for-each loop). Add a comment only when the *why* is not obvious from the *what*.
- When silently catching an exception, always add a comment explaining the intent (see §5).
- `docs/architecture.md` and `docs/conventions.md` should be updated when structural changes are made to the project.

---

## 11. Language-Specific Conventions (Java)

**[existing]**

- **Target version**: Java 21. Use modern Java features where they improve clarity.
- **Lombok**: used selectively for `@Getter`, `@Setter`, `@RequiredArgsConstructor`. Field-level annotations are preferred — annotate only the fields that actually need a getter or setter. Avoid class-level `@Getter @Setter` unless every field requires both.
- **Streams**: used for functional operations on collections (`filter`, `forEach`, `anyMatch`, `flatMap`). Prefer streams over manual loops when the intent is clearer.
- **`List.of()`**: used for immutable lists in tests. Use `ArrayList` when the list is mutated after creation.
- **`var`**: not currently used. **[recommended]** Use `var` for local variables where the type is obvious from the right-hand side:
  ```java
  var players = playerManager.getPlayers(); // List<Player> is clear from context
  ```
  Do not use `var` when the type is not immediately obvious.
- **`Math.random()`**: used throughout for random values. Acceptable for a game of this size. Do not replace with `ThreadLocalRandom` or `Random` unless needed.
- **Access modifiers**: prefer the most restrictive modifier that works. Default (package-private) is preferred over `protected`. Avoid `protected` entirely — there is no inheritance in this codebase.
- **`final`**: fields injected via constructor are declared `final`. Mutable fields are not. This pattern is already consistent in the codebase.
- **Checked exceptions**: none are thrown or declared. Keep it that way — the codebase does not warrant checked exceptions.

**[recommended]**

- Prefer `instanceof` pattern matching (Java 16+) if type checks are ever needed.
- Prefer text blocks for multi-line strings if they appear.
- Do not use raw types. Fully parameterize all generic types.

---

## 12. Framework-Specific Conventions (Swing)

**[existing]**

- **EDT safety**: all game-loop updates and UI mutations run on the Swing Event Dispatch Thread via `javax.swing.Timer`. Do not perform UI operations off the EDT.
- **`paintComponent` override**: only `GamePanel` overrides `paintComponent`. It always calls `super.paintComponent(g)` first.
- **`setPreferredSize` vs `setSize`**: `GamePanel` uses `setPreferredSize` (correct — lets the layout manager decide); `SettingsUI` uses `setSize` (acceptable for a fixed-size, non-resizable frame).
- **`setResizable(false)`**: both the settings window and the game window are non-resizable. Maintain this.
- **`setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)`**: used on both frames. The application exits when either window is closed.
- **FlatLaf**: initialized in `SettingsUI` before any UI component is created. Must remain the first UI operation in `main` flow.
- **`Graphics` disposal**: `Graphics2D` objects obtained from `BufferedImage.createGraphics()` or `Image.getGraphics()` are always disposed in a `try/finally`-equivalent pattern (currently via explicit `g2.dispose()` at the end of the method). Always dispose graphics contexts you create.

**[recommended]**

- Do not introduce additional `JFrame` instances. The settings window and the game window are sufficient.
- Do not use `KeyListener` directly on `JPanel`; attach it to the `JFrame` (as `GameWindow` already does). `JPanel` may not receive focus reliably.
- When creating a new `javax.swing.Timer`, add an import statement rather than using the fully qualified class name inline.

---

## 13. Things to Avoid in This Project

- **Do not add gameplay features.** The project is feature-complete. Core mechanic changes are out of scope (see `CONTRIBUTING.md`).
- **Do not add a persistence layer.** No save files, high scores, or user preferences. If this ever changes, open a discussion first.
- **Do not add networking.** The game is local multiplayer only.
- **Do not add a dependency injection framework** (Spring, Guice, etc.). The manual wiring in `GameEngine`'s constructor is appropriate for this size.
- **Do not add a logging framework** (SLF4J, Log4j, etc.). There is no operational environment that would benefit from structured logs.
- **Do not add a plugin or event-bus system.** Speculative extensibility adds complexity without benefit.
- **Do not use static mutable fields for shared state.** Use instance fields instead; static mutable state causes hard-to-diagnose bugs when instances are created more than once.
- **Do not embed game logic in rendering methods.** `GameRenderer` should only draw. State transitions and game-end checks belong in the game loop.
- **Do not use `protected` for visibility.** There is no inheritance in this codebase; use package-private instead.
- **Do not use reflection utilities for new tests.** Expose a package-private constructor or field instead.
- **Do not use magic numbers without named constants** for any value that affects gameplay, layout, or timing.
- **Do not commit debug output** (`System.out.println`, `System.err.println`).
