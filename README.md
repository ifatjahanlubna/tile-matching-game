# Tile Matching Game

A match-3 style puzzle game built in Java, using Swing for the UI and custom Java 2D rendering instead of any external game engine. I built this for my Software Project Lab 1 (SPL-1) course.

The goal is to practice OOP design in a real project instead of a toy example - inheritance for the level system, encapsulated game state, and a UI layer that isn't just default Swing components thrown on a JFrame.

## What it does

It's a fruit and food themed tile matcher (apples, pizza, burgers, cupcakes, and so on) with 15 levels split across three difficulty tiers - Easy, Normal, and Hard, five levels each. Progress is tracked per level and unlocks sequentially as you clear them.

Each level can have a different win condition - reach a score, clear all the ice-covered tiles, break the locked tiles, or clear every obstacle on the board - and you're working against either a timer or a limited number of moves, depending on the level.

Difficulty isn't just a label - it actually scales the numbers:

| Difficulty | Time | Moves | Target Score | Obstacles |
|---|---|---|---|---|
| Easy | +30% | +30% | -25% | none |
| Normal | standard | standard | standard | some |
| Hard | -25% | -25% | +30% | more, and tougher |

Obstacle tiles come in two flavors: ice tiles that break after one adjacent match, and chained/locked tiles that need two.

The board is built so you never start a level already sitting on a match - the generator checks for that before the level even loads.

## Project structure

```
src/
  Main.java            entry point
  WelcomeScreen.java    title screen, difficulty picker
  LevelWindow.java      level select screen, grouped by difficulty
  AbstractLevel.java    the actual game engine — matching, cascades,
                        obstacle logic, win conditions, animation
  Level1.java ... Level15.java
                        each level just defines its board layout,
                        objective, and limits — the logic lives in
                        AbstractLevel
  GameProgress.java     tracks unlocked levels and current difficulty
  ModernUI.java         colors, fonts, shared style constants
  ModernIcons.java      hand-drawn Java2D icons (timer, score star, etc.)
  ModernDialog.java     custom confirm/alert dialogs
  RoundedButton.java    custom button component
  *.png                 tile art
```

The level classes are intentionally thin = `AbstractLevel` holds all the shared behavior (matching, cascading, obstacle state, timers/move counting), and each `LevelN` subclass just plugs in its own board shape, tile set, and objective. Adding a new level is mostly just writing a new subclass.

## Requirements

- JDK 11 or newer

## Running it

Clone the repo and build from the `src` folder:

```bash
git clone https://github.com/ifatjahanlubna/tile-matching-game.git
cd tile-matching-game
javac -d bin src/*.java
java -cp bin Main
```

If you're using IntelliJ, you can also just open the project folder directly - it's already set up as an IDEA project (`.iml` included).

## Notes

This is built for a university lab course, so don't expect production-grade error handling everywhere - but the OOP structure (abstract base class, per-level subclasses, centralized progress/state tracking) is the actual point of the assignment and is where most of the design effort went.

## Author

Ifat Jahan Lubna - SPL-1, 3rd Semester, Software Engineering