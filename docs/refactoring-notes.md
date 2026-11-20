# Model refactoring: explanation and review notes

## A concrete execution path

1. The player presses W. `GameController.handle` maps W to the UI's UP direction and rejects an opposite input or a second queued turn within a tick.
2. On the next timeline tick, `directionFor` converts the accepted key to domain `Direction.UP`.
3. `Snake.advance` computes the next cell. If wrapping is enabled, it resolves the cell on the opposite edge before checking collisions.
4. The model determines whether that cell contains food. This matters before checking the body because a growing snake keeps its tail in place.
5. If the cell is blocked by a wall, obstacle or non-vacating body segment, the model becomes dead and retains its last valid positions. Otherwise, it appends the old head to the neck end of the body and removes the tail only when not growing.
6. The controller awards five points and generates the next apple only when `advance` reports food eaten. It skips spawning on a full-board win.
7. `GameView` asks its selected `SnakeSkin` to draw the positions. The model does not know the skin, JavaFX scene, images or keyboard.
8. At loss or victory, the controller records the score once and refreshes the leaderboard/end panel. Retrying creates a fresh model while the same view retains the skin.

## Why one atomic step?

The previous `move` / `ateCoin` sequence required callers to complete both operations in the correct order. A collision could leave an extra segment after movement changed state but growth resolution was skipped. A single method makes the rule explicit: either a successful step is committed, or the last valid geometry stays unchanged. Its boolean result means "food was eaten", not "movement succeeded"; inspect `isDead()` to distinguish a collision from ordinary non-growing movement.

An immediate reverse request is ignored without ending the round or changing positions. During normal play the controller retains the previous valid direction, so a rejected input does not pause movement. The model also checks reversals so another caller cannot bypass the rule.

## Two tail scenarios to explain

A four-cell snake turns up, right, then down into its current tail cell. With no food there, the tail leaves during that step, so moving into it is legal. If the destination contains food, the tail would remain, so the same move is a collision. Both cases have explicit regression tests. The game's normal apple spawner avoids occupied cells; the second test protects the model's rule even when called with unusual input.

## Trade-offs

- `model` is a focused package extraction. Moving all classes at once would add review risk without helping these tests.
- `Direction` carries only movement displacements; the renderer chooses angles. This prevents a display convention from becoming a model dependency.
- Mutable `Point` is retained for compatibility, but model getters return deep copies. An immutable coordinate type could be a later refactor with a wider migration.
- The selected skin is view state, so it survives a model reset without being copied into each new snake.
- The leaderboard's file path is injectable. `@TempDir` gives each test its own real store, exercising persistence without altering player scores. The shared application store is lazy so tests do not implicitly initialize it.
- The model has Javadocs, while generated documentation for the whole application remains a separate submission task.

## Useful Q&A practice

- Why does testing the model no longer need `Platform.startup`?
- Why is food checked before the body collision loop decides whether to include the tail?
- Why can callers not mutate the snake through `getBody()` now?
- What happens when score storage is unavailable, and how is recovery tested?
- Which changes preserve gameplay, and which fix observable bugs?
- What remains coupled to JavaFX outside the extracted model?

Use the actual code and tests to answer these questions in your own words; do not treat these notes as a memorized script.
