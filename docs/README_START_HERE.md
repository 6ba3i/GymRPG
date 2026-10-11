# Gym RPG — Complete cahier des charges and Codex pack v1.1

**Supersedes:** Gym RPG v1.0 package dated 10 October 2026. **Language:** English. **Target:** Xiaomi 17 Pro / Chinese HyperOS 4.0.0.41 beta; native Android; local-first and open-source.

## What to read, in order

1. `DECISION_REGISTER.md` — all binding decisions and what superseded earlier ideas.
2. `GymRPG_Technical_Spec_v1.1.md` — full updated requirements / architecture / algorithms / acceptance tests.
3. `AGENTS.md` — repository-level Codex project policy.
4. `ASSET_PIPELINE.md` — finalized **2D front/back SVG** anatomy, Google Sans Flex / LastWave-inspired typography, glass/full-efficient, icons, original ranks, licensing and screenshot QA.
5. `TOOL_AND_SKILL_MATRIX.md` — all 13 discussed external skill repositories (including optional ones), seven planned project skills, complete Android and graphics tooling reference. **No installation instructions.**
6. `M0_CODEX_BRIEF.md` — precise initial implementation milestone; use M0 before later features.
7. `*.schema.json`, `.example.json`, `muscle-taxonomy.v1.json`, `plateau-fixtures.v1.json` — machine-readable contracts and examples.
8. `CHANGELOG_v1.1.md` — precise v1.0 → v1.1 changes and deliberate schema-version preservation.
9. `GymRPG_Technical_Spec_v1.1.pdf` — PDF reading copy of the same authoritative specification.

## Important final locks

- **2D ONLY** anatomy, not Blender/SceneView/Filament. Native Compose canvas path renderer with front/back toggle, tap and zoom.
- **Google Sans Flex** under its independent OFL upstream license; recreate LastWave's look without copying GPL implementation; no font bytes provided in this archive.
- **All previously recommended external skills are in the catalog**—use relevant skill groups for the task instead of forcing contradictory guidance simultaneously.
- Liquid Glass: original dark-first five-tab floating pill, Full/Efficient modes; Kyant0 Backdrop candidate with fallbacks.
- Calendar, template versioning, muscle subdivision stagnation rule, regional mapping, LP/XP, nutrition JSON/micros, offline data, DeepSeek/OpenAI-compatible API settings and **Chinese Mi Fitness** priority all remain specified.
- Rear screen remains out of scope; Chinese HealthKit requires vendor approval. Live Update promotion into HyperIsland remains OEM-controlled and must be tested.

## Contents

The ZIP includes a *readable PDF*, complete Markdown source, strict food/exercise/anatomy/asset schemas, example JSON files, 52-subdivision checklist, plateau fixtures, source/asset/skill policies and milestone brief. The JSON contracts unchanged from v1.0 retain their original schema version rather than receiving a cosmetic breaking version bump.

**Not included:** installation guides, device configuration commands, fonts or proprietary image/3D asset binaries, unapproved Xiaomi SDK, real app code or fabricated screenshots.
