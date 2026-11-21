# AI assistance record — working notes for student review

These notes describe assistance in this project conversation. They are not the university declaration form and must be reviewed and adapted by the student before submission.

## Earlier feature and maintenance sessions

ChatGPT/Codex assisted with implementing and revising the five snake skins, connecting their artwork, the grass board, apple collection, smaller board/larger tiles, WASD and arrow controls, retry buttons and Enter retry, fullscreen/resizing, the local top-five leaderboard, and Windows Maven-wrapper fixes. It also wrote supporting documentation and ran development build/headless UI checks. These contributions should be included in the declaration, not only the latest session.

## Model and JUnit session — 21 November 2026

Following the student's agreement to a refactoring/testing recommendation based on the coursework brief, ChatGPT/Codex:

- examined the existing movement, rendering and leaderboard code;
- proposed and implemented the JavaFX-independent `model.Snake` and `model.Direction` extraction;
- moved the selected skin into the view and updated the controller/renderer integration;
- implemented atomic movement/growth/collision resolution and defensive position copies;
- made default leaderboard initialization lazy to isolate file-based tests;
- added JUnit Jupiter/Surefire configuration and 45 executed test cases;
- ran the tests and a separate headless JavaFX integration smoke check;
- drafted the root README and refactoring explanation notes;
- prepared focused Git commits on a refactoring branch and integrated them into the playable feature branch.

## Student review still required

Record which suggestions you accepted, changed or rejected; your own testing and explanations; and the tool/model/session details requested by the COMP2042 AI Use Declaration Form. Check all claims against the final submitted code. Be prepared to trace and justify both your own and AI-assisted implementation during the code-understanding Q&A. Do not claim these generated changes or notes were written without assistance.

## Playable-level session — 22 November 2026

At the student's request to update the program with substantive coursework work, ChatGPT/Codex designed and implemented three selectable/progressive levels, goals and increasing speed, hedge/stone layouts, transitions, cumulative adventure scoring and final-only leaderboard submission. It extracted pure-Java level/progress/food-placement logic, replaced the unbounded food retry loop, added 32 JUnit cases (77 total), ran model/UI checks and updated the README and extension notes. The student still needs to review the implementation, test on Windows and record their own contributions and understanding in the required declaration form.
