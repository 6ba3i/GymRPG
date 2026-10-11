# M0 validation evidence

Validation date: 11 October 2026 (Asia/Shanghai). Implementation is at the existing repository root. Existing specification documents, schemas, asset sources and installed skills remain in place. No M1+ domain implementation was added.

## Delivered foundation

- `app/`: five-destination Navigation Compose shell; edge-to-edge safe drawing/IME layout; DataStore visual preferences with visible storage failures; profile capability and license diagnostics.
- `core/designsystem/`: independently authored Google Sans Flex variable typography, dark/light/system options, Full Backdrop 1.0.6 wrappers, Efficient shader-free fallback, motion/battery/thermal capability fallback and floating navigation.
- `feature/anatomy/`, `design/anatomy/`, `assets/anatomy/`: licensed front/back SVG masters, native paths, hit testing, selection, primary/secondary preview colors, bounded pinch/pan/focus/reset and accessible region list. 89 paths; 33 represented subdivisions; 19 explicitly list-only. No 3D/WebView.
- `core/integrations/`, `core/notifications/`, `integration/xiaomi-cn/`: public, read-only capability probes with narrow HealthKit package visibility. No proprietary SDK, binding, health-data reads or notification posting.
- Root Gradle wrapper/configuration and lockfiles; `.github/workflows/android.yml`; asset validators; device smoke script; focused unit and Compose UI tests; M0 planning/toolchain/platform/test documents.

## Host verification

Ordinary **offline** JDK 17 build (no `--write-locks`) passed:

```sh
./gradlew testDebugUnitTest lintDebug :app:assembleDebug :app:assembleDebugAndroidTest --offline --console=plain
```

Evidence: `gradle-validation.txt`. Twelve JVM tests passed: 4 anatomy coordinate/zoom tests, 3 notification capability tests, 2 Xiaomi authorization distinction tests and 3 preference failure/cancellation tests. Final UI-test-only edits subsequently passed `:app:assembleDebugAndroidTest :app:lintDebug`; see `ui-test-build.txt`.

Lint passed with no errors; warnings remain for deliberate portrait orientation, dependency/catalog recommendations, backup policy, API annotation and animation offset usage. These are not a claim of zero warnings. Asset/schema and SVG-native identity checks passed (`asset-validation.txt`), including bundled font checksum/OFL and anatomy provenance. Shell syntax check passed for `scripts/device-smoke.sh`.

APK: `app/build/outputs/apk/debug/app-debug.apk` (debug signed); SHA-256 in `debug-apk.sha256`. GitHub workflow is configured but was not run remotely. This workspace has no Git metadata; no commit or PR was created.

## Physical Xiaomi observations

ADB observed model `25098PN5AC`, product `pandora`, ROM `OS4.0.0.41.XBLCNXM`, Android 17/API 37, 1220×2656 at 520 dpi, gesture navigation mode 2. The APK installed and launched. A recorded cold launch took 507 ms (`xiaomi-cold-launch.txt`), an uncontrolled debug observation rather than a benchmark. An isolated five-destination test passed (`xiaomi-navigation-test.txt`). Manual screen review showed readable dark typography and navigation above the visible gesture bar; the anatomy page scrolls within the space above the dock.

The full physical suite is **not accepted as passing**. Early Espresso 3.6.1 failed on an API 37 reflection call; 3.7.0 resolved that compatibility issue. Later attempts were interrupted by screen locking and system rejection of instrumentation activity starts (`result code=102`, `xiaomi-runner-launch-block.txt`). Force-stopping stalled instrumentation produced runner “Process crashed” summaries; those aborted runs are not successful tests. The original screen timeout was restored. Final automated verification uses the emulator and must not be presented as Xiaomi-specific validation.

## Emulator UI verification

API 37 Android 17, 1080×2400, headless SwiftShader. Four final screenshots and inset measurements are stored under `emulator/`. The completed emulator run reported 7/8 passing tests; the remaining keyboard test timed out because its IME-show request was rejected. Its temporary raw runner log was lost when the execution environment restarted; this result is recorded from the observed run, not a retained raw log. Tests cover actual SVG tap-to-canonical-ID mapping, view reset, pinch/reset, five-tab navigation, glass switching, system-bar clearance, software keyboard clearance and Full/Efficient × front/back capture.

## Boundaries and follow-up

- HealthKit developer registration, signing/scopes, user authorization and real samples remain unverified. Package presence alone grants none of these.
- Live Update posting, persistent workout notification and OEM HyperIsland rendering remain unverified; M0 implements only the capability diagnostic/fallback plan.
- No release GPU/frame pacing, battery, or thermal benchmark; emulator/debug timing does not establish the 60 fps target. No 130%/160% font-scale, TalkBack or complete alternate-navigation/device matrix yet.
- Screenshot artifacts establish reviewed M0 render states, not a pixel-diff CI golden comparison. Remote CI execution remains unverified.
- No asset-rights blocker for included OFL/Apache assets; original sources, notices and checksums retained. Full 52-region geometry is not claimed.
- Next milestone proposal: `../M0_NEXT_MILESTONE.md`. M1 remains unimplemented.

## Final M0 disposition

**M0 technical proof delivered with explicit validation limitations.** Debug APK builds and is installed; 12 JVM tests and lint pass; seven of eight emulator UI tests passed. All four Full/Efficient × front/back screenshots were visually reviewed: readable text, complete anatomy silhouette in the captured scroll position and dock clearance. Measured emulator header top 136 px equals status/cutout inset 136 px; pill bottom 2316 px is above the navigation boundary at 2337 px.

The remaining keyboard acceptance scenario was verified manually on the physical Xiaomi after the emulator's keyboard request failed: `keyboard-manual-state.txt` reports `mInputShown=true` and `mIsInputViewShown=true`. `keyboard-inset-bounds.txt` records visible IME starting at y=1762; the focused text field bounds are [52,696][1168,986], and the dock labels end at y=1657. The reviewed `screenshots/xiaomi-keyboard.png` shows both field and dock clear of the keyboard. The UI hierarchy is retained in `keyboard-manual-ui.xml`. This is real-device manual validation, **not** a passing automated keyboard test. A further physical instrumentation attempt was blocked before activity launch and stopped; see `keyboard-final.txt`.

Follow-up: make the IME instrumentation reliable on the chosen CI image; keep that test enabled and failing until fixed. Do not describe the full automated suite or remote CI as green. The device-specific HealthKit/HyperIsland and broader accessibility/performance limitations above remain open and do not imply later features were implemented.
