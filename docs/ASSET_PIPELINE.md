# Gym RPG — Asset and Visual System Production Contract

**Version:** 1.1 | Applies to all developer- and Codex-created graphics and interactions.

## Non-negotiable final visual decisions

- **Native two-dimensional anatomy:** editable front/back vector files, independently addressable polygons; explicit Front/Back buttons, short crossfade, tap selection and simple scale/translate zoom. No 3D/Blender/SceneView/GLB/Filament.
- **Typography:** Google Sans Flex from Google's SIL OFL distribution, variable axes inspired by LastWave Native's `Type.kt`, not copied GPL application code. Bundle official copyright/OFL notices in the eventual released APK/repo. **No font binaries in this specification archive.**
- **Glass:** dark first, premium Full effects and battery-saving Efficient mode, floating pill navigation. Source candidates Kyant0 Backdrop and native Compose shader primitives; user choice and system reduced-motion preference prevail.
- **Rank iconography:** nine original Wood/Bronze/Silver/Gold/Platinum/Diamond/Champion/Titan/Olympian emblems. Original graphics, not extracts from Liftoff; distinct silhouette/ornamentation at each tier and sufficient legibility at small icon size.
- **General icons:** coherent Lucide SVG-derived native icon family (ISC), not a mixture of unrelated assets. No flattened PNG for path-driven anatomy. All colors and effects use theme tokens, not scattered hardcoded hex.

## 1. Anatomy vector source candidates and evaluation

| Candidate | Details | Licence / condition |
|---|---|---|
| https://github.com/vulovix/body-muscles | >70 regions; front/back; SVG paths embedded in TS; reference renderer is web-based | Apache-2.0; preserve LICENSE and NOTICE, verify specific asset provenance |
| https://github.com/Jsplice/MuscleMap | Original SVG body paths, selectable semantic regions, front/back | MIT with asset provenance doc; confirm source paths included in grant |

Neither is directly an Android library. Codex must review and extract vector *geometry* under compatible license, transform into native path assets, and retain the original upstream URL, commit hash and full license/notice. Choose one source unless combining is justified and every path provenance is tracked. **Do not use random internet images as editable SVG replacements.**

## 2. Vector representation and mapping

- Master sources in `design/anatomy/front.svg` and `design/anatomy/back.svg`, with consistent coordinate/viewBox rules. Each path has stable ID; overlapping paths have explicit hit-test z-order.
- Native import must produce a **validated region manifest** conforming to `anatomy-render-contract.schema.json`: `view`, `region_id`, `path_ids`, `subdivision_ids`, `mapping_precision`, `license_source`. A separate SVG-to-native compiler may be scripted for reproducibility; its input/output is checked into git or generated during Gradle asset task.
- The scientific/canonical taxonomy is `muscle-taxonomy.v1.json` (16 groups, 52 subdivisions). The source path library may have more or fewer regions. A visible geometric region is **not automatically** a one-to-one anatomical head. Track `exact_visual`, `merged_visual`, or `approximation`; show UI limitations.
- For subdivisions with no geometry, use list/details as a fallback; no auto-generated mystery polygons. Do not force all 52 subdivisions to be unique physical shapes if the art can't plausibly represent them.
- Tap hit-testing must transform touch coordinates back from pan/zoom/scale; small muscle targets have alternative semantic list or appropriately padded hit zones. Native UI labels and TalkBack are mandatory.
- Runtime maps region/color/evidence independently of CSV/food/backend; no API calls to render the local body.

## 3. Required viewing and interaction tests

1. **Front and Back buttons** correctly switch, preserve current mode and do not leave invisible overlapping hit targets.
2. Tap every renderable region to show label, linked exercise list and appropriate group rank/evidence.
3. Select Bayesian cable curl and see expected arm primary/secondary region highlights; tap other exercise tests for chest/back/legs.
4. Test `Strength Rank`, period Volume, Muscle Balance, Estimated Recovery, Template Targeting each with a *distinct multihue legend*. Incomplete data shown as Unknown.
5. Zoom to target bounding box with bounded scale/translate; visible Reset; tap coordinates still map to correct region after zoom.
6. Fit in removable/resizable Home widget and full-screen anatomy view on Xiaomi portrait with 100/130/160% font scale and both system navigation methods.
7. TalkBack accessible labels and list view substitute; no tiny unclickable paths blocking essential workflows.
8. Screenshot baseline comparisons and animation performance profiling; Efficient Glass must render without required shader backend.
9. SVG license, source commit, checksums, editable vector masters and full NOTICE retained.

## 4. LastWave-inspired typography

LastWave Native repo `app/src/main/java/com/lastwave/app/ui/theme/Type.kt` uses variable font Google Sans Flex with `wght`, `wdth`, `ROND`, `opsz`, `GRAD`. Recreate our own type scale, not verbatim GPL source code. Example design targets: hero 760 weight/122 width/42 roundness, title 640/110/30, body 430/102/18, pill label 680/108/46; use optical size matching display type size. Apply font consistently in native Compose, accessibility and system fallback. Android launcher widget fallback to platform fonts if custom typography unsupported. Do not infer that individual typographic axes make the app a copy of LastWave's branding.

## 5. Liquid Glass details and design tokens

- `GymGlassSurface`, `GymGlassPillNav`, `GymGlassButton`, `GymGlassSheet`, `GymGlassWidgetFrame` expose stable props; an internal `GlassRenderer` supports `FULL`, `EFFICIENT` and `REDUCED_MOTION` paths.
- For `FULL`, evaluate Kyant0/AndroidLiquidGlass Backdrop. It is a library with demo components, not a ready-made app design system; wrap vendor classes and pin verified API.
- For `EFFICIENT`, remove live refraction and excessive blur; use semi-translucent fill, thin edge strokes and limited shadows. The app should remain premium-looking even with effects off.
- Dark default, optional light/system; high contrast legibility; floating pill clear of system gesture bar and keyboard; 48dp minimum tap targets.
- Native Compose spring transitions and Color animation for common UI. Lottie Compose and Rive are documented evaluated alternatives for particularly complex rank-up animations; neither is a blocker nor a required dependency.
- No copyright-infringing pixel clone of Apple Music, LastWave, Liftoff or commercial apps. The intended design is original with comparable layout principles.

## 6. Asset format and licensing

Keep editable originals and compact runtime derivatives. E.g. `design/anatomy/*.svg`, `design/ranks/*.svg`, `design/branding/*.svg`, then generated native Compose geometry. Record every third-party file in `asset-manifest.schema.json` with source, SHA-256, SPDX license, attribution, changes and editable-master flag. Preserve OFL.txt (fonts), LICENSE/NOTICE (Apache), and MIT/ISC notices as appropriate. The manifest example is an *unverified placeholder* and MUST NOT be shipped with `license_verified=false` as if it were real art.

Codex must attach **real screenshots** of the Android app at each visual milestone; passing compilation is not visual acceptance. It must not claim images have been authored or licenses verified unless the corresponding artifacts and evidence exist.
