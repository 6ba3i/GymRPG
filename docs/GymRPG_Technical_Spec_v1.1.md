# Gym RPG — Technical Requirements & Implementation Specification

**Version:** 1.1 — Consolidated, superseding v1.0; executable design baseline  
**Date:** 10 October 2026  
**Primary device:** Xiaomi 17 Pro, mainland China ROM, HyperOS 4.0.0.41 beta  
**Language:** English  
**Distribution:** Open-source GitHub repository and signed, sideloadable Android APK  
**Status:** Product scope agreed; platform integrations, visual-asset provenance, dependency compatibility and statistical calibration require development-stage validation. This v1.1 supersedes v1.0.

> **Product principle:** A personal, offline-first, native Android gym RPG combining complete workout logging, deterministic and explainable progression/ranks, a configurable anatomical training map, intelligent workout-template rotation, detailed nutrition and energy tracking, and optional AI assistance. No internet, account server, or Google Play service may be required to use its core functionality.

## 1. Scope, terminology and non-goals

### 1.1 Product goals

- Make logging sets, reps, loads, exercises, drop sets, supersets, and session times extremely quick.
- Provide a **native 2D front/back SVG-path anatomical model** with detailed selectable muscle subdivisions; show training stimulus and progression *without claiming physical muscle size was measured*.
- Gamify performance with nine strength tiers, exercise/muscle-group/overall ranks, LP, XP, quests, streaks, seasonal summaries and personal records.
- Implement fixed-weekday Push/Pull/Legs/Upper/Lower training, versioned workout templates, and automatic sequential rotation after muscle-subdivision plateau detection.
- Track food with strictly validated multi-item JSON import, recipes, quantities, macronutrients, complete supported micronutrients, EFSA-based age/sex references and transparent calorie expenditure estimates.
- Support user-controlled AI for exercise research, training-history search, plateau diagnosis and nutrition analysis, with budget controls and precise caching.
- Offer a fluid dark-first, **Google Sans Flex / LastWave-inspired** Liquid Glass interface, adjustable effect quality, customizable in-app dashboard widgets, Android launcher widgets and workout Live Updates.

### 1.2 Boundaries

**In scope:** one local profile, native Android app, on-device database/domain engine, offline-first workflows, manual JSON backup/restore, Xiaomi *mainland* Mi Fitness integration when approved, optional Health Connect adapter only if functioning on the user's specific ROM, AI/search connectors configured by the user.

**Out of scope for v1:** rear-screen widgets; real-time 3D, Blender geometry, GLB/glTF, SceneView/Filament, WebView-based anatomy; account/cloud-hosted backend; public leaderboards or friendships; monetization; virtual currency, cosmetic shop or physical prizes; exercise tutorial video library; mandatory rest timer; daily mandatory workout/rest tracking; automatic form grading; medical diagnosis; automatic meal-photo scanning; mandatory wearable; importing historical data from competing fitness apps; native iOS.

**Terminology:**

- **Exercise definition:** canonical movement and equipment/loading variant, with searchable aliases and anatomical mappings.
- **Working set:** performed load and repetitions, taken to failure by default; a *drop component* is another load/rep pair in the same set sequence.
- **Template:** ordered playlist of exercises with prescribed working-set counts; immutable versions preserve historical meanings.
- **Group:** collection of templates, e.g. Push A/B/C/D. An active-template pointer selects one at a time.
- **Session:** actual workout with timestamped performance records; an optional planned template version is captured as a snapshot.
- **Subdivision:** anatomically named region/head shown on the body (e.g. lateral deltoid); a **muscle group** aggregates subdivisions (e.g. deltoids).
- **Training stimulus:** heuristic allocation of performed working sets to regions, not a direct measurement of muscle growth or mechanical tension.
- **Performance estimate:** normalized exercise-specific strength evidence, with a confidence flag; not a claim of directly measured 1RM.
- **Strength rating:** 0–1000 independent scale for qualifying movements, group ranks and overall rank where calibration is available.

### 1.3 Requirements notation

- **P0:** must be implemented and pass acceptance tests before the relevant milestone closes.
- **P1:** required for full v1 release, but may be implemented in a later milestone.
- **P2:** experimental integration; hidden/disabled gracefully when unavailable.
- **Blocked external:** requires third-party approval, data license or OS-device verification; the rest of the app must still ship.

## 2. Target device and platform

| Topic | Contract |
|---|---|
| Device | Xiaomi 17 Pro; Chinese HyperOS 4.0.0.41 beta, actual Android API level probed on device at runtime |
| Form factor | Portrait phone UI; responsive to small display, large fonts, keyboard and varying navigation-bar modes |
| APK | Reproducibly buildable debug and signed release APK; sideload without Play Store or Play Integrity dependency |
| Android baseline | `minSdk=29` proposal for wider open-source compatibility; `compileSdk/targetSdk` set to the latest stable level verified by CI/toolchain at project bootstrap, with API-36+ Live Update capability gated dynamically |
| Permissions | Least privilege, requested only when feature accessed; use modern photo/file picker rather than broad file access |
| Insets | Edge-to-edge background may extend, but all interactive controls, sticky toolbars, sheets and floating pills respect display cutout, status bar, IME and gesture/navigation insets |
| Orientation | Portrait only for v1; preserve state if configuration changes anyway |
| Theme | Dark as default; selectable full glass, efficient glass, system-following light/dark optional |
| Data processing | All core computations locally; no telemetry or analytics without explicit opt-in |

**Important:** HyperOS version does not by itself establish Android API level or third-party API access. Do not hardcode assumed Android 17 support. Build capability probes and run on the actual device.

## 3. App architecture and repository

### 3.1 Architectural style

Use Kotlin, Jetpack Compose, Material 3 primitives where helpful, AndroidX Navigation, Kotlin Coroutines/Flow, Room/SQLite, DataStore, WorkManager only for deferrable tasks, Glance/App Widgets, OkHttp/Ktor HTTP clients, and explicit domain interfaces. Maintain a **modular monolith**: a local service layer (the application's 'backend') rather than a localhost server or a required remote backend.

Recommended source tree:

```text
app/
core/model/                 immutable domain values, stable IDs, units
core/database/              Room entities, DAO, migrations, transaction boundaries
core/designsystem/          Google Sans Flex typography, tokens, glass modes, icons and effects
core/algorithms/            pure strength/LP/XP, plateau, nutrition, energy functions
core/importexport/          versioned JSON, validators, snapshots, checksums
core/notifications/         foreground session notification + Live Update bridge
core/integrations/          integration contracts and common provenance model
feature/onboarding/
feature/home/
feature/workouts/
feature/exercises/
feature/calendar/
feature/anatomy/            native Compose Canvas vector paths; NO 3D renderer
feature/rankings/
feature/nutrition/
feature/profile/
feature/settings/
integration/xiaomi-cn/        HyperOS 4 HealthKit adapter, separately gated
integration/healthconnect/   optional compatibility adapter
integration/ai/              model providers, optional web search, local AI cache
integration/widgets/         home-screen widget entrypoints
assets/anatomy/              licensed 2D SVG masters, extracted native paths, region geometry and attribution
assets/exercises/            licensed exercise metadata and aliases
schemas/                    canonical JSON Schema, data snapshots
fixtures/                   test workouts, food, import/export examples
build-logic/                 shared Gradle convention plugins if justified
docs/                        ADRs, architecture, scoring methodology
.github/workflows/           lint, unit tests, APK builds, provenance checks
```

Prefer few, cohesive modules over premature micro-modules. Keep business logic free of Android/Compose dependencies. All derived metrics have calculation-version IDs and reproducible fixtures. Avoid global mutable singleton state.

### 3.2 Data and units

- Stable UUID/ULID entity IDs. External seed IDs mapped to canonical IDs, never used as database primary keys without namespace.
- Timestamp storage in UTC with original offset/time-zone identifier for user-visible scheduling and chronology. Workout week determined by a user-selectable home calendar time zone (default device zone).
- SI internal values: kilograms, centimeters, grams, milligrams, micrograms, kcal, seconds; optional localized presentation only. Floating-point calculations use defined rounding; mass/food quantities preferably use integer milligrams or validated decimal values, avoiding compounded display rounding.
- Entity updates inside Room transactions; foreign keys/indexes enabled; soft-delete or tombstones for records referenced by completed sessions.
- All custom-record edits emit relevant data-revision events to invalidate derived charts and AI cache entries.

### 3.3 Core schema (conceptual entities)

| Entity | Key fields / relationships | Key invariant |
|---|---|---|
| `LocalProfile` | id, dateOfBirth, sexForReferenceValues, heightCm, weight-history linkage, homeTimeZone | exactly one profile; no cloud login |
| `Measurement` | date/time, type (`weight`,`bodyFat`,`waist`,`neck`,`hip`,...), value, unit, provenance | body-fat can be manual; provenance visible |
| `Goal` | energyKcal, proteinGrams, effectiveDate, status, method | user override has priority over calculator |
| `MuscleGroup` / `MuscleSubdivision` | stable taxonomy ID, parentId, view polygon IDs, side, version | group-level strength rank; region-level color statistics |
| `Exercise` | id, name, aliases, equipment, variationIds, loadConvention, status, evidence | distinguished variants; editable aliases |
| `ExerciseMuscleMap` | exerciseId, subdivisionId, role, coefficient, method, confidence, mappingVersion | coefficient is training heuristic, not physiology fact |
| `TemplateGroup` | id, name, order, activeTemplateId, activationPolicy | unlimited templates |
| `Template` / `TemplateVersion` | groupId, versionNo, createdAt, authorNote, immutable content hash | previous versions cannot mutate |
| `TemplateExercise` | templateVersionId, order, exerciseId, plannedSets, optional supersetGroup | planned sets >=1; no default rep prescription required |
| `PlanDay` | weekday, assigned group/rest, enabled, validFrom | Monday Push to Friday Lower default; weekends rest |
| `WorkoutSession` | id, templateVersionId nullable, startedUtc, endedUtc, state, scheduledDate, localZone | one active session; start/end editable after stop |
| `PerformedSet` | sessionId, exerciseId, order, type, actualTimestamp, side, complete | captured per set; to-failure assumed |
| `LoadComponent` | performedSetId, order, loadKg, reps, loadConvention, kind (`main`,`drop`) | drop chain can contain N additional segments |
| `SessionExerciseSnapshot` | sessionId, exerciseId, canonical version, order, plannedSetCount | historical displays never depend on current catalog mutations |
| `StrengthObservation` | exerciseId, sessionId, formulaVersion, estimate, reliabilityFlag | cannot compare across invalid load conventions |
| `RegionProgressSnapshot` | templateVersionId, sessionId, subdivisionId, evidence/score, eligibility, formulaVersion | statistical comparison preserved with version |
| `PlateauState` | groupId, activeTemplateVersionId, consecutiveStagnant, lastSessionId, status | idempotent one evaluation per session |
| `RankSnapshot` / `XpEvent` / `QuestProgress` | score, tier, LP, level, sourceEvent, seasonId, algorithmVersion | append-only gains; no double award |
| `Food` | id, name, nutritionBasis, schemaVersion, provenance, flags | unknown nutrient = null, not 0 |
| `Recipe` / `RecipeIngredient` | recipeId, food/recipe reference, quantity, cooking/yield metadata | cycle-free; immutable diary nutrient snapshots |
| `FoodDiaryEntry` | date/time, food/recipe ID, grams/portions, nutrientSnapshot, provenance | edits recalculate today's totals |
| `ActivityRecord` | activityType, start/end, MET/intensity or reported energy, source, externalId | provenance + overlap detection |
| `ExternalHealthSample` | source, sourceRecordId, sampleType, interval, value, importStamp | unique per provider record, deduplicated |
| `AIConfiguration` | providerId, modelId, endpoint, feature toggles, cache policy, budget | secrets stored separately via Keystore |
| `AIResultCache` | scope, hash, provider, model, promptVersion, evidence, payload, createdAt | only valid for same relevant data revision |
| `WidgetLayout` / `WidgetInstance` | layoutId, kind, grid position, span, settings | atomic layout writes, safe defaults |
| `ImportBatch` / `ExportManifest` | schemaVersion, checksum, createdAt, outcome, warnings | restore atomically after validation |

Specify indexed DAO queries and unique constraints before writing repositories. Any versioned record referenced by a workout must remain accessible even after deleting a template or catalog entry.

## 4. App structure and UX contract

### 4.1 Navigation, Google Sans Flex typography and Liquid Glass

Five bottom destinations: **Home / Workout / Calendar / Food / Profile**, in that order. The bottom dock is one floating **pill-shaped, translucent** component with an independently animated selected-tab lens. The dock is anchored **above the effective gesture/navigation inset**, and every screen must reserve enough bottom scroll padding so actions never hide beneath it. Rank screens live under Home/Profile; settings, logs, diagnostics and imports live under Profile. A contextual `Start workout` action is accessible from Home/Workout/Calendar. Portrait-only app.

**Typography source (locked):** Google Sans Flex **variable** font, from the [official Google Fonts upstream](https://github.com/googlefonts/googlesans-flex), under **SIL OFL 1.1** with the required license/copyright attribution. The look is **inspired by** the independent Compose typography setup of [LastWave Native](https://github.com/Clash-Projects/LastWave-Native/blob/main/app/src/main/java/com/lastwave/app/ui/theme/Type.kt). LastWave's repository code is GPLv3; **do not copy its Kotlin theme file**, UI assets or GPL code into Gym RPG without a separate explicit license decision. Recreate design tokens independently; download font from OFL upstream and do **not** redistribute third-party font binaries in this specification pack.

Define one `GymTypography` mapping all Material 3 typography roles. Preserve six Google Sans Flex axes where supported: `wght`, `wdth`, `opsz`, `ROND`, `GRAD`, `slnt`. Proposed visual *starting tokens*, inspired by LastWave and subject to screenshot validation:

| Role | `wght` | `wdth` | `ROND` | `opsz` | Remarks |
|---|---:|---:|---:|---:|---|
| Display / hero | 760 | 122 | 42 | 57 | expressive, wide and round; avoid clipping |
| Headline medium | 780 | 120 | 38 | 28 | headings and muscle panel titles |
| Title large | 640 | 110 | 30 | 22 | workout template and card names |
| Body medium | 430 | 102 | 18 | 15 | readable numeric and narrative content |
| Label / pill | 680 | 108 | 46 | 14 | bottom tabs, chips and buttons |

Full typographic scale (display/headline/title/body/label, small/medium/large), use Compose `FontVariation` or compatible APIs with feature checks. Support Android font scale and opt-in system-font fallback. Use tabular numerals for time, sets, LP, weights and nutrition where supported. Ensure font weight/style coverage for symbols, non-Latin fallback, accessibility and launcher widget constraints (Android widget text may not support custom app fonts; use the platform family unless an accessible supported technique is proven). No branding claims implying Google or LastWave sponsors Gym RPG.

**Liquid Glass visual system:** Dark by default; selectable **Full Glass** and **Efficient Glass**, plus light/system-following mode and reduced-motion override. Candidate rendering foundation: [Kyant0/AndroidLiquidGlass (Backdrop)](https://github.com/Kyant0/AndroidLiquidGlass) (Apache-2.0; **evaluate API maturity/compatibility and pin tested version**, never blindly copy demo code). Build Gym RPG wrappers such as `GymGlassSurface`, `GymGlassPillNav`, `GymGlassButton`, `GymGlassSheet`; abstract renderer so no feature module imports vendor library directly. Use native Compose drawing and `RenderEffect`/shader primitives only where supported. AGSL for optional premium refraction; provide deterministic efficient fallback using subtle translucent fills, borders and light blur or no blur. All backgrounds keep text readable. No intensive blur behind long scrolling lists when it causes jank.

Motion: spring-like pill transitions, subtle card expansion, XP/LP fills, rank-up and PR celebrations, short front/back anatomy crossfades. Respect system reduce-motion. At least 48dp interaction targets; accessible semantic content; preserve IME/status/cutout/gesture insets; test both gesture and 3-button nav and different font scales. Original iconography and graphics, **not copied from LastWave/Liftoff/Apple Music**.

**Primary iconography:** license-compliant SVG/vector set (e.g., [Lucide](https://lucide.dev/), ISC) adapted to one stroke/style, plus original nine tier emblems. Material Symbols may be evaluated as an alternative but mix only with clear design consistency. No image generation as a substitute for editable/path-addressable muscle geometry. Asset sourcing, provenance, and review workflow are in `ASSET_PIPELINE.md`.

### 4.2 Home dashboard widgets

Initial layout: **anatomy widget centered at top**, then today's program, active session/quick start, rank card, progress, calorie/protein, micronutrient summary, weekly trend, activity expenditure, next rotation, and recent PR. Every widget—including anatomy—can be removed, resized, reordered by long press/drag, configured, and restored. Layout edit mode has add-widget catalog, snap grid, preview, reset layout, overflow handling and undo. Preserve stable layout IDs; reject off-grid or overlapping saves; provide responsive spans by available width.

Launcher widgets (Android home screen) are **independent** of in-app widgets: today's workout, start/resume, current session, rank/XP, calories/protein, step count, muscle map thumbnail, upcoming plan, progress summary. Respect launcher widget resizes, periodic refresh restrictions and battery costs. Sensitive dietary/profile widgets show limited info if lock-screen disclosure settings are enabled.

### 4.3 Native 2D anatomy renderer — final, no 3D

**Decision lock:** use an open-source-licensed **front and back 2D SVG anatomy** with individually addressable muscle SVG paths; do not implement 2.5D simulated orbit, side view, 3D models, GLB, Blender, SceneView, Filament or WebView canvas. Front/back are **explicit buttons/toggle**, switching via a quick crossfade, with correct state and accessible labels. This is the final choice after investigating and rejecting 3D.

**Source evaluation (before copying any geometry):**

1. [vulovix/body-muscles](https://github.com/vulovix/body-muscles) — 70+ named regions, front/back SVG path data, Apache-2.0 **LICENSE and NOTICE**; JavaScript renderer is reference only, do not embed it.
2. [Jsplice/MuscleMap](https://github.com/Jsplice/MuscleMap) — individually addressable original SVG muscle paths, MIT; verify its `ASSET_PROVENANCE.md` and exact asset license; source code is not an Android renderer.

Select based on accuracy, required coverage, ease of native-path conversion, quality of outlines, left/right discrimination, legal attribution, and suitability to 52 taxonomy subdivisions. **The 52 taxonomy entries and 70+ source SVG regions are not 1:1.** Use an explicit `geometryMap` from canonical subdivision IDs to one or more front/back SVG path IDs; multiple regions may represent a subdivision, and several anatomical subdivisions may need a deliberately coarse shared shape. Mark unavailable or indistinguishable subdivisions as such; never imply shape precision that the source lacks. Neither repo is to be imported as a JavaScript dependency.

**Runtime:** extract normalized SVG `viewBox` plus path strings at build/asset-processing time. Parse to native `Path` / Compose `Canvas` paths; cache immutable geometry and bounds, not a raster-only picture. Render in layers (neutral silhouette, colored region fills, subtle outlines, selection ring, tap/zoom overlay). Keep stable `regionId`, `sourcePathId`, `view`, `sourceLicense`, `groupId` and `subdivisionIds` mappings; validate all IDs against `muscle-taxonomy.v1.json`. Hit-test using inverse zoom/pan transform plus `Path.contains`-equivalent geometry or precomputed path tests; prevent tiny body parts from becoming inaccessible by a companion searchable **list view** and accessible hit targets.

**Interactions:** (1) front/back segmented buttons, (2) tap a muscle region -> detail and exercises, (3) highlight primary vs secondary exercise involvement, (4) color each path from the active map mode, (5) one simple animated focus operation using `scale + translation` to bounding box of selected paths, (6) Reset zoom. No 360-degree illusion or fake 3D rotation. Selection and scroll state survive switching. Fit within the Home widget and detail views with correct content bounds.

**Modes:** `Strength Rank` (only **muscle groups** get ranked tiers; subdivision colors indicate mapped relative progress/evidence, not isolated muscle strength), `7/30/90-day Volume`, `Relative Muscle Balance`, `Estimated Recovery` (label experimental), and `Template Targeting` (playlist-only planned region stimulus). Each has its own **multiple-hue, discrete and colorblind-considerate legend**, visual symbols/rank emblems, numeric values, `Unknown` styling, and an accessible table/list alternative. Never simply apply red shades to everything. Users can tap a region for exercise list and relevant history; template editing can offer candidates for that specific template's uncovered regions; no unsolicited undertrained-muscle recommendation feed.

**Acceptance gate for artwork:** every path must be appropriately licensed, named, mapped and selectable; source vector masters must remain editable, with no hidden raster-only hitmap. Automated geometry tests and screenshot snapshots are mandatory on both front and back. Detailed contract: `ASSET_PIPELINE.md` and `anatomy-render-contract.schema.json`.

### 4.4 Onboarding

One local profile: date of birth, sex solely where needed for appropriate reference and calculations (offer 'unspecified' with fallback/warning), height, weight, optional manual body-fat, training goal, protein g/day, energy goal (calculated or edited), default PPLUL plan, unit preferences. Optional circumference measurements; optional watch integration and API keys **after** basic setup. Explain that BMI/body-fat/muscle strength/calorie expenditures are estimates or user measurements as appropriate. No account registration required.

## 5. Exercise database and custom exercise workflow

### 5.1 Catalog goals

Bootstrap from legally reusable metadata (e.g. public-domain `yuhonas/free-exercise-db` 800+ movements) then expand variations, canonical aliases, newer online names such as **Bayesian cable curl**, incline Smith press, cuff lateral raise, unilateral cable patterns, machine variants and regional names. Do not falsely promise complete TikTok coverage; implement additive updates and custom creations.

Canonical exercise identity includes movement pattern, equipment, attachment/handle, unilateral flag, relevant loading convention and other factors that make strength comparisons materially different. Search supports aliases, transliterations, fuzzy queries, muscle filters, and equipment filters. De-duplicate synonymous aliases while preserving distinct load variants. Local catalog fully offline and licensed source/data-version attributed in About.

### 5.2 Anatomical mappings

Each exercise specifies subdivisions, primary/secondary role and contribution coefficients. Default heuristic: primary=1.0 and secondary=0.35, reviewable on a per-exercise basis, never interpreted as exact physiological stimulus. Regions not separately inferable are tagged `group-level only`; prohibit false-precision scores for individual heads. For custom exercises, users may tap regions manually and adjust primary/secondary assignments. Built-in mappings are immutable reference records with user overrides layered on top.

### 5.3 AI-assisted addition

Enter name/alias -> `Research exercise` -> choose provider's native web search if *actually supported* or separately configured web-search API -> retrieve source URLs -> structured output schema -> validate anatomy IDs, movement/equipment, load conventions, synonyms, confidence, source dates -> present comparison preview -> user edits/approves -> save. No AI-generated record bypasses validation or approval. Offline/manual path always available. Unknown anatomy subdivision must be reported, never silently created.

### 5.4 Exercise detail UI

Show name, aliases, equipment, affected groups/subdivisions, primary/secondary split, mapping confidence and explanatory caveat. Use a simple native 2D zoom/translate toward the relevant front/back SVG regions, then Reset; never invoke a 3D camera. Training-history graphs show date, best comparable strength estimate, load/reps, working sets, training volume and PRs with a formula/version label. No tutorial video required.

## 6. Workout logging and active-session semantics

### 6.1 Template creation

A template is a playlist. Add exercise via searchable catalog, set **number of working sets only** (required), reorder, group supersets, clone template, reorder templates within group, version notes. Rep target and prescribed load are not mandatory; previous values may appear as editable guidance. The template body banner shows aggregated muscle-target allocation for planned sets; tapping a subdivision lists compatible exercises to insert into the template.

### 6.2 Session lifecycle

`not_started -> active -> completed` or `discarded`. Start records UTC instant and local time. Active timer derives from persisted start timestamp and continually shows elapsed time; no pause button. Session survives device lock, leaving the app and background process eviction by transactional logging and restoration. On actual reboot, restore as `interrupted/review` without pretending a service continued running; timestamp-based elapsed duration remains available for user review. At completion save end timestamp. Profile > Logs displays chronology, duration, template/version and manual start/end adjustment with chronology validation and recalculation of duration.

Never force a user to enter 'top weight' after workout; derive it from performed sets. Users can review and correct any set before finalization; corrections after finalization re-run deterministic derived computations and undo/reissue relevant gamification events idempotently.

### 6.3 Set editor

Each completed set captures exercise identity, displayed previous performance, weight, reps, side (`both`,`left`,`right`), and optional notes. Failure is implicit; optional marking of incomplete/unperformed invalidates ranking for that set. **Drop sets:** `+ drop` appends new `(weight,reps)` components in order to one performed set; show each segment in the UI and never confuse the sum of drop reps with one continuous set for 1RM. **Supersets:** link exercises with common circuit ID without adding a mandatory timer. **Unilateral:** choose mirrored/individual-side entries; defaults symmetrical, but unilateral asymmetry can be recorded and surfaced. **Load conventions:** dumbbell weight *per hand*, cables *displayed stack load*, barbells total external load inclusive of bar, Smith displayed loading notation, bodyweight-plus-added vs assisted weight, machine proprietary setting. Exercise-specific conversion metadata must govern comparability; never sum two dumbbell weights just to match a barbell score.

Warm-up sets may be marked explicitly but are excluded from strength, plateau and rank estimates unless user intentionally changes their type. For a log representing an absolute-to-failure working set, no RIR input is required.

### 6.4 Session summary

On Finish show total duration, exercise and set counts, ranked-comparable top efforts, PRs, estimated region workload, XP awarded, updated LP/ranks, plateau status and next group template. Include editable notes and links to each exercise chart. Distinguish 'No new comparable data' from 'No progress'. End-of-session summaries are deterministic without AI; optional AI narrative is secondary.

## 7. Calendar and automatic rotation

### 7.1 Scheduling

Default mapping: Monday Push, Tuesday Pull, Wednesday Legs, Thursday Upper, Friday Lower, Saturday/Sunday rest. User can remap weekdays/groups while keeping **fixed weekdays** by default. The calendar supports month/week/day, planned vs completed sessions, active template letter, status indicators and history drilldown. Missing a scheduled day is flagged with a nonblocking confirmation when the user next opens the app (and optional notification); **never move subsequent workouts automatically**. A missed day does not increment any plateau counter or automatically count as completed.

### 7.2 Template history as lightweight Git

Implement immutable `TemplateVersion` snapshots with version ID, parent version, timestamp, message and structured diff (added/removed/reordered exercises, prescribed sets, superset groups). View history, compare versions, restore by creating a *new* version derived from an old version. Do **not** require a Git executable on-device. Every completed workout stores the exact template version snapshot.

A materially changed mapping/sequence invalidates comparability for affected regions and resets template stagnation streak. A purely cosmetic change (name, color, notes) does not. Define materiality mechanically: exercise canonical IDs changed, set counts changed, region mappings changed, or load conventions changed; reordering only without supersets is cosmetic, but superset changes are material if they impact what is compared. Preserve old session/PR history regardless.

### 7.3 Plateau engine v1 — deterministic specification

1. **Input:** a completed session for active Template `T`, with immutable template version `V`, working sets and calibrated/uncalibrated exercise performance outputs.
2. **Comparable exercise:** same canonical exercise **and loading convention** occurs in current session and previous completed attempt of the **same template lineage**, with adequate quality and no material version change. An exercise newly introduced into a template first establishes a baseline.
3. **Best effort metric:** for calibrated exercises compute reliable strength estimate `P(e,s)` from eligible primary working sets, not drop components; see Section 8. No cross-equipment raw comparison. Each attempt stores its best valid performance score and raw supporting set.
4. **Exercise change:** `d_e = ln(P_current / P_previous)` with both `P>0`. A meaningful improvement requires `d_e > t_e`, where `t_e` is a declared threshold derived from rep/count/load uncertainty; default min threshold 1%, with a higher threshold for noisy/high-repetition observations. Threshold function is versioned and tested. Unknown/unreliable values are ineligible, not failures.
5. **Subdivision contributions:** assign each comparable exercise to all mapped subdivisions with positive heuristic coefficient `c_(e,m)`. Define `w_(e,m) = c_(e,m) * min(4, valid_working_set_count_e)`; clips prevent very high set counts from swamping others. For each region `m`, `D_m = sum(w*d_e)/sum(w)` over comparable supporting exercises. Record contributors, weight sum and uncertainty.
6. **Region eligibility:** region belongs to current template's mapped target set, has at least one comparable supporting exercise, and its current/prior mapping is consistent. An uncertain AI-only mapping not approved is not eligible. A region with insufficient data stays `unknown`.
7. **Region stagnation:** eligible `m` counts as stagnant if `D_m <= T_m`, where `T_m` is a transparent propagated noise threshold (default 1%, may be stricter based on supporting exercises). Negative changes count as stagnation; `D_m` above threshold means progress.
8. **Coverage gate:** at least three eligible subdivisions **and** eligible subdivisions must represent >=70% of current template's total heuristic planned muscle-target weight (calculated from all mapped regions). If not, label result `insufficient comparable evidence`, **do not advance or reset** the three-session counter until next eligible completed attempt.
9. **Session status:** if `stagnant_subdivisions / eligible_subdivisions >= 0.50`, mark this eligible attempt `stagnant`; otherwise `progressing` and reset the counter to zero.
10. **Trigger:** three consecutive **eligible stagnant attempts of the same active template/version lineage** => automatically advance active-template pointer to the next template in group cyclic order **for its next scheduled date**. Create one rotation event and one user notification; reset new template's active streak only if its prior streak belongs to an invalidated baseline; other template histories remain intact.
11. **Idempotency:** evaluating the same session multiple times must not produce duplicate rotations or awards. On post-hoc edit, replay this template's session events in chronological order from the last valid checkpoint, recompute active pointer and report differences in an audit log.

#### Plateau worked example

Template Push A trains eight subdivisions. On three *comparable* completed Push A occurrences, five, four, and five subdivisions, respectively, stagnate; all pass the coverage gate. Ratios are 62.5%, 50%, 62.5%, so after the third attempt the **next Push day** selects Push B. Which specific subdivisions stagnated may differ each time. If the third session has only 40% comparable muscle-target coverage, it remains `insufficient evidence` and does not trigger rotation.

#### Important limitation

This is **muscle-region performance inference** using trained exercises and heuristic allocations. It cannot measure regional hypertrophy or isolated neural strength. UI copy must say 'relative performance', 'workload', and 'estimated recovery', never 'measured muscle development'.

## 8. Strength mathematics, ranks, XP and LP

### 8.1 Observable quantities and conservative comparison

Represent an observation as `{exerciseVariantId, equipment/loadConvention, loadKg, reps, isWarmup, isDrop, side, timestamp, source}`. Only completed primary working-set components with `reps>=1` and reproducible loads qualify. Unknown or assisted-equipment semantics require exercise-specific evaluators; never use arbitrary global 'cable multiplier' or 'machine intensity multiplier'. Cable torque profile depends on line of force and geometry; dumbbells are not uniformly disadvantaged.

For ordinary external-load exercises in a reasonable range (default **1–15 reps**, configurable per exercise), provisional **Epley estimated 1RM**:

```text
E1RM_kg = loadKg * (1 + reps/30)
```

Use measured 1-rep load directly where appropriate. High-rep values (>15) are logged normally but flagged `low_confidence` for 1RM; >30 reps ineligible for default ranked comparisons until exercise-specific calibration exists. These cutoffs are application rules, **not medical or biomechanical constants**. Some movements (bodyweight, assisted pull-ups, machine stacks, bilateral/unilateral loading) require explicit domain-specific conversion and may remain `unranked` initially. User can always see raw PR and history even when unranked.

Do not compare dropset combined reps as an ordinary set. Do not treat 32 kg x 4 as universally superior/inferior to 30 kg x 8; expose metric and confidence. For multiple qualifying sets, choose the highest reliable estimated score, tie-break by higher reps within comparable loads and latest performance. When confidence differs materially, display both top raw set and best eligible estimate.

For exercise performance progress, prefer compare `E1RM` in stable reps/loading conditions and add reliability-aware `t_e` threshold (default floor 1%). Store formula identifiers (`epley-v1`, `assist-pullup-v1`, etc.), original inputs and canonical exercise variant to permit deterministic recomputes.

### 8.2 Population-standard calibration

For a given movement and sex/bodyweight category, a populated external standards table may map performance to a rank score `R in [0,1000]`. Must store dataset source, rights/license, update/version date, reference population, bodyweight bins, calibration version, interpolation/extrapolation method and confidence. Never pretend local single-user training provides a representative global percentile distribution.

**Fallback when no licensed calibration exists:** use unranked performance and deterministic **personal progression XP / personal benchmark** with an explicit label; do not fabricate an 'elite' population rank from arbitrary numbers. For group/overall ranks, only aggregate calibrated supported exercises and show **coverage**. Optionally display a separate clearly named `Personal Rating` across all exercises; do not conflate it with population-comparable tiers.

Exercise score -> group score: form a stable weighted robust average of strongest representative calibrated exercise scores in each group, with contribution caps and min evidence; use same contribution rules independent of training frequency. Whole-body score: weighted average of eligible group scores, with coverage threshold (proposed >=4 of 6 main groups) and missing category callouts. Ranks cannot be raised merely by performing many repeated sets of the same exercise. Version group weights in a config file and provide a migration/recalculation path.

### 8.3 Nine rank tiers and LP

Preserve the familiar nine names and colors, but render **original artwork**, not copied proprietary emblems. Our score thresholds (initial design contract):

| Rank | Score inclusive | Color direction |
|---|---:|---|
| Wood | 0–199 | earthy brown |
| Bronze | 200–299 | copper |
| Silver | 300–399 | silver |
| Gold | 400–499 | yellow/gold |
| Platinum | 500–599 | teal |
| Diamond | 600–699 | cyan/blue |
| Champion | 700–799 | purple |
| Titan | 800–899 | red/coral |
| Olympian | 900–1000 | amber/glowing gold |

`LP within rank` uses 0–99 normalized **within the current tier**, so Wood's wider 0–199 band still yields 100 LP display steps:

```text
tierMin, tierMax = chosen rank interval
rankLP = min(99, floor(100 * (clamp(score,tierMin,tierMax)-tierMin) / (tierMax-tierMin+1)))
```

Display overall score next to LP. This is an original deterministic tier representation, **not an asserted reverse-engineering of Liftoff's proprietary LP computation**. Upon score crossing tier boundary show promotion animation and retain event history; demotion rules default to updating from recalculated score, with user-facing explanation; no punishment for rest days or inactivity. LP and rank reflect performance, not XP.

### 8.4 XP, levels, streaks and quests

XP is an append-only, idempotent activity economy (no purchasable currency). Initial transparent v1 award policy:

- Complete eligible scheduled or manually started session: **100 XP**, once per session.
- Each uniquely completed programmed exercise: **10 XP**, capped at 10 distinct exercises/session.
- Each new qualifying exercise PR: **25 XP**, capped at 4 PRs/session.
- First completed workout for that scheduled training weekday: **25 consistency XP** (no additional XP for deliberately splitting the same day into duplicate sessions).
- Daily quests and periodic achievements have explicit, versioned award values and claimed-event IDs. No XP for entering fake extra sets or simply reopening a session.

Levels: `level=1` at XP=0; level `L+1` reached when cumulative XP >= `sum_{k=1..L}(200 + 50*(k-1))`. Present next-level progress and milestone badges. Values are tunable via future **scoring-version migration**, not silent balance patches to old data.

**Streak:** counts consecutive *scheduled training opportunities completed*, with user-approved miss/skip statuses distinguishable. Saturday and Sunday rest do not break the streak; normal streak progression resumes on Monday. Missing Monday and training Tuesday starts a new streak unless Monday was intentionally marked an approved schedule exception. Quests must not incentivize skipping prescribed rest days. Four-month seasons reset only seasonal quests, seasonal records and leaderboards of one's own previous seasons; **never** erase lifetime rank, XP, PRs or history.

### 8.5 Consistency and audit contracts

Every XP award, rank change, promotion, quest completion, season transition and template rotation has `(eventId, sourceSessionId, algorithmVersion, createdAt)`; database uniqueness prevents duplication. Editing/deleting a session creates compensating events or triggers deterministic replay, never hidden score drift. Statistics panels explain what changed, compared with what, and whether data is estimated.

## 9. Exercise-to-muscle workload and visualization calculation

For a period `[start,end)`, training-stimulus proxy for subdivision `m`:

```text
stimulus_m = sum_over_completed_working_sets(e,s)
             contribution(e,m) * setFactor(e,s)
```

Default `setFactor=1` per completed normal working set. For a drop chain, its initial component counts as one set; additional drop components contribute configurable partial work (default **0.5 per drop component**, cap overall setFactor at 2.0). These factors are **visual workload heuristics**, not equivalent physiological effective sets. A more sophisticated evidence-based model can replace the proxy when justified; preserve algorithm-version labels. Display actual raw exercise sets alongside estimates so users can inspect how heatmaps were formed.

Normalize visualization **within selected period and between muscle subdivisions only with user-visible legends**, avoiding comparisons where muscle size or role makes raw set counts misleading. The Template Targeting view uses prescribed sets and mapping coefficients, no workout-performance history; it explains relative planned coverage and suggests exercises for the selected subdivision. `Muscle Balance` indicates relative distribution, not ideal development. `Estimated Recovery` is heuristic from recent region exposure and configurable decay, labelled experimental; it must not claim medical readiness.

Group rank color and subdivision workload coloration **must be independent layers**: showing upper chest as blue and mid chest as orange does not mean upper chest has a higher measured absolute strength rank.

## 10. Nutrition, EFSA references and energy balance

### 10.1 Food database contract

Food entries are supplied primarily through user-controlled JSON, not a mandatory online catalog. The system must ship an **exportable, fully populated import template** and JSON Schema defining every supported nutrient key and unit. File supports multiple items per batch, including foods, recipes and diary entries; no open-ended nutrient dictionary whose keys are guessed by AI. Support both `per_100g` and verified serving conventions converted to 100g internally; `null` means absent/unknown and is distinct from an actual measured `0`.

For each food: name, aliases/brand (optional), data source, edible weight, basis, macros, micros, uncertainty/coverage flags, unique external key (optional). The exact canonical field registry is in **`food-import.schema.json`** delivered beside this specification. A ChatGPT-generated food JSON must pass the same local parser, unit/range checks and preview as a manually written file. Duplicate matching uses stable source ID when available or explicit user resolution; never silently overwrite an existing food.

Daily logging: choose food/recipe, enter weight in grams or permitted portions; instant totals and remaining values. Also accept imported diary entries pre-assigning quantities/dates. A recipe is ordered ingredients plus raw/cooked basis, yield weight and number of portions; compute nutrients from ingredients then normalize using recorded final yield. Prevent recipe-reference cycles and distinguish cooked vs uncooked food variants. Diary entries store nutrient snapshots so historic days are stable even after modifying an ingredient; optionally 'recalculate using newest food definition' with explicit preview.

### 10.2 Nutrient completeness and reference tables

Nutrient vocabulary includes: energy; protein; total carbohydrate, sugar, fiber; total fat, saturated, mono-/polyunsaturated/trans fats, cholesterol, starch, added sugars, omega-3 (ALA/EPA/DHA) and omega-6 linoleic acid; vitamins A, B1, B2, B3, B5, B6, B7, B9, B12, C, D, E, K; calcium, chloride, chromium, copper, fluoride, iodine, iron, magnesium, manganese, molybdenum, phosphorus, potassium, selenium, sodium, zinc, choline. All names and units are explicit in the accompanying JSON Schema. Additional nutrients require schema and migration versions, not silently accepted arbitrary keys.

Load a **versioned reference table derived from EFSA DRV Finder** with provenance and age/sex/life-stage applicability. EFSA DRVs are population reference values, **not individualized clinical prescriptions**. Do not scale all micronutrient requirements to height/weight. EU folate references and folate DFE estimates use different definitions: apply an explicit validated conversion or display total folate separately, never compare mismatched units silently. Some targets vary by age, sex or energy intake; unavailable reference -> `no EU value` label, never an invented number. Where applicable show lower target/adequate intake, established tolerable upper intake (if actually defined for category), grams or mg/µg consumed, percent, source/version and confidence/data completeness.

Daily percent display = `consumed_known / selected_reference * 100`; totals with incomplete ingredient data show `at least X` and warning rather than implying total adequacy. For food with missing Vitamin C, that portion does **not** contribute zero Vitamin C. If micronutrients from 60% of today's foods are unknown, warn that percentages likely underestimate intake. For nutrients with established upper limits, UI must not suggest excessive intake is beneficial.

### 10.3 Goals and body measurements

Onboarding calculated energy suggestion using a documented resting-energy prediction (e.g. Mifflin-St Jeor where required inputs and applicability are met), activity assumptions and goal adjustment. Show assumptions and allow user override. User edits daily calories and **protein target in grams** only as primary customizable targets; carbs/fats are tracked in detail without imposing complicated macro-split targets. Optional date-based goals and training/rest-day averages as future extension, not required.

BMI=`weightKg/(heightMeters^2)` with standard adult-range caveats and disabled adult-category messaging for minors; body fat is a manually recorded percentage from the user's measurement device. Optional other body measurements. No AI-estimated body-fat from photos. Show weight/BF time-series, food trends, strength versus weight trends and note measurement methods.

### 10.4 Energy expenditure and source precedence

Present estimated **resting expenditure**, nonexercise daily activity, planned/logged workouts, additional running/cycling/walking (distance, duration, speed, optional reported calories), and total daily expenditure. Avoid presenting the result as directly measured metabolic energy.

Priority for activity data: (1) user-authorized **Chinese Mi Fitness official HyperOS HealthKit** read where approved and validated, (2) official mainland health-cloud API where approved, (3) Health Connect if verified available and populated *from China-side data*, (4) phone step sensor, (5) manual entry. Preferred source is configurable per metric. Watch heart rate may inform display and provenance, but does not by itself establish precise exercise calories.

Anti-overlap strategy: normalize activity intervals and provider sourceRecordId; never add imported whole-day **total energy** to a separately calculated resting + exercise total. If a trusted source provides total expenditure, use it as the daily primary estimate and show component breakdown only where reconciliation is reliable. Otherwise compute resting estimate + active components with time-window union/deduplication. If manually entered cycling overlaps a Xiaomi cycling record for same period, prompt to merge or choose one. Explicit `unknown` and data-coverage indicators when sensor streams are missing.

## 11. Mainland China Mi Fitness integration — official-first design

### 11.1 Non-negotiable distinction

**Mainland China Mi Fitness is not interchangeable with the global Google Play Mi Fitness.** Chinese watches/app accounts, data regions, permissions and synchronization can differ. Never assume an integration works because the Play Store product lists Health Connect. Do not require global app, Google account, Play services, unofficial watch pairing or watch firmware modification.

### 11.2 Primary: Xiaomi HyperOS 4 HealthKit SDK

Xiaomi's official **client integration guide (updated 2026-08-27)** describes a local HealthKit system service for **HyperOS 4+ in mainland China**. It lists controllers for sampled health data (`DataController`), health records and sports records; a device-side binder (`com.xiaomi.healthkit`), user authorization and developer app registration/approval requirements. This is a **documented route, not proof that an unaffiliated individual GitHub APK has been approved to use it**.

Engineering implementation:

1. Probe package `com.xiaomi.healthkit`, OS version/API, developer service eligibility and SDK initialization.
2. Register the project with Xiaomi developer services; obtain approved App ID/scopes, bind correct release and debug signing certificate fingerprints. **Do not commit private credentials.**
3. Implement `XiaomiCnHealthAdapter` behind `HealthDataSource` interface; use only approved, documented read interfaces. Request permissions for steps, energy, heart rate, body mass, activity/sport records only when used.
4. Support `unavailable`, `approval_required`, `not_signed_in`, `privacy_not_accepted`, `not_authorized`, `service_not_found`, `rate_limited`, `supported`, and `partial_data` states.
5. Restrict background reads to approved intervals; apply exponential backoff, date-window batching and idempotent ingestion. Xiaomi documentation notes rate limiting and background/lock-screen limitations.
6. Never claim the cloud has synchronized just because local data read/write succeeded; display last successful source import and per-metric provenance.
7. Do not write back workouts to Xiaomi unless a later explicit opt-in and tested use case justify it; initial adapter is **read-only** to avoid duplicates and surprising third-party cloud uploads.
8. Keep Xiaomi SDK code in a separate Gradle implementation module or optional distribution variant if its terms restrict redistribution. `base` app must compile without proprietary SDK artifacts.

### 11.3 Secondary sources and fallback

The official mainland **Xiaomi health-cloud API** exists and requires Xiaomi-account OAuth, developer application approval and user consent. Its enabled scopes may differ from the on-device SDK. Implement separately only when approved; do not embed a confidential OAuth client secret in the APK. Health Connect is a tertiary adapter only after runtime availability and actual **mainland** source records are verified. For unsupported configurations, use phone sensor/manual logs and optional user-exported data if compatible with a documented import format.

Community reverse-engineered clients or user token extraction are **not** part of the shipping integration. Do not silently scrape app private data, bypass authorization or request the user's Xiaomi account password. If official approval fails, label Xiaomi integration `Unavailable: Xiaomi developer authorization required` and retain manual data functionality.

### 11.4 On-device validation checklist

- Verify HyperOS build, installed `com.xiaomi.healthkit` version and permission service binding.
- Confirm Xiaomi developer account registration/app/service authorization/signed APK identity.
- Read one step sample and one daily steps aggregate; validate units, timestamp and date boundaries against Chinese Mi Fitness UI.
- If authorized, read total and active energy separately, activity records and pulse samples; document exactly which fields are available.
- Turn off connectivity and confirm cached data/UI and core workout logging remain operational.
- Revoke authorization, lock the device, deny Google services and restart; verify graceful errors.
- Compare overlapping manually entered activity versus Xiaomi import to verify one-count-only totals.

### 11.5 Sources for this integration

- Official mainland SDK: https://dev.mi.com/xiaomihyperos/documentation/detail?pId=2332
- Official server authorization: https://dev.mi.com/xiaomihyperos/documentation/detail?pId=2333
- Official cloud data API: https://dev.mi.com/xiaomihyperos/documentation/detail?pId=2328
- Android Health Connect availability requirements: https://developer.android.com/health-and-fitness/health-connect/availability

## 12. Live Updates, HyperIsland and background behavior

For user-started active workouts on Android API **36+**, attempt Android standard **Live Updates**: appropriately permissioned promoted ongoing notification with qualifying standard styles, title, ongoing flag and system-managed promotion. OEMs may additionally gate display, so the app must check eligibility and offer a persistent standard notification fallback. `Notification.ProgressStyle` is available on API 36; content may show elapsed duration, current exercise and completed/total working sets. Tapping opens the current session; do not put nonessential dynamic UI in a custom remote view that disqualifies promotion. On other Android versions, normal foreground workout notification.

Avoid false promise of a native 'island expanded card' layout: Xiaomi controls how eligible standard Android Live Updates appear in HyperIsland. Feature verification requires screenshot/screen-recorded real-device test on HyperOS 4.0.0.41 beta. The workout's start/end timer is anchored in persisted timestamps, not a forever-running background ticker. Foreground service type/permission choice must satisfy current Android restrictions; do not create a foreground service solely to count time if persistent state + notification suffices. Watch or sensor observation is a separate, permissioned activity.

Official Android live updates: https://developer.android.com/develop/ui/views/notifications/live-update

## 13. AI integration, search and reproducible cache

### 13.1 AI settings and scope

One or more user-configured providers: OpenAI-compatible Chat Completions or Responses adapters as supported, custom endpoint/model, and explicit DeepSeek preset. Do not hardcode a particular 'Flash' model ID; validate provider model list or accept model ID from user. Provider capability detection: tool calling, native web search, structured JSON output, token accounting, streaming; do not infer search from marketing labels or compatibility alone.

Individually controlled AI feature toggles: web-assisted exercise definition, plateau explanation, nutrition interpretation, and natural-language history query. Optional search provider contract (e.g. Brave Search; alternate Exa/Tavily) is separate from model provider and requires its own key where native search isn't available. The AI must show source URL/date for web-supported facts and mark unsupported claims.

### 13.2 Data scope and privacy

Request permission before sending a slice of locally stored workout/nutrition data externally. Use minimum necessary structured summaries and redact optional personally identifying fields. Keys encrypted via Android Keystore-backed symmetric encryption, never committed/logged/exported. Warn explicitly that enabling cloud AI or web search sends user-selected content to third-party providers; offline functionality is unaffected. Restrict arbitrary base URLs to HTTPS by default; allow HTTP localhost only with explicit developer-mode warning.

### 13.3 Caching and budgeting

Cache key = SHA-256(`featureId|provider|model|modelOptions|promptVersion|schemaVersion|relevantRecordRevision|normalizedInputs|retrievalEvidenceHash`). Reuse only when its dependency records are unchanged; a chest workout edit must not invalidate unrelated recipe-analysis results. Web-based results may have TTL regardless of local data (default 30 days for anatomy research; configurable). Expose per-feature token/search usage, estimated cost when provider rates configured, session budget cap and a button to clear cache.

### 13.4 Guardrails

Never let AI rewrite training session data, award XP/LP, rotate templates or overwrite nutrition facts automatically. AI outputs pass rigid schemas and user approval. For natural-language history questions, prefer translating intent into **allowlisted local read-only queries**, then passing compact results for summarization. Do not execute arbitrary generated SQL or shell code on the device.

## 14. JSON backup, import/export and migration contract

Export all local profile values, recorded measurements, catalog overrides/custom exercises, versioned templates, completed/active workouts, set/drop components, ranks/XP event ledger, body-region maps, plans/calendar, food/recipes/diary, activity samples/source provenance, widget layouts and configuration **excluding secrets**. Include schema version, app version, UTC timestamp, manifest/hashes and per-dataset counts. JSON must remain human-readable. User can choose full export or subset (food template, foods library, diary log). Export uses Android Storage Access Framework document picker. No broad storage permission.

Restore is a **transactional, validated operation**: stage and parse file, validate schema and references, display conflicts/data counts, choose merge or replace, keep a pre-restore safety copy, import inside transaction, then verify row counts/checksums. Fail entirely (not half-restore) if corrupted. Preserve immutable history and handle IDs on merge with explicit mapping. Schema forward compatibility: unknown major version produces explanatory failure, unknown supported optional fields can be ignored only if the schema permits. Test restore from multiple prior schema versions and duplicate imports.

For daily food import and exercise creation, separate JSON schemas are downloadable directly inside Settings > Import/Export with example fixture files.

## 15. Settings, logs and reports

**Settings groups:** profile/measurements, calorie/protein goal, units, language, theme and performance, animation accessibility, in-app dashboard reset, workout behavior, plateau/noise thresholds (advanced), strength scoring version explanation, ranks/season rules, integration sources, AI providers/keys/search budgets, import/export, notifications/live updates, license, about and diagnostic capability report.

**Profile > Logs:** session timeline with start/end, manually editable date/time, duration, exercises, sets, ranking events and versioned template reference. A separate audit/history screen shows imports, rotations, template edits, AI data requests (without sensitive content), score recalculations and adapter errors.

**Reports:** deterministic after-workout, weekly and monthly dashboards: sessions completed/missed, scheduled split compliance, working-set counts, region workload, top comparable performance change, PRs, ranks and XP, plateau rotations, weight/body-fat, intake/protein, micronutrient completeness, energy source breakdown and activity totals. Optional AI explanation is clearly supplementary and cached.

## 16. Accessibility, performance, security and quality gates

- Minimum 48dp touch target and accessible content descriptions on all anatomy hit areas; provide semantic text list alternative; scalable fonts and TalkBack-compatible controls.
- 60 fps **target** for standard navigation and anatomy transitions on target device; heavy visual effects automatically fall back to efficient mode on thermal/battery constraints. No freezes from Room/JSON parsing or remote AI on main thread.
- Cold start and navigation benchmarks recorded on Xiaomi; proposed goals cold start <2.5 s and common tab switch <250 ms on warm state, measurable and subject to device test.
- Background battery: no fixed wake-lock or continuous polling for passive workout timer. Optional sensor watch stream operates only when authorized and active.
- Log each set transactionally and expose user-visible autosave state for failure recovery. Gracefully handle process kill/flight mode/time-zone change/reboot.
- Android Keystore protects API keys; no secrets in source, backups, support logs, screenshots of diagnostic pages. DB is private app storage; JSON export protected by Android document picker access.
- Versioned migrations are non-destructive by default; verification test for every migration. Block unsafe destructive fallback.
- Crash logs default local only, no unsolicited telemetry; remove personal information before user-initiated bug-report export.
- Repo uses reasonable license (choose at project bootstrap), third-party notices/data licenses, dependency locking, SCA scans, static analysis, Kotlin format, conventional commits, tests and release notes.
- On beta HyperOS, defensively code platform integrations behind feature flags and degrade gracefully rather than shipping invasive system hooks.

## 16A. Complete design / tooling / agent-skills inventory (approved scope)

**This section is the authoritative Codex inventory of all tooling and agent-skill families discussed after v1.0.** The complete URLs, roles, applicability and compatibility hierarchy are in `TOOL_AND_SKILL_MATRIX.md`; project-specific, testable asset requirements are in `ASSET_PIPELINE.md`. The inventory is documentation, **not** an instruction to install all skills or execute all concurrently on every task. The user's preference is to **include and make available every discussed core and optional skill**, activating relevant ones as needed to avoid conflicting architecture rules.

**Native tools and libraries:** Kotlin, Java/JDK, Gradle wrapper + Kotlin DSL/Android Gradle Plugin, Android SDK/Build Tools/Platform Tools/ADB, Android Studio (IDE/debugger/preview/profiler), Jetpack Compose, Material 3, Compose Canvas and Animation, Room/SQLite, DataStore, Coroutines/Flow, DI (Hilt if justified), AndroidX Navigation, lifecycle-aware state, WorkManager when necessary, Glance/App Widgets, Android Notifications/Live Updates, Android Health Connect *only as verified fallback*, Xiaomi mainland HealthKit adapter, optional official mainland cloud API, OkHttp/Ktor (choose one HTTP stack), Android Keystore, Kotlin serialization/JSON Schema validation, Compose UI tests, screenshot tests, Android Lint, formatting/static analysis, Gradle test/build and CI, Git/GitHub/GitHub Actions, ADB/scrcpy hardware validation, Inkscape for master SVG refinement. Optional visual animation runtimes **Lottie Compose** and **Rive** remain evaluated alternatives, not mandatory dependencies; primarily use Compose native animations. **Kyant0 AndroidLiquidGlass** is candidate render engine behind an abstraction; never wire it into domain modules. No Docker, Node, Flutter, Firebase, Compose WebView anatomy or 3D dependencies are required by the app; do not build using them simply because a skill example mentions them.

**Agent skill sources all included in the approved reference roster** (former core and former optional):

1. `android/skills` — official Google Android skills, edge-to-edge, testing setup, performance and lifecycle recipes.
2. `haidrrrry/compose-kotlin-agent-skills` — Kotlin/Compose architecture, MVI/MVVM state, testing, coroutines.
3. `Kotlin/kotlin-agent-skills` — official Kotlin language/build ecosystem guidance.
4. `Kashif-E/KMPLiquidGlass` — Compose/KMP glass design guidance (verify availability and compatibility).
5. `ulugbekeshnazarov409/mobile-design` — mobile design hierarchy, typography, restraint and accessibility.
6. `skydoves/compose-performance-skills` — performance, recomposition, rendering, baseline profiles.
7. `skydoves/android-testing-skills` — instrumentation, Compose UI, ADB and JVM testing.
8. `suchobits/android-skills` — design systems, architect, modularization, lint enforcement, testing and CI.
9. `hamen/compose_skill` — `compose-agent` and `jetpack-compose-audit`.
10. `kizaki-R/compose-liquid-glass` — AGSL, refraction and performance guidance.
11. `kizaki-R/android-ios-liquid-glass-tabbar` — floating pill nav and gesture/spring UX guidance.
12. `krutikJain/android-agent-skills` — broad Kotlin, Room, Hilt, Gradle, CI and security reference.
13. `liuyi0808/android-review` — architecture/Compose/Kotlin/performance/security independent review.

**Gym RPG-specific skills**, designed in the repository when feature work begins: `gymrpg-design-system`, `gymrpg-anatomy`, `gymrpg-strength-engine`, `gymrpg-workout-rotation`, `gymrpg-nutrition-engine`, `gymrpg-xiaomi-integration`, `gymrpg-quality-gates`.

**Routing and conflict policy:** This requirements document, schemas, decision register and acceptance fixtures outrank external skills. Official Android/Kotlin guidance is preferred for platform correctness; selected architecture must stay consistent across modules; external skill recipes may advise alternative patterns, but their installation must **not silently alter our minSdk, gradle plugin set, UI style or app's functional semantics**. Review findings require evidence and material impact; ignore cosmetic nitpicks. Each milestone must include screenshots and performance evidence, not merely a passing APK build.

## 17. Milestones, deliverables, and acceptance gates

| Stage | Delivery | Exit criteria |
|---|---|---|
| M0 — Technical proof | Xiaomi capability-report screen, **Google Sans Flex typography proof, glass full/efficient comparisons, 2D front/back SVG path selection + zoom proof**, bare APK CI | Sideloader runs; safe insets; test exact SDK/Android version; official HealthKit eligibility documented |
| M1 — Foundation | **Glass pill navigation**, onboarding, Room, theme modes, home widget grid, JSON backup shell | Complete offline onboarding, app restart persistence and backup smoke test |
| M2 — Workout core | exercise seed catalog, aliases, template playlists, logging, drop sets, supersets, active session/notification | 10+ representative set scenarios, restart recovery, historical snapshots and exercise chart tests |
| M3 — Calendar & versioning | PPLUL scheduling, template history, region plateau engine, sequential rotation | All plateau fixtures pass; missed days never move next workout; restored version is new version |
| M4 — Anatomy and ranked RPG | annotated 2.5D front/back map, mapping engine, rank cards, LP/XP, levels, quests, streaks, seasons | taps/legend/accessibility work; score and XP idempotency; no invented ranks without reference |
| M5 — Food and energy | strict JSON import, recipes, diary, EFSA tables, calculated calories, reports | Schema validation, unknown vs zero, nutrient math, energy de-dup fixtures pass |
| M6 — AI and platform | configured AI/search, caching, live updates, launcher widgets, Xiaomi CN HealthKit adapter if approved | No-key/offline path works; no duplicate activity; Xiaomi integration test result truthfully reported |
| M7 — Release QA | UI polish, CI, security review, performance audit, complete documentation and signed APK | Restore clean-install backup, full test suite, Xiaomi screenshot audit, release notes and checksums |

**Rule:** External-access blockers (SDK authorization, weathered Xiaomi beta quirks, proprietary score calibration, purchased anatomical assets) cannot block unrelated milestones. Mark gated features as experimental/unavailable in Settings until they pass device tests.

## 18. Acceptance tests / objective scenarios

| ID | Given / action | Expected |
|---|---|---|
| A01 | Launch with no Google account/network | Onboarding, templates, logging, calendar and food records work |
| A02 | Xiaomi 17 Pro with gesture bar + cutout | No bottom dock/CTA, top overlay or keyboard field is obscured |
| A03 | Start at 15:00, background app, lock phone, reopen at 15:47 | Session elapsed time 47 min, sets intact, active notification present where allowed |
| A04 | Force-kill mid-set logging; reopen | All previously committed sets intact; active session recoverable |
| A05 | Log 30 kg x 8 vs 32 kg x 4 same dumbbell movement | Raw performances and formula-consistent strength estimates visible, not raw-load-only ranking |
| A06 | Drop set: 30x10, +20x6, +15x5 | Three components saved in same drop chain; 1RM uses qualifying 30x10 component only |
| A07 | Six mapped subdivisions; three stagnate per eligible attempt three times | Third attempt triggers next-template rotation for next *matching weekday*, exact-50% included |
| A08 | Region coverage only 30% due to new exercises | `Insufficient evidence`; streak held; no forced rotation |
| A09 | Edit template exercise then restore old version | Old sessions remain linked to original immutable versions; restore creates new version |
| A10 | Miss Wednesday Legs workout; open Thursday | Missed prompt shown; Thursday Upper remains Thursday; plateau unaffected |
| A11 | Add AI-generated Bayesian Curl with unknown mapping ID | Schema rejects invalid ID; user can edit/approve; no silent database change |
| A12 | No AI key and no internet | Local exercise creation and all established modules still work |
| A13 | Import foods with `vitamin_c_mg: null` | Daily Vitamin C shows incomplete-data warning, not a fabricated zero |
| A14 | Import JSON containing ten foods, two recipes, diary entries | Validation preview then one atomic import; duplicate IDs resolved explicitly |
| A15 | Enter recipe raw ingredients and cooked yield | Calories/micros scale to consumed grams of final recipe, not unadjusted weight |
| A16 | Xiaomi steps and a manual walking activity overlap | One source is chosen/merged; expenditure not double counted |
| A17 | Official Xiaomi HealthKit denied/not installed | Clear status; app works offline and accepts manual activity |
| A18 | Android 16+ Live Update disallowed by OEM | Valid persistent notification fallback without UI breakage |
| A19 | Submit same session completion event twice | XP/LP, plateau counter and rotation events not duplicated |
| A20 | Export backup, uninstall, reinstall, restore | Equal profile, sessions, templates, food/diary, scores and settings, excluding API keys |
| A21 | AI invoked twice with identical inputs | Second request cache hit, no redundant provider call; relevant data edit invalidates |
| A22 | Saturday and Sunday no workout, Monday resumed | Planned-rest days do not break training streak |
| A23 | Historical food definition edited | Historical diary snapshot unchanged without explicit recalculate action |
| A24 | Map mode toggled quickly with screen reader on | Region labels accurate, contrast accessible, no stuck/incorrect selection |
| A25 | Feature fails on beta HyperOS | Controlled fallback logged with diagnostic; no crash or data loss |

### Test strategy

- **Pure JVM unit tests:** numerical algorithms, import validation, timestamp/date logic, XP and rotations.
- **Property-based tests:** plateau determinism, rank monotonicity for like-for-like effort, importer round trips, recipe mass balance, data revisions.
- **Room instrumented tests:** migrations, foreign key integrity, transactional restore and concurrent set updates.
- **UI instrumented tests:** onboarding, template creation, session lifecycle, anatomy taps, home widget drag/resize, accessibility and window inset regressions.
- **Hardware matrix:** user Xiaomi 17 Pro HyperOS 4 beta as release gate; one ordinary Android reference/emulator; network off; notification permission denied; relevant Google services absent.
- **Golden fixtures:** store input sessions, expected per-region plateau classifications, rank, XP/LP event ledger and daily nutrition totals in repository. Any scoring change updates version and expected fixtures in one reviewed PR.

## 19. Coding-agent instructions and implementation discipline

This specification defines desired functionality; it does not authorize making up missing physiology, unofficial API access or proprietary vendor behavior. Before each milestone, convert the relevant sections to explicit GitHub issues or implementation tickets with acceptance tests and dependency lists.

Implement **one cohesive vertical slice at a time**. Keep PRs focused with tests; prefer minimal production code that reuses existing abstractions. No public API key, signed-release credential, Xiaomi token or user health data in repository. No Android permission without a documented feature path. When the spec says experimental, implement a capability gate and visible fallback, not a mock claiming success.

The coding agent must report: tests run, result, warnings, on-device tests not yet executed, API approvals still outstanding and follow-up issues. It must not claim HyperIsland or Chinese Mi Fitness integration works on hardware until a reproducible real-device test proves it.

## 20. Sources and external references (accessed 10 Oct 2026)

1. Xiaomi official mainland HyperOS 4 HealthKit client guide (updated 27 Aug 2026): https://dev.mi.com/xiaomihyperos/documentation/detail?pId=2332
2. Xiaomi official mainland health-cloud server integration guide: https://dev.mi.com/xiaomihyperos/documentation/detail?pId=2333
3. Xiaomi official health-cloud sample/sport API: https://dev.mi.com/xiaomihyperos/documentation/detail?pId=2328
4. Android Live Updates: https://developer.android.com/develop/ui/views/notifications/live-update
5. Android `Notification.ProgressStyle` API 36: https://developer.android.com/reference/android/app/Notification.ProgressStyle
6. Android foreground-service types: https://developer.android.com/about/versions/14/changes/fgs-types-required
7. Android Health Connect availability: https://developer.android.com/health-and-fitness/health-connect/availability
8. EFSA DRV Finder: https://multimedia.efsa.europa.eu/drvs/index.htm
9. Public-domain exercise database: https://github.com/yuhonas/free-exercise-db
10. OpenAI web search capability (provider-specific, not generic OpenAI-compatible): https://developers.openai.com/api/docs/guides/tools-web-search

### Addendum to primary references (v1.1)

- Official Google Sans Flex and OFL license: https://github.com/googlefonts/googlesans-flex
- LastWave Native typography reference (do not copy GPL implementation): https://github.com/Clash-Projects/LastWave-Native/blob/main/app/src/main/java/com/lastwave/app/ui/theme/Type.kt
- `vulovix/body-muscles` Apache-2.0 + NOTICE: https://github.com/vulovix/body-muscles
- `Jsplice/MuscleMap` MIT + asset provenance: https://github.com/Jsplice/MuscleMap
- Compose backdrop glass foundation: https://github.com/Kyant0/AndroidLiquidGlass
- Lucide icons and license: https://lucide.dev
- Lottie Android animation runtime (optional): https://github.com/airbnb/lottie-android
- Rive Android animation runtime (optional): https://rive.app
- All agent-skill repositories: see `TOOL_AND_SKILL_MATRIX.md`; no installation instructions in this pack.

## Appendix A — Configuration defaults (versioned; proposed, not scientific claims)

```yaml
initial_split: {MONDAY: Push, TUESDAY: Pull, WEDNESDAY: Legs, THURSDAY: Upper, FRIDAY: Lower, SATURDAY: Rest, SUNDAY: Rest}
rotation: {metric: muscle_subdivision_performance, threshold_fraction: 0.50, consecutive_eligible_attempts: 3, min_eligible_subdivisions: 3, min_stimulus_coverage: 0.70, policy: sequential}
exercise_stimulus: {primary_weight: 1.00, secondary_weight: 0.35, additional_drop_weight: 0.50, set_factor_cap: 2.00}
strength: {default_equation: epley_v1, best_effort_rep_range: [1, 15], high_rep_eligible_only_if_calibrated: true, improvement_floor: 0.01}
notifications: {live_updates: auto_if_supported, standard_fallback: true}
ux: {theme: dark, effects: full_or_efficient_user_selectable, orientation: portrait, anatomy_home_default: top_center}
data: {cloud_required: false, readable_json_backup: true, restore_is_atomic: true}
integrations: {xiaomi_cn_healthkit: primary_if_approved, xiaomi_cn_cloud: secondary_if_approved, health_connect: tertiary_if_available, manual: always}
```

## Appendix B — Change control and unresolved *external* dependencies

| Item | Owner/action | Effect if unavailable |
|---|---|---|
| Xiaomi mainland HealthKit developer service and SDK | Apply for official permissions; verify package, scopes and APK signing on device | Mark adapter unavailable; fall back to local/manual data |
| Xiaomi mainland cloud API access | Obtain official approval, OAuth configuration | Do not ship cloud adapter; local SDK/manual unaffected |
| Third-party exercise dataset and anatomy visual assets | Verify licenses and provenance | Use original drawings or metadata with compatible rights |
| Strength population norms | Obtain a licensed/reference dataset and document its bias | Supported exercises stay personal-only/unranked until calibrated |
| HyperIsland promotion | Verify Android API level, permissions, OEM behavior on target firmware | Standard active-workout notification |
| Mi Fitness watch pairing | Watch pairs with **Chinese Mi Fitness**, independent of our app | Data reads only after watch-to-Chinese-app data exists; our app never pairs watches directly |
| EFSA nutrient tables and applicable upper limits | Author audited, date/versioned reference dataset | Show `no reference` instead of invented percentage |

**Implementation start condition:** M0 can begin immediately. There are no remaining product-definition questions blocking native Android work. External approvals determine integration availability, not core app completion.

## Appendix C — v1.1 binding decision record: changes after original delivery

- **2D beats 3D:** user explicitly rejected Blender and 3D/SceneView/Filament; use real front/back buttons, editable licensed SVG paths, native Compose Canvas, simple animated focus. This overrides all previous 2.5D rotation and later transient Blender plans.
- **Typeface:** official OFL Google Sans Flex, independently styled after LastWave's variable-axis typography. Do not copy GPL theme source.
- **Assets:** editable vector masters, individually addressable 52-taxonomy mapping, original tier emblems and app icon; license/provenance ledger, quality check, source files and Android-ready native geometry.
- **Glass:** dark-first Apple Music-inspired glass language, switchable Full/Efficient, Kyant's Backdrop a candidate not a promise, pill nav separated from touch-system areas, GPU budget and accessibility controls.
- **All skills and tools:** core + optional skill catalogs retained for on-demand routing; Kotlin/Android/Compose/CI/icon/SVG/animation/profiling/test tool registry included; no installation playbook.
- **No change to core business rules:** fixed-weekday PPLUL, versioned templates, >=50% eligible subdivisions stagnant for three *comparable same-template* performances, sequential rotation, local-only ranked XP/LP system, JSON food import/micros, China-only Xiaomi preference, native Android Live Update with fallback and optional AI.
- **No unauthorized assumptions:** do not assume precise Liftoff scoring, protected Xiaomi interfaces, compatibility of specific alpha glass versions, 52 visually unique anatomical polygons, or verified graphics performance. These each require proof or documented fallback.

