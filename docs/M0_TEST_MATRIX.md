# M0 test and evidence matrix

This document is the test contract for the M0 shell and anatomy proof. It records expected tests; it is not evidence of a passing device run.

## Instrumentation semantics contract

- `tab_Home`, `tab_Workout`, `tab_Calendar`, `tab_Food`, `tab_Profile`: clickable navigation items. `pill_nav`: full navigation pill bounds. `screen_title`: destination title containing destination name; Home may use "Home" as the accessible title even when anatomy is prominent.
- `m0_header`: top content surface used to check status bar clearance. `glass_toggle`: selectable Full Glass chip. `glass_full` / `glass_efficient`: the visible active glass-mode state, preferably the chip and/or state summary, with exactly one visible at a time. Persistence is a separate app behavior and is not asserted by the screenshots.
- `anatomy_front`, `anatomy_back`: view toggles. `anatomy_canvas_front`, `anatomy_canvas_back`: interactive map. `anatomy_region_front`: accessible, mapped front region action. `anatomy_selection`: details of the selected path; absent after changing views. `anatomy_reset`: reset zoom control, disabled at identity zoom.
- `ime_probe`: text field on Profile demo diagnostic so a real IME is raised and the field remains visible. This is a technical inset harness, not a future Profile feature.

## Scope

| Behavior | Test | Result |
|---|---|---|
| Five tab navigation opens matching destination | `fiveDestinationsOpenFromFloatingNavigation` | Passed on API 37 emulator |
| Full/Efficient switch remains legible and nav available | `glassModeCanSwitchBothWays` | Passed on API 37 emulator; four screenshots reviewed |
| Front/back map and SVG tap-to-canonical-ID selection | `anatomyViewSwitchesAndSelectingRegionReportsItsMapping` | Passed on API 37 emulator |
| Pinch zoom and reset | `anatomyZoomCanBeReset` | Passed on API 37 emulator |
| Pill bottom/status content top outside system bars | `bottomNavigationStaysAboveGestureArea` | Passed on API 37 emulator; expanded font/navigation matrix remains open |
| Focused text remains above keyboard | `keyboardDoesNotCoverFocusedField` | Automated emulator failure; manual Xiaomi keyboard/inset scenario passed (see evidence report) |
| Full/Efficient × front/back screenshot artifacts | `captureFullGlassHomeAndBothAnatomyViews`, `captureEfficientGlassHomeAndBothAnatomyViews` | Four emulator captures reviewed; no pixel-diff golden CI yet |

## Execution

Run `scripts/device-smoke.sh DEVICE_SERIAL [OUTPUT_DIR]` only after the app and test APK compile. It records Android API, ROM, display size and density; installs the two debug APKs; runs instrumentation; and pulls screenshots from the app-specific external files directory. Use `./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest` for JVM/lint/test compilation before device testing. Record actual output and screenshots separately; device screenshots prove render state only after a human or visual review assesses legibility and clipping.

## Out-of-scope until later milestones

M1+ onboarding, Room migrations, nutrition and workout flows; 52 mapped anatomy polygons are not implied by this M0 geometry proof. Xiaomi HealthKit authorization and OEM HyperIsland promotion require approved access and separate physical verification.
