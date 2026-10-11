# Gym RPG — Binding Decision Register (v1.1)

All decisions below reflect the complete conversation, including the changes *after* the 10 October 2026 v1.0 release. Entries are ordered by importance; most recent user decision supersedes older proposals.

| Area | Final user decision / engineering interpretation | Supersedes |
|---|---|---|
| Target | Xiaomi 17 Pro, **Chinese HyperOS 4.0.0.41 beta**, sideloaded APK, GitHub OSS, one local profile | Cloud/Play Store assumptions |
| Architecture | Offline/local-first with on-device modular domain engine and Room; no remote login/backend needed; JSON backup/restore | Cloud-first architectures |
| Nav | Home, Workout, Calendar, Food, Profile; dark-first bottom **glass pill** | Generic Material nav |
| Glass | Full premium blur/refraction and Efficient battery-saving mode, chosen in settings; accessible fallbacks | Always-heavy blur |
| Font | **Google Sans Flex** via licensed upstream; independently recreate **LastWave** variable-axis feel | Unspecified font |
| Anatomy | **Native 2D SVG front/back**, buttons, crossfade, selectable/colorable regions and simple zoom/reset | 2.5D rotation, Blender, GLB, Filament, 3D |
| Muscle segmentation | 16 parent groups / 52 canonical subdivisions; rank at muscle **group**, multicolor *indicators* at subdiv level | Head-by-head measured strength ranks |
| Template presentation | Workout templates are playlists; planned sets and combined muscle-target banner. Muscle tap offers contextual exercises to add. | Simple list-only editor |
| Workout log | Weight/reps recorded **per individual set**, drop `+` additional weight/reps, all working sets assumed absolute failure, supersets/unilateral, previous value prefill, exercise charts | End-only maximum weight input |
| Time | Workout start/stop timer survives locking/background; no pause; start/end editable in Profile Logs | Pause or mandatory rest timers |
| Calendar | Mon Push/Tue Pull/Wed Legs/Thu Upper/Fri Lower, weekends off by default. Missed workouts don't shift fixed calendar. | Rolling weekday plan |
| Rotation | >=50% **eligible muscle subdivisions** stagnate in three consecutive eligible workouts of **same template**, then auto-sequential switch to next template in group | Exercise-count 50% rule |
| Template versions | Git-like immutable versions, diffs/restore; stable workout snapshots; material edits invalidate affected baselines only | Overwrite template in place |
| Rank | Liftoff-inspired **nine rank** ladder and 100 LP per tier, original scores/art, exercise/group/overall where calibrated. XP separate. | Claimed exact proprietary Liftoff scoring |
| Game mechanics | Quests, streaks, achievements, seasons, level XP, PR and rank-up celebrations, no currency/cosmetics/online leaderboard | Full commercial game economy |
| Nutrition | User fills explicitly keyed **multi-item JSON** template externally (including with ChatGPT); recipes, diary import, full macros and 47 nutrient keys; EU EFSA percent reference and missing-value treatment | Required external searchable food database/AI camera scanner |
| Goals | Recommended calories adjustable; only protein grams is essential manual macro target; manual body-fat entry. | Mandatory carbohydrate/fat targets |
| Energy | Total estimated energy, workouts, steps and additional run/walk/cycling with overlap protection | Mandatory global Mi Fitness sync |
| Mi Fitness | Prefer **mainland Chinese Mi Fitness / Xiaomi official HyperOS 4 HealthKit** authorized API, not global Play Store assumptions; Health Connect tertiary only if Chinese source data present | Global Mi Fitness inference |
| HyperIsland | Android standard Live Updates for user-started workout with persistent notification fallback; OEM presentation tested on real beta | Promise of private HyperIsland APIs |
| Rear screen | **Out of scope** | Explore Xiaomi rear display |
| AI | User key, OpenAI-compatible + DeepSeek, native search if real or optional Brave/Exa/Tavily search API, approved exercise mappings, plateau/nutrition/history analysis, per-feature toggles and cache invalidation | Always-on AI or automatic modification |
| Assets | Editable SVG anatomy, vector originals, creative rank emblems, one cohesive icon family, provenance and distribution rights | Ad hoc image-generation assets as final editable geometry |
| Agent skills | Include **all** 13 external reference sources and seven planned Gym RPG-specific skills; load relevant groups, avoid conflicts. | Core-only skill shortlist |
| Delivery | Milestones M0-M7 with screenshot, unit/instrumented/performance gates and Xiaomi evidence | Single giant unvalidated PR |
| Host preparation | Excluded from this *requirements* deliverable; no installation walkthrough | Setup tutorial content |
