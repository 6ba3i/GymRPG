# Gym RPG — Original asset creation requirements (Codex)

**2D anatomy only**: compare the Apache-2.0 body-muscles and MIT MuscleMap path sources. Validate native Android Compose path import/hit testing. Use front/back buttons; color region fill by workload/stimulus and parent-group rank, animated crossfade and transform-only zoom. IDs must map to `docs/muscle-taxonomy.v1.json`; generate a coverage report and handle missing regions truthfully. No Blender or GLB.

**Typography**: official Google Sans Flex six-axis variable font (`wght`, `wdth`, `opsz`, `ROND`, `GRAD`, `slnt`), retaining OFL notices. Adapt LastWave-inspired type tokens. Define consistent styles for numbers using tabular lining figures, and labels for pill navigation. Check `docs/ASSET_PIPELINE.md` for accepted visual requirements.

**Icons**: subset Lucide ISC icons rather than shipping the complete icon pack. Preserve consistent stroke, optical balance, touch target and contrast. Material Symbols is an optional reference, not a mandate to mix styles.

**Ranks**: design nine ORIGINAL SVG master emblems: Wood, Bronze, Silver, Gold, Platinum, Diamond, Champion, Titan, Olympian. Emblems require distinct silhouettes and detail, not recolored duplicates. Source files editable, neutral backgrounds, contrast at small sizes. Create them during design milestone; do not claim they are pre-provided assets.

**Glass UI**: evaluate `Kyant0/AndroidLiquidGlass` (Backdrop) and all fetched Liquid Glass agent skills. Test the floating pill bar, translucent tiles, blur/refraction, safe insets, and battery-efficient fallback. Do not vendor an entire sample app or copy incompatible shaders into the Android app without checking licenses and SDK performance.

**Animations**: prefer native Compose animations for rank-ups, personal records, anatomy crossfade, zoom, and widget dragging. Lottie Compose/Rive are optional evaluated dependencies only when authored animation assets actually justify them.

**Provenance**: inspect upstream licenses; keep source URL, revision, license and modifications for every asset; commit only reviewed assets required by the APK, not the complete vendor source trees.
