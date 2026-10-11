---
name: gymrpg-xiaomi-integration
description: Use on mainland China HyperOS 4 HealthKit, Live Updates, device capability and fitness provenance.
---
# gymrpg-xiaomi-integration

Target the mainland China Mi Fitness ecosystem, NOT the global Play Store version. Check official Xiaomi HyperOS 4 HealthKit access, developer approval and sign requirements; never assert compatibility without real device verification. Build capability-gated adapter; fallback to Health Connect only if it actually receives mainland data, and manual entry otherwise. Use Android Live Updates for running workouts and notification fallback for HyperIsland. Prevent double-counting step/workout data.

## Evidence / exit criteria
Before claiming completion, compile affected modules, run applicable fixtures/tests, and explain any device-only limitations. Record changed files and results. Never use external examples to override the project's v1.1 specification.
