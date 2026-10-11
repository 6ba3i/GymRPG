---
name: gymrpg-nutrition-engine
description: Use on JSON food schemas, recipes, EFSA nutrient references, daily diary and TDEE.
---
# gymrpg-nutrition-engine

Treat strictly validated versioned schema as authoritative; every nutrient key/unit is explicit and missing is null, not zero. Allow bulk food and diary imports, recipes, per-100g basis, scaled servings, user-edited protein target, age/sex-appropriate EFSA reference values, and estimated total daily expenditure avoiding duplicate activity data. Preserve a reversible JSON export and use deterministic unit tests.

## Evidence / exit criteria
Before claiming completion, compile affected modules, run applicable fixtures/tests, and explain any device-only limitations. Record changed files and results. Never use external examples to override the project's v1.1 specification.
