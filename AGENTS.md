# Gym RPG — Codex development policy (v1.1)

Before editing any code, read `docs/GymRPG_Technical_Spec_v1.1.md`, `docs/DECISION_REGISTER.md`, and the contract/fixtures relevant to the task. Read `docs/ASSET_PIPELINE.md` and `docs/TOOL_AND_SKILL_MATRIX.md` when touching visuals, dependencies, tools or skills.

## Non-negotiable decisions
- Native Kotlin + Jetpack Compose; modular local-first application; no remote backend dependency.
- Main phone Xiaomi 17 Pro, mainland Chinese HyperOS 4 beta; all China Mi Fitness work is official mainland HealthKit-first, permission and signature gated.
- Native 2D front/back SVG anatomy. **Do not use Blender, GLB, SceneView, Filament or JavaScript/WebView anatomy.**
- Officially licensed Google Sans Flex; typography inspired by LastWave but **no copying GPLv3 app code** into Gym RPG without separate approval.
- Dark-first premium Full/Efficient glass modes; floating five-tab pill and safe system insets.
- Versioned same-template subdivision-based plateau logic and scoring algorithms are specified; never replace them with generic workout heuristics.
- The 47 nutrient fields, JSON contracts, 52 taxonomy IDs and acceptance fixtures must remain consistent across app and tests.

## Skills
The complete 13-repository external skill roster, including formerly optional sources, is in `docs/TOOL_AND_SKILL_MATRIX.md`. Treat all as *available references*, loading only task-relevant skills. The seven Gym RPG project skills are installed by the bootstrap; confirm they exist in `.agents/skills` before using them. Official Android/Kotlin docs and this project's binding contracts take precedence when skills conflict.

## Assets and licensing
Keep editable SVG originals and native runtime paths; register source URLs, SHA-256 and SPDX notices. No extracted LastWave assets except separately OFL-licensed upstream Google Sans Flex. No Liftoff art clones. No proprietary Xiaomi SDK committed without distribution rights. No third-party font binaries in specification archive.

## Engineering and PR discipline
Implement M0 before subsequent milestones. Prefer small, self-contained PRs and no unnecessary new abstractions. Choose consistent dependencies (one HTTP client stack, one glass abstraction). Make algorithms deterministic and versioned. Use transaction-safe local writes and migrations. Provide unit, database, instrumented/screenshot tests and real-device evidence when possible. Do not label work complete solely because it compiles.

## Reporting
For each milestone report: files changed, requirements implemented, tests actually run, pass/fail, device screenshots/metrics, rights/approval blockers, unverified OEM functionality and follow-up decisions. Never claim untested HyperIsland, official Xiaomi HealthKit authorization or exact proprietary Liftoff behavior.

## Local project skill installation
This bootstrap installs the seven Gym RPG domain skills in `.agents/skills/` and registers
third-party skill sources under namespaced `upstream-*` links. Skills from the original
specification described as planned are available once bootstrap installation succeeds.
Third-party skills do not override binding project decisions.

<!-- gymrpg-android-sdk-37 -->
## Android SDK 37 build configuration
For Android API 37, use the SDK Manager package `platforms;android-37.0`
(installation directory `$ANDROID_HOME/platforms/android-37.0`). Set
`compileSdk = 37` in Gradle. Do **not** request the unavailable package
`platforms;android-37`. Verify the platform is installed before building;
`compileSdk` does not automatically determine `targetSdk` or `minSdk`.
<!-- /gymrpg-android-sdk-37 -->
