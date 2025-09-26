<h1 align="center">
  <img src="docs/assets/logo.png" alt="Drak" height="100px" align="center">
  Drak
</h1>
<p align="center">
  <a href="./LICENSE"><img src="https://img.shields.io/badge/license-MIT-yellow?style=flat-square" alt="License"></a>
  <img src="https://img.shields.io/badge/snyk-monitored-4C4A73?logo=snyk&style=flat-square" alt="Snyk">
  <img src="https://img.shields.io/badge/renovate-enabled-brightgreen?logo=renovate&style=flat-square" alt="Renovate">
</p>

_Drak_ is a multiplayer snake game inspired by the classic _Zatacka_ (also known as _Achtung, die Kurve!_,
_Achtung_ or _Curve Fever_) where players leave a trail (curve) and try to outlast their opponents by avoiding
collisions with their own and other players' curves.
This repository is a Java remake of the game, originally created during a school internship in 2007. The original source
code can be found in the [original-src](original-src) directory.

For years I wanted to modernize the game, started it multiple times, but never finished it. Now I finally found the
time (or rather: took the time) to do so.
It was a fun little project to see how my skills have developed and how I would approach the project differently today.
Also, the heavy refactoring and modernization were a great exercise.
In the later stages, I worked with GitHub Copilot to review the code and making me aware of potential bugs, violations
of principles or how to further improve the code.  
Now that I finally modernized the game, I'm done with it and will not add any new features or change the game
mechanics (at least as of June 2025).

If you want to try it out, you can download a precompiled executable (available for Linux, macOS, and Windows) from the
release page or build it yourself.

## How to Play

- Each player uses two keys to turn left or right.
- The goal is to avoid crashing into your own curve or other players' curves.
- The last surviving player wins the round and earns points.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more information.
