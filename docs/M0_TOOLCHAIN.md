# M0 toolchain and Backdrop audit

Audit date: **2026-10-11, Asia/Shanghai** (2026-10-10 UTC during the session). This records dependency metadata and host/device probes, not completed application tests. Build, screenshot and OEM verification results belong in the M0 acceptance report.

## Selected compatible baseline

| Component | Pin | Evidence / rationale |
| --- | --- | --- |
| Android platform | `platforms;android-37.0`, revision 2 | Installed final Android 17 SDK: API 37.0, preview SDK 0; use `compileSdk = 37`. |
| Android Build Tools | `36.0.0` | Installed and AGP 9.2 documented minimum/default. |
| Android Gradle Plugin | `9.2.1` | Stable patch release; AGP 9.2 supports maximum API 37.0. |
| Gradle | `9.4.1` | AGP 9.2 documented minimum/default. |
| JDK | `17` | Installed at `/usr/lib/jvm/java-17-openjdk`; AGP 9.2 documented minimum/default. |
| Kotlin / Compose compiler plugin | `2.3.10` | Matches Backdrop 1.0.6 publication. AGP built-in Kotlin defaults to 2.2.10, so resolve the shared Kotlin plugin classpath to 2.3.10. |
| Compose BOM | `2026.02.00` | Published BOM maps Compose UI/foundation to 1.10.3, matching Backdrop. |
| Backdrop | `io.github.kyant0:backdrop:1.0.6` | Android release, Apache-2.0; published Kotlin 2.3.10, Compose 1.10.3 and shapes 1.2.0 dependencies. |

These are deliberate compatible pins, not claims to be the newest releases. Google Maven also contained AGP 9.4.1 and Compose BOM 2026.09.00 at retrieval; upstream Backdrop had progressed to a multiplatform 2.x line. M0 does not require those upgrades. Compilation and device rendering remain the final integration checks.

AGP 9 provides built-in Kotlin for Android modules: do not apply `org.jetbrains.kotlin.android` alongside it. Applying the Compose compiler plugin at 2.3.10 and aligning any Kotlin JVM modules to that version avoids mixed compiler versions. Configure Java/Kotlin bytecode targets consistently. `compileSdk` does not select `targetSdk` or `minSdk`; those remain explicit application decisions.

## Official compatibility and artifact evidence

- [AGP 9.2 release notes](https://developer.android.com/build/releases/agp-9-2-0-release-notes): API 37.0, Gradle 9.4.1, Build Tools 36.0.0 and JDK 17 matrix.
- [AGP built-in Kotlin guidance](https://developer.android.com/build/migrate-to-built-in-kotlin): Kotlin Android plugin migration and built-in Kotlin configuration.
- [AGP 9.2.1 published POM](https://dl.google.com/dl/android/maven2/com/android/tools/build/gradle/9.2.1/gradle-9.2.1.pom): plugin availability and default Kotlin Gradle plugin 2.2.10 runtime dependency.
- [Compose BOM 2026.02.00 published POM](https://dl.google.com/dl/android/maven2/androidx/compose/compose-bom/2026.02.00/compose-bom-2026.02.00.pom): exact Compose 1.10.3 mapping. BOM 2026.02.01 instead maps UI to 1.10.4.
- [Compose compiler Gradle plugin guidance](https://developer.android.com/develop/ui/compose/compiler): use the Kotlin Compose compiler Gradle plugin with Kotlin 2.x.
- [Backdrop 1.0.6 published POM](https://repo1.maven.org/maven2/io/github/kyant0/backdrop/1.0.6/backdrop-1.0.6.pom): dependency and Apache-2.0 license declaration.

## Host evidence and reproducibility

Read-only probes found SDK platforms `android-34`, `android-36`, `android-37.0`, and Build Tools `34.0.0`, `36.0.0`. Both `ANDROID_HOME` and `ANDROID_SDK_ROOT` are `/home/idriss/Android/Sdk`. The installed `android-37.0/source.properties` identifies `Platform.Version=17`, `Pkg.Revision=2`, `AndroidVersion.ApiLevel=37.0`, `AndroidVersion.PreviewSdkInt=0` and `AndroidVersion.ExtensionLevel=22`.

**JDK path caveat:** `JAVA_HOME` points to JDK 17, but bare `java -version` resolves OpenJDK 27 through the shell PATH. Gradle's wrapper honors `JAVA_HOME`; explicitly run with JDK 17 and confirm `./gradlew --version`. Do not infer the Gradle daemon JDK from bare `java`. CI should provision JDK 17 rather than copy an absolute workstation path.

No emulator executable, system images or AVD were installed during the audit. `/dev/kvm` exists and is readable/writable, so an emulator is feasible after installing the corresponding SDK packages. A physical device was already connected and authorized through ADB; no emulator installation was necessary for the initial smoke-test route.

### Wrapper checksums

The selected distribution is `https://services.gradle.org/distributions/gradle-9.4.1-bin.zip`. Its official [distribution SHA-256](https://services.gradle.org/distributions/gradle-9.4.1-bin.zip.sha256), fetched during this audit, is:

```text
2ab2958f2a1e51120c326cad6f385153bb11ee93b3c216c5fccebfdfbb7ec6cb
```

This value is present as `distributionSha256Sum` in the wrapper properties. The official [Gradle 9.4.1 wrapper JAR SHA-256](https://services.gradle.org/distributions/gradle-9.4.1-wrapper.jar.sha256) is:

```text
55243ef57851f12b070ad14f7f5bb8302daceeebc5bce5ece5fa6edb23e1145c
```

The wrapper was regenerated using Gradle 9.4.1 during bootstrap. A fresh `sha256sum gradle/wrapper/gradle-wrapper.jar` matches the official wrapper JAR checksum above. The earlier bootstrap JAR was replaced; both final distribution and wrapper JAR provenance are verified.

## Backdrop source/API assessment

The verified release tag `1.0.6` resolves to full commit `896a94a3ade1cc1a940b92365f942a34971fecda`. Use this immutable source when reading API examples; the repository default branch is now `kmp`, and its current APIs/build versions differ. The 1.0.6 library declares Android minSdk 21. Its source build uses a JDK 21 toolchain, which does not itself mean consumers must run Gradle on JDK 21; test the published AAR with the selected consumer build.

The library supplies rendering primitives, not a complete navigation/design system. Keep imports inside `core:designsystem`, behind Gym RPG wrappers. Use one shared source backdrop for the surface being sampled. An efficient surface should avoid installing live backdrop blur/refraction and draw its translucent fill/border directly. Reduced motion, unsupported effects, power restrictions and rendering failures need an explicit fallback path.

Pinned source references:

- [README](https://github.com/Kyant0/AndroidLiquidGlass/blob/896a94a3ade1cc1a940b92365f942a34971fecda/README.md): component ownership and upstream documentation.
- [Version catalog](https://github.com/Kyant0/AndroidLiquidGlass/blob/896a94a3ade1cc1a940b92365f942a34971fecda/gradle/libs.versions.toml): producer Kotlin 2.3.10 / Compose 1.10.3.
- [Library build](https://github.com/Kyant0/AndroidLiquidGlass/blob/896a94a3ade1cc1a940b92365f942a34971fecda/backdrop/build.gradle.kts): minSdk, publication and license.
- [LayerBackdrop](https://github.com/Kyant0/AndroidLiquidGlass/blob/896a94a3ade1cc1a940b92365f942a34971fecda/backdrop/src/main/java/com/kyant/backdrop/backdrops/LayerBackdrop.kt): `rememberLayerBackdrop()`.
- [DrawBackdropModifier](https://github.com/Kyant0/AndroidLiquidGlass/blob/896a94a3ade1cc1a940b92365f942a34971fecda/backdrop/src/main/java/com/kyant/backdrop/DrawBackdropModifier.kt): `Modifier.drawBackdrop` shape/effects/surface API.
- [Blur](https://github.com/Kyant0/AndroidLiquidGlass/blob/896a94a3ade1cc1a940b92365f942a34971fecda/backdrop/src/main/java/com/kyant/backdrop/effects/Blur.kt): no effect below API 31.
- [Lens](https://github.com/Kyant0/AndroidLiquidGlass/blob/896a94a3ade1cc1a940b92365f942a34971fecda/backdrop/src/main/java/com/kyant/backdrop/effects/Lens.kt): no effect below API 33; requires a supported rounded rectangular/corner-based shape and can throw for unsupported shapes.
- [License](https://github.com/Kyant0/AndroidLiquidGlass/blob/896a94a3ade1cc1a940b92365f942a34971fecda/LICENSE): preserve Apache-2.0 notices in distribution.

Example API sketch (not standalone tested code):

```kotlin
val backdrop = rememberLayerBackdrop()
// Apply to the source content:
Modifier.layerBackdrop(backdrop)
// Apply to a separate glass surface sampling that content:
Modifier.drawBackdrop(
    backdrop = backdrop,
    shape = { RoundedCornerShape(28.dp) },
    effects = {
        blur(12.dp.toPx())
        lens(16.dp.toPx(), 24.dp.toPx())
    },
    onDrawSurface = { drawRect(tint) },
)
```

`rememberLayerBackdrop` and `layerBackdrop` are in `com.kyant.backdrop.backdrops`; `drawBackdrop` is in `com.kyant.backdrop`; `blur` and `lens` are in `com.kyant.backdrop.effects`. Avoid depending on silent effect no-ops for product mode semantics: Full eligibility and the visible effective-mode label should agree.

## Physical-device observations and limits

ADB reported an authorized Xiaomi device with product/device `pandora`, model `25098PN5AC`. Supported read-only probes returned Android release `17`, SDK `37`, and physical display `1220x2656`. These observations justify testing API 37 directly; they do not verify safe insets, glass performance, keyboard behavior, HealthKit authorization or HyperIsland presentation.

No proprietary Xiaomi SDK was examined or required by this toolchain. HealthKit availability/eligibility and Live Updates/OEM presentation require the separate diagnostics lane and application/device acceptance evidence. No M1+ implementation is implied by this audit.

## Final reproducibility corrections

The IDE generated a daemon JVM criteria file selecting JDK 25 during development. M0 now explicitly pins `toolchainVersion=17` in `gradle/gradle-daemon-jvm.properties`; CI supplies that JDK. The Gradle daemon criteria file takes precedence over `JAVA_HOME` when present.

Gradle's initial `--write-locks` omitted the transitive `kotlin-stdlib-common` from app runtime lock configurations. Targeted `:app:dependencies --configuration debugRuntimeClasspath --update-locks org.jetbrains.kotlin:kotlin-stdlib-common` and the equivalent `debugUnitTestRuntimeClasspath` command populated them. A subsequent ordinary **offline build without lock-writing flags passed** (see `m0-evidence/gradle-validation.txt`). Do not use dependency-report exit status alone as build evidence.

An API 37 emulator was subsequently created from the already installed Google APIs Play Store x86_64 image. Its acceptance evidence is separate from the physical Xiaomi evidence; headless SwiftShader rendering cannot establish device GPU performance.
