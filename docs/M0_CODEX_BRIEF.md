# Codex — Implementation brief for milestone M0 (v1.1)

Implement **M0 only** using `GymRPG_Technical_Spec_v1.1.md`, `AGENTS.md`, `DECISION_REGISTER.md`, `ASSET_PIPELINE.md`, `TOOL_AND_SKILL_MATRIX.md` and accompanying schemas. Everything is a **local-first native Android** project targeting a Chinese-ROM Xiaomi 17 Pro, currently HyperOS 4.0.0.41 beta. The app must build without Xiaomi proprietary SDKs or external keys.

## Work plan
1. Identify actual target device Android API, renderer/shader availability and display insets through supported Android APIs; create a diagnostic screen. Do not infer API level solely from the HyperOS marketing version.
2. Establish Kotlin/Jetpack Compose + Material 3 project skeleton, dark theme default, **Google Sans Flex** variable type proof (source must be Google upstream under OFL), theme Full/Efficient toggle with correct fallback; font-license notices present in eventual app distribution.
3. Build one demo five-section shell with a floating **pill nav** and correct status bar/gesture area/IME layout. Verify all tabs and keyboard resizing. Choose a tested glass rendering foundation behind wrappers; assess Kyant0/AndroidLiquidGlass and implement an efficient fallback first.
4. Prove a **2D SVG path-rendering** anatomy sample using a correctly licensed front/back candidate asset: Front/Back buttons, crossfade, tap path highlight, primary/secondary colors, zoom/reset and ID mapping. If source asset acquisition/provenance blocks this, use an **original temporary geometric dummy test vector** clearly labeled as such; do not pretend 52 paths are already complete.
5. Provide a lightweight Android Live Update capability diagnostic with persistent-notification fallback plan and explicit OEM-controlled island caveat. Test on device when physically available.
6. Probe `com.xiaomi.healthkit` **only via allowed Android package/service visibility mechanisms** and list permission/approval/signing requirements. Do not embed or imitate an unapproved mainland Xiaomi SDK. Do not use global Play Store Mi Fitness assumptions.
7. Establish deterministic UI screenshot tests / system-inset test harness, CI build and basic unit tests. Capture screenshots of Full and Efficient mode and both anatomy views from device/emulator when available.
8. Add an asset provenance manifest and SPDX notices for included art and font. Do not add Blender, Filament, SceneView, GLB, WebView anatomy, Flutter, React Native, server components, virtual currencies or online leaderboards.

## Deliverables and exit criteria

- A buildable APK, reproducible Gradle wrapper configuration, one physical-device or emulator smoke test, diagnostics, five-tab nav shell, 2D anatomy proof, glass fallback, working dark typography.
- Evidence of actual display inset behavior in portrait and correct touch targets above gesture navigation; no clipped UI.
- Unit + Compose instrumentation/screenshots where environment permits, ADB/system-level capability report with verified/unverified labels.
- No handwaved passing tests or claims that mainland Xiaomi developer authorization was granted.
- Next milestone proposal, with no implementation of M1+ scope before M0 gate passes.
