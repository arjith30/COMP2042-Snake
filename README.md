# Snake: Apple Meadow — COMP2042 coursework

GitHub repository: https://github.com/arjith30/COMP2042-Snake

The maintained JavaFX Snake game has five cartoon skins, apple collection, a grass board, keyboard controls, fullscreen support and a persistent local top-five leaderboard. The coursework brief specifies the starter at https://github.com/kooitt/CW2026. The implementation extends the existing project; the history and original attribution should be retained.

## Compilation instructions

Requirements: **JDK 25 recommended**, Git, and internet access on the first build. The existing JavaFX 25.0.1 dependency requires JDK 23 or newer at runtime, even though the compiler's release target remains 21. Maven is downloaded by the included wrapper; no separate Maven installation is needed.

In Git Bash / MSYS2, Linux or macOS:

```sh
git clone --branch feat/cartoon-snake-skins https://github.com/arjith30/COMP2042-Snake.git
cd COMP2042-Snake
java -version
bash ./mvnw clean verify
bash ./mvnw javafx:run
```

For an existing checkout, close the game first and run `git pull` before building. On Windows PowerShell, replace `bash ./mvnw` with `.\mvnw.cmd`; set `JAVA_HOME` to your JDK folder if required. Compilation uses Maven's normal dependency resolution for the current platform. The first build needs access to Maven Central.

- `bash ./mvnw test` runs JUnit tests without opening the game or starting JavaFX.
- `bash ./mvnw clean verify` removes stale classes, runs tests and packages the project.
- `bash ./mvnw javafx:run` is the verified development launch command.
- Build output and Surefire test reports are in `target/` and `target/surefire-reports/`.

## Implemented and working properly

| Feature | Behaviour and location |
| --- | --- |
| Five skins | Orange Pop, Mr. Bones, Cosmic, Candy Pop and Jade Dragon. `SnakeSkin` renders one continuous body with facial features, clipped markings and consistent base colours. `MainMenuController` supplies the selected skin to `GameView`. |
| Apple collection | `Coin` supplies apple positions; `AppleArt` supplies cached artwork. Each apple grows the snake by one segment and awards five points. |
| Grass board | `GameView` draws a 15 × 15 board with a default tile size of 44px and a separate HUD. |
| Controls | WASD and arrow keys steer. P pauses; movement keys resume. Reverse input cannot turn directly into the neck. |
| Retry | Enter after a loss or win, the Try Again button, R or Tab starts a new round with the selected skin. |
| Fullscreen | F11 or the Fullscreen button toggles fullscreen. Plus/minus also enter/leave it. Resizing keeps the board square and centered. Esc exits fullscreen first, then returns to the menu. |
| Local leaderboard | `Leaderboard` stores the top five completed-round scores in descending order, preserving ties. `LeaderboardView` displays them in the menu and end panel. A round is submitted once. |
| Win handling | Filling the board wins without attempting to spawn food into a full board. |

The model starts with four occupied cells, leaving 221 apples to fill the current board. The leaderboard is local to this device, not an online service. Scores live in `data/leaderboard.txt`, relative to the launch directory; `data/` is ignored by Git. If saving fails, scores remain available for the current session and the UI reports this. Unfinished rounds are not submitted when restarting or leaving.

## Maintenance and refactoring

The focused model refactoring was developed on `refactor/snake-model-junit` and integrated into the playable `feat/cartoon-snake-skins` branch. `main` remains separate pending final integration/review.

| Before | Change and rationale | Evidence |
| --- | --- | --- |
| `Snake` constructed JavaFX images and owned its skin while also deciding movement and collisions. | Move it to `com.comp2042.model.Snake`; remove rendering state and unused image/colour/direction helpers. `GameView` retains the chosen skin while `SnakeSkin` maps domain directions to display angles. This separates responsibilities and allows tests without JavaFX startup. | `SnakeTest`, the UI integration smoke check. |
| Movement appended a segment and a later `ateCoin` call removed it. | Replace this two-phase protocol with `advance(Direction, Point, Collection<Point>)`, which resolves movement, growth and collisions together. The controller awards points only when it returns true. | Food growth, normal movement and collision tests. |
| Failed movements could alter the body despite the head remaining in place. | Check the proposed cell before committing position/body changes. | Wall, obstacle, self-collision and dead-snake tests. |
| Tail collision handling did not distinguish growth correctly. | Permit the current tail cell only when the tail vacates it; check it as occupied during a growing step. | Paired tail-vacating/tail-growing regression tests. |
| `getBody()` copied the list but exposed its mutable `Point` elements. | Return deep copies of positions. | Defensive-copy test. |
| Obstacle checks preceded wrapping. | Resolve the wrapped cell first, then check obstacles and the body. | Wrapped-destination obstacle test. |
| Direction rules depended on characters and UI key values. | Add the domain `Direction` enum for displacements and reverse checks; translate keyboard input in `GameController`. | `DirectionTest` and reverse-input tests. |
| Constructing a test leaderboard also initialized the default score store. | Initialize the shared application store lazily; test instances use explicit temporary paths. | Isolated `LeaderboardTest` cases using `@TempDir`. |

This applies the Single Responsibility Principle to movement versus presentation and improves encapsulation. It does not introduce a design pattern solely for the rubric. Java's `Point` remains a standard-library grid coordinate, protected by defensive copying. Other application classes remain in the existing package; a wider package migration is future work.

## Test cases and results

The committed JUnit Jupiter 5.11.4 suite runs through Maven Surefire 3.5.2:

| Test class | Executed cases | Coverage |
| --- | ---: | --- |
| `src/test/java/com/comp2042/model/SnakeTest.java` | 29 | Initial state, all movement directions, constant length, growth, reverse input, all walls, obstacles, self-collision, tail rules, all wrapping edges, wrapped obstacles, defensive copying, dead state and invalid inputs. |
| `src/test/java/com/comp2042/model/DirectionTest.java` | 4 | Opposite-direction rules for every direction, including null comparisons. |
| `src/test/java/com/comp2042/LeaderboardTest.java` | 12 | Top-five sorting, ties, persistence, missing directories, corrupt/negative/overflow rows, immutable results, write failure, recovery and zero scores. |
| **Total** | **45** | **0 failures, 0 errors, 0 skipped** in the verified build. |

Parameterized tests count each input separately. The tests construct real model objects and use temporary score files; they do not open a display, write to the player's score file, or depend on installed fonts. No JavaFX test platform is required for `mvn test`.

A separate, headless JavaFX smoke check exercised the actual menu and all five skin selections, scoring/growth, WASD and reverse rejection, fullscreen/Esc, the end screen and leaderboard, Enter retry, retaining the selected skin, and square-board resizing. This supplementary check was run during development; it is not part of the committed JUnit suite. It does not establish native Windows interactive behaviour or replace the JUnit results.

## Implemented but not fully verified / known limitations

- Native Windows fullscreen and final manual gameplay still need confirmation on the student's computer. Earlier build/startup issues were addressed, but the final refactored version needs a fresh manual play-through.
- The shaded JAR is produced by the build; the documented and checked launch path is the Maven JavaFX plugin. A portable standalone distribution has not been validated.
- The leaderboard does not support names, online synchronization or simultaneous multi-process updates.
- `Coin` and the legacy power-up classes still use the original naming. Random power-up coins are disabled in the current apple mode. `Game` and `Obstacle` still contain some presentation-related details; this session isolates the snake model, not the entire application.

## Features / submission items not implemented

- Additional playable levels and adaptive difficulty have not been added. These are candidate substantive extensions after the model work is understood and reviewed.
- `Design.pdf`, complete generated `Javadoc/`, the student's own-voice `Demo.mp4`, and the final submission archives are not complete. They must be prepared from the final code, following the coursework brief.
- This README is a working coursework record. The student must review its accuracy, supply personal details where required in the final submission, and describe any further work.

## New Java classes

All locations below are under `src/main/java/`.

| Class | Location | Purpose |
| --- | --- | --- |
| `model.Direction` | `com/comp2042/model/Direction.java` | Grid displacement and reverse-direction rules, independent of UI keys. |
| `model.Snake` | `com/comp2042/model/Snake.java` | Moved/refactored original snake model; atomic movement, growth and collisions. Replaces `com.comp2042.Snake`. |
| `SnakeSkin` | `com/comp2042/SnakeSkin.java` | Five cartoon skins and continuous rendering. |
| `AppleArt` | `com/comp2042/AppleArt.java` | Cached apple artwork. |
| `Leaderboard` | `com/comp2042/Leaderboard.java` | Ranked local score storage and persistence. |
| `LeaderboardView` | `com/comp2042/LeaderboardView.java` | Reusable menu/end-screen leaderboard UI. |

## Modified Java classes

- `GameController`: keyboard translation, atomic model stepping, score updates, retry, fullscreen and once-per-round leaderboard submission.
- `GameView`: grass/apple presentation, HUD, end panel, responsive board scaling, and ownership of the selected skin.
- `MainMenuController`: skin selection/preview, leaderboard refresh, and creation of the model/view/controller with the correct responsibilities.
- `ViewManager`: resizable stages, fullscreen menu support, and refreshing the menu leaderboard.
- `Game`: larger tile size, score/win/reset handling, and use of `Snake.INITIAL_LENGTH` instead of repeating the starting length.
- `Coin`: apple presentation and imports of the extracted model; retains the existing class name for this bounded change.
- `Obstacle` and the legacy `BlueCoin`, `GoldCoin`, `SilverCoin`, `RainbowCoin`: update snake model imports where required; their legacy feature behaviour is not redesigned in this session.

## Unexpected problems and resolutions

- **Maven missing in the Windows shell:** use the included wrapper instead of requiring a separate installation.
- **Invalid wrapper distribution URL / Windows carriage returns in the download URL:** correct the distribution URL, strip the carriage return, enforce wrapper line endings and retry empty downloads.
- **Rendering seams and turn colour changes:** use one continuous ribbon with a single base colour rather than independently rotated gradient tiles.
- **Model testability:** creating the old snake triggered image snapshots, requiring JavaFX. The extracted model compiles using the JDK alone and tests run without starting the toolkit.
- **Mutable body data:** a shallow list copy allowed external position mutation. Deep copies now protect the internal state.

## Code understanding and AI-use record

See [docs/refactoring-notes.md](docs/refactoring-notes.md) for a movement walkthrough, regression cases and design trade-offs. See [docs/ai-use-record.md](docs/ai-use-record.md) for the assistance record to review when completing the required COMP2042 AI Use Declaration Form. This record is not a substitute for that form or for being able to explain and justify the submitted implementation.
