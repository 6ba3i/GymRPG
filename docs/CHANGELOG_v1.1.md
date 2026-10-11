# Changelog — Specification v1.1 from v1.0

**Scope:** This is a *consolidated replacement* of the whole requirements pack, not a patch solely for the anatomy model.

### Added or changed since v1.0

- Replaced undecided 2.5D/3D discussion with **final native 2D front/back SVG anatomy**: buttons, quick crossfade, touch selection, per-region coloration and scale/translate zoom; rejected Blender/GLB/Filament/SceneView/WebView anatomy.
- Documented the **two vetted upstream SVG candidate repositories**, licensing and the non-one-to-one mapping between visual paths and 52 taxonomy subdivisions. Added strict geometry contract and coverage checklist.
- Locked typography to **Google Sans Flex** from OFL upstream, with **LastWave-inspired variable axes**, font fallback, copyright notices, accessibility and GPL code-copying precautions.
- Expanded Liquid Glass UI: candidate **Kyant0 Backdrop**, native Compose shader path, Full versus Efficient effects, pill navigation, design tokens, GPU budgets, reduced-motion and screenshot requirements.
- Added complete graphics/asset pipeline: Lucide vector icons, original nine rank emblems, app icon/branding, visual QA, original editable masters and SPDX provenance manifest. Lottie/Rive alternatives listed without becoming requirements.
- Added a comprehensive **13-source Codex skills roster including every originally optional source**, with seven proposed internal Gym RPG skills, routing and conflict rules.
- Added full Android/Kotlin/Compose/Gradle/Room/DataStore/Health Connect/Chinese HealthKit/Android Live Updates/AI/web/CI/ADB/scrcpy and graphic tool matrix; no installation steps.
- Preserved and cross-referenced complete earlier domains: detailed exercise logging, PPLUL fixed calendar, region-based plateau engine, immutable template history, calibrated LP/XP ranks, food JSON bulk imports and EFSA micronutrients, China-only Xiaomi preference, offline AI adapters, deterministic backup and tests.
- Tightened M0/M1 gates around font/glass/SVG proofs and real-device screenshot evidence.

### Files intentionally retained at format v1.0

The supplied `food-import.schema.json`, `exercise-import.schema.json`, `muscle-taxonomy.v1.json`, `plateau-fixtures.v1.json` and their examples were reviewed for the changes above. They remain logically compatible because the updated visual renderer uses **the same canonical taxonomy**, the post-v1.0 ranked/plateau rules were already in the v1.0 pack, and the nutrition format has not changed. This v1.1 archive intentionally carries these identical versioned contracts instead of silently bumping them for a purely visual/tooling update.

### Excluded on purpose

No Arch Linux setup instructions, Android Studio installation commands, downloadable font binaries, pretrained/ready-made anatomy SVGs, fabricated AI scoring standards, real Xiaomi SDK binaries, or proprietary app assets are packaged.
