# Gym RPG — Complete Tools, Libraries and Codex Skills Inventory

**Version:** 1.1 | **Status:** Approved reference inventory; no installation instructions.

This file consolidates **every tool and agent skill explicitly discussed after v1.0**, including those initially described as optional. All skill sources belong in the reference catalog. Codex should select the relevant skills per task rather than injecting all at once or allowing conflicting patterns. Availability/versions change: check current manifests and licenses and pin verified commits/dependency versions when development starts.

## 1. Host tooling, Android platform and production workflow

| Tool or technology | Responsibility | Planned status |
|---|---|---|
| Arch Linux workstation | Development host; Codex environment | Existing host |
| Android Studio | IDE, SDK Manager, Compose Preview, profiler, Logcat and interactive debugger | Recommended tooling |
| Android SDK, Platform, Build Tools, Command-line Tools | Compile, package, analyze and verify Android platform targets | Required toolchain |
| Android Platform-Tools / ADB | Install APK, test system notifications, query Xiaomi capabilities, capture screenshots, diagnostics | Required validation |
| JDK and Java toolchain | Gradle/AGP build runtime; version from compatibility matrix | Required toolchain |
| Gradle Wrapper, Kotlin DSL, AGP | Reproducible build/variants, version catalog and convention plugins only where justified | Required |
| Kotlin, Compose Compiler integration | Native business logic and declarative Android UI | Required |
| Jetpack Compose + Material 3 | Screen components, theme, lifecycle-aware interaction, accessibility | Required |
| Compose Canvas / paths, Animation, graphics | Native **2D SVG-derived anatomy**, geometry hit testing, zoom and crossfade; charts, progress animations | Required |
| AndroidX lifecycle, Navigation | ViewModel, observable `StateFlow`, state restoration and navigation | Required |
| Kotlin Coroutines / Flow | Structured asynchronous operations and reactive queries | Required |
| Room/SQLite | On-device single source of truth; versioned migrations | Required |
| DataStore | UI preferences, active settings, theme, provider toggles | Required |
| Hilt | DI candidate; use if complexity justifies it and test replacement is easy | Selected architecture tool |
| WorkManager | Deferrable report generation/allowed adapter refresh only | As required |
| OkHttp **or** Ktor | HTTP for BYOK AI/search and approved Xiaomi cloud integration; **choose one** client stack | One required for optional integrations |
| Kotlin serialization + JSON Schema validation | Strict food/exercise import and backup parsing | Required |
| Android Keystore | Secure key material / API credentials; secrets excluded from export | Required for AI settings |
| Android Storage Access Framework | Human-readable JSON export/import | Required |
| Android Live Updates / Notifications | Workout status; native promoted notifications when OS/OEM permits; standard fallback | Required with capability gate |
| Android Glance/App Widgets | Native home-screen widgets, independently from in-app dashboard widgets | Required |
| Official Xiaomi **mainland China** HyperOS 4 HealthKit | Chinese Mi Fitness data via authorized system adapter, **not global Play Store app** | Preferred, approval-gated |
| Official Xiaomi **mainland health cloud API** | Additional authorized provider/data source | Optional, approval-gated |
| Android Health Connect | Secondary import only if it actually exposes data from mainland China source on target ROM | Verified fallback only |
| Phone motion / step sensor | Local fallback for step-based activity when supported | Capability-gated |
| Git and GitHub | Source and template-schema versioning, public releases | Required |
| GitHub Actions | Compile, lint, tests, secret/license checks, APK artifacts and provenance | Required |
| ADB + **scrcpy** | Real Xiaomi screen mirroring, screenshot and gesture/navigation regression checks | Recommended |
| Android Emulator | Alternate API/device baseline tests; real Xiaomi remains release gate | Recommended |
| Android Lint, Spotless/ktlint, detekt (evaluate) | Formatting/static analysis | Quality tools, select consistent configs |
| AndroidX Test, Compose UI Test | Unit, instrumentation, accessibility and UI regression | Required |
| Screenshot testing / Roborazzi (evaluate) | Deterministic reference screenshots, nonempty/edge case screens | Recommended and required in CI as chosen |
| Macrobenchmark / Baseline Profiles | Startup, frame timing, jank and repeatable performance | Required at visual/performance gate |
| Inkscape | Edit, inspect, retrace path IDs and export SVG source masters | Recommended asset editor |
| Google Fonts upstream | **Google Sans Flex** variable font source under OFL 1.1; obtain upstream, not GPL app repo | Required typography asset |
| SVG + Android VectorDrawable or direct Compose native paths | Source vectors + compiled runtime paths. **Do not flatten click-target anatomy to PNG.** | Required |
| Lucide (ISC) | Navigation, utility and interface icon family | Primary candidate |
| Material Symbols | Alternate icon family only if icon style consistency maintained | Evaluated alternative |
| Lottie Compose | Optional complex celebration sequence; native Compose animations first | Evaluated optional runtime |
| Rive | Optional author-controlled state-machine animations; export/license review first | Evaluated optional runtime |
| [Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) | Candidate Backdrop-based refraction and liquid effects. Compose wrappers and Efficient fallback always required. Apache 2.0. | Candidate production library |
| Compose native `RenderEffect`, shaders/AGSL | Glass effects where supported; capability/version gates | Required support primitives |
| **Blender, GLB/glTF, SceneView, Filament, 3D engines** | Explicitly rejected after evaluation; not part of final design | **Out of scope / do not add** |
| Flutter, React Native, Firebase, Docker, required server, WebView anatomy | Not part of selected architecture | **Do not add** |

**Policy:** Avoid one-off external dependencies unless evaluated for SDK compatibility, license/provenance, startup/memory/battery effect and long-term maintenance. Material 3 is a foundation, not permission to ignore specified Liquid Glass visual language.

## 2. All external agent-skills — reference catalog

| # | Repository / link | Status in this project | When Codex should consult it |
|---:|---|---|---|
| 1 | [Google `android/skills`](https://github.com/android/skills) | Included | Official edge-to-edge, testing setup, platform correctness, system-bar behavior, Gradle/AGP workflows |
| 2 | [`haidrrrry/compose-kotlin-agent-skills`](https://github.com/haidrrrry/compose-kotlin-agent-skills) | Included | Architecture, Compose state, lifecycle, Flow, DI, UI testing; don't force strict MVI if it conflicts with selected project contract |
| 3 | [Kotlin `kotlin-agent-skills`](https://github.com/Kotlin/kotlin-agent-skills) | Included | Official Kotlin/Gradle/toolchain guidance |
| 4 | [`Kashif-E/KMPLiquidGlass`](https://github.com/Kashif-E/KMPLiquidGlass) | Included (verify current repo and license) | Glass component patterns and API comparison, not a mandated app dependency |
| 5 | [`ulugbekeshnazarov409/mobile-design`](https://github.com/ulugbekeshnazarov409/mobile-design) | Included | Mobile layout, typography, contrast, visual restraint, accessibility |
| 6 | [`skydoves/compose-performance-skills`](https://github.com/skydoves/compose-performance-skills) | Included | Recomposition, frame pacing, baseline profiles, GPU draw bottlenecks |
| 7 | [`skydoves/android-testing-skills`](https://github.com/skydoves/android-testing-skills) | Included | UI/JVM/instrumented tests, ADB evidence and flaky test prevention |
| 8 | [`suchobits/android-skills`](https://github.com/suchobits/android-skills) | Included — formerly optional | Design system, architect, modularization, build logic, lint enforcement, CI and screenshot strategy |
| 9 | [`hamen/compose_skill`](https://github.com/hamen/compose_skill) | Included — formerly optional | **Both** `compose-agent` during coding and `jetpack-compose-audit` in review |
| 10 | [`kizaki-R/compose-liquid-glass`](https://github.com/kizaki-R/compose-liquid-glass) | Included — formerly optional | AGSL, glass shaders/refraction, fallback and performance |
| 11 | [`kizaki-R/android-ios-liquid-glass-tabbar`](https://github.com/kizaki-R/android-ios-liquid-glass-tabbar) | Included — formerly optional | Floating, pill-shaped bottom bar, spring travel, gesture/click polish |
| 12 | [`krutikJain/android-agent-skills`](https://github.com/krutikJain/android-agent-skills) | Included — formerly optional | Broad Kotlin/Room/Hilt/Gradle/security/CI focused references |
| 13 | [`liuyi0808/android-review`](https://github.com/liuyi0808/android-review) | Included — formerly optional | Independent material-severity code review: security, architecture, Compose, Kotlin and performance |

**All means included in the catalog and available to use**—not necessarily installed, loaded simultaneously, or a reason to fork the project architecture. Some repos are community maintained, not official Android documentation; verify live contents before importing. An expired/unavailable repository does not block development: record failure and use an equivalent established source.

## 3. All planned *project-specific* Gym RPG skills

| Skill | Scope / test anchors |
|---|---|
| `gymrpg-design-system` | LastWave-inspired Google Sans Flex axis tokens, full/efficient glass, safe insets, pill dock, original emblems, screenshot audits |
| `gymrpg-anatomy` | SVG front/back imports, source provenance, 52-subdivision path coverage, hit-testing, color + zoom controls, no 3D |
| `gymrpg-strength-engine` | Calibrated ranking vs personal metrics, rank tiers, LP, XP events, deterministic scoring |
| `gymrpg-workout-rotation` | Immutable template history and same-template eligible-muscle-subdivision plateau state machine |
| `gymrpg-nutrition-engine` | 47 named nutrient keys, `null` unknown, EFSA references, energy and diary imports |
| `gymrpg-xiaomi-integration` | China ROM, official mainland HealthKit authorization, Live Updates, Health Connect fallback, provenance and dedupe |
| `gymrpg-quality-gates` | Build, tests, lint, screenshot comparison, database migrations, benchmark, real Xiaomi evidence |

These are **planned skill specifications**. Codex may implement them from the authoritative technical spec; do not claim they are already installed or written. Add only when implementation work reaches that module.

## 4. Routing priority for contradictory guidance

1. Explicit user decisions and `GymRPG_Technical_Spec_v1.1.md` / `DECISION_REGISTER.md`.
2. Versioned machine-readable schemas and accepted test fixtures. If inconsistent with #1, fix/review contract; do not silently choose.
3. Official Android/Kotlin docs for supported APIs and security.
4. Gym RPG project skills (once written) and architecture decisions.
5. External agent-skill recipes and third-party examples.
6. Generic LLM defaults.

Never merge contradictory style paradigms just because all skills are registered. One coherent Compose architecture, one chosen HTTP stack, one selected glass backend plus fallback, and one canonical anatomy taxonomy must remain authoritative.

## 5. Skill/task routing matrix

- **M0/M1 core platform:** 1,2,3,5,7,8,12 plus 9/13 audit, project design-system & quality-gates skills.
- **Pill navigation and glass:** 1,2,4,5,6,9,10,11 plus project design-system; evaluate Kyant library.
- **Anatomical SVG:** 1,2,5,6,7,8,9,12 plus `gymrpg-anatomy`, Inkscape, path geometry validation; never load 3D tooling.
- **Workout/ranking/rotation:** 2,3,7,8,12,13 plus domain skills and golden fixtures.
- **Nutrition + offline import:** 2,3,7,12,13 plus project nutrition skill and JSON Schema unit tests.
- **Xiaomi + notifications/widgets:** 1,2,7,8,12,13 plus China integration skill and device tests.
- **Pre-merge independent reviews:** 6,7,9,13, Google `android/skills` relevant audits, and `gymrpg-quality-gates`.

There are intentionally no installation commands in this document.
