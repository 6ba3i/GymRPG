---
name: gymrpg-quality-gates
description: Use for Android builds, CI, UI screenshots, architecture, battery and migration tests.
---
# gymrpg-quality-gates

Require focused unit and integration tests, Room migration fixtures, JSON schema golden tests, source license checks, screenshot comparisons including insets, performance profiling for glass and anatomy, and signed APK release checks. Do not fake hardware validation. Identify regressions by severity, and report skipped or unavailable checks.

## Evidence / exit criteria
Before claiming completion, compile affected modules, run applicable fixtures/tests, and explain any device-only limitations. Record changed files and results. Never use external examples to override the project's v1.1 specification.
