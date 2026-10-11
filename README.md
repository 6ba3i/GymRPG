# Gym RPG

Native, local-first Kotlin / Jetpack Compose Android application. The current implementation is the **M0 technical proof**, initialized directly in this repository root. It does not yet log workouts, compute ranks, import nutrition, or read Xiaomi health records.

## Build

Use JDK 17 and an Android SDK containing `platforms;android-37.0` and `build-tools;36.0.0`. Set `ANDROID_HOME`, or create an untracked `local.properties` with `sdk.dir=/path/to/sdk`.

```sh
./gradlew testDebugUnitTest lintDebug assembleDebug
./gradlew connectedDebugAndroidTest
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.

See [M0 implementation tickets](docs/M0_IMPLEMENTATION_PLAN.md), [toolchain audit](docs/M0_TOOLCHAIN.md), and [technical specification](docs/GymRPG_Technical_Spec_v1.1.md). The original documentation, skill configuration and asset source cache are preserved. Dependencies are pinned; the Gradle wrapper distribution and JAR checksums are verified.

## Modules

- `app`: five-tab demo shell, local visual preferences, diagnostic UI and instrumentation.
- `core/designsystem`: Google Sans Flex, theme tokens, shared glass renderer and navigation controls.
- `feature/anatomy`: licensed SVG-to-native front/back rendering proof and mapping evidence.
- `core/integrations`, `core/notifications`: read-only capability contracts.
- `integration/xiaomi-cn`: optional mainland HealthKit package probe; no proprietary SDK.

Room/onboarding and real feature persistence begin at M1. SDK availability and package presence do not establish Xiaomi developer authorization or HyperIsland support.
