---
layout: default
---

## Gameplay

_Drak_ is a fast-paced multiplayer action game inspired by the classic _Zatacka_, also known as _Achtung, die Kurve!_.
This Java adaptation lets up to six players compete by steering their colored curves across the playfield.
The objective is to survive as long as possible without crashing into the curves of other players or their own.

### Game Rules

- Each player controls a continuously moving curve that leaves a trail behind.
- Each curve moves continuously and can only be steered left or right.
- Colliding with another curve or its own trail eliminates the player from the round.
- Random gaps occasionally appear in a curve's trail, allowing players to pass through.
- The last player remaining wins the round.
- The game continues until a player reaches the target score: Target Score = (Number of Players - 1) * 10.

### Controls

Each player uses two keys to turn left or right. The default controls are:

| Player | Color                                               | Turn Left | Turn Right |
|--------|-----------------------------------------------------|-----------|------------|
| 1      | <span style="color: rgb(255,0,0);">Red</span>       | 1         | Q          |
| 2      | <span style="color: rgb(0,255,0);">Green</span>     | Y         | X          |
| 3      | <span style="color: rgb(0,0,255);">Blue</span>      | B         | N          |
| 4      | <span style="color: rgb(255,0,255);">Magenta</span> | ,         | .          |
| 5      | <span style="color: rgb(0,255,255);">Cyan</span>    | O         | 0          |
| 6      | <span style="color: rgb(255,255,0);">Yellow</span>  | 2         | 3          |

Controls, colors, and game speed are customizable via the settings menu.
