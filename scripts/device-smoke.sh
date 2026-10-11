#!/usr/bin/env bash
set -euo pipefail

# M0 device run. Pass a serial explicitly when multiple transports are connected.
serial="${1:-${ANDROID_SERIAL:-}}"
if [[ -z "$serial" ]]; then
  echo 'Usage: scripts/device-smoke.sh DEVICE_SERIAL [OUTPUT_DIR]' >&2
  exit 2
fi
output="${2:-build/m0-device-smoke}"
mkdir -p "$output"
adb -s "$serial" get-state | tee "$output/device-state.txt"
adb -s "$serial" shell getprop ro.build.version.sdk | tr -d '\r' | tee "$output/android-api.txt"
adb -s "$serial" shell getprop ro.build.version.release | tr -d '\r' | tee "$output/android-version.txt"
adb -s "$serial" shell getprop ro.build.version.incremental | tr -d '\r' | tee "$output/rom-build.txt"
adb -s "$serial" shell wm size | tee "$output/display-size.txt"
adb -s "$serial" shell wm density | tee "$output/display-density.txt"

./gradlew :app:assembleDebug :app:assembleDebugAndroidTest
adb -s "$serial" install -r app/build/outputs/apk/debug/app-debug.apk
adb -s "$serial" install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
# Prevent the physical screen timeout from hiding the activity during UI tests.
original_timeout="$(adb -s "$serial" shell settings get system screen_off_timeout | tr -d '\r')"
restore_timeout() {
  if [[ "$original_timeout" == "null" ]]; then
    adb -s "$serial" shell settings delete system screen_off_timeout >/dev/null || true
  else
    adb -s "$serial" shell settings put system screen_off_timeout "$original_timeout" || true
  fi
}
trap restore_timeout EXIT
adb -s "$serial" shell settings put system screen_off_timeout 600000
adb -s "$serial" shell input keyevent KEYCODE_WAKEUP
adb -s "$serial" shell wm dismiss-keyguard
# Secure keyguards still require the device owner's normal unlock.
adb -s "$serial" shell dumpsys window policy > "$output/window-policy.txt"
if grep -q 'showing=true' "$output/window-policy.txt"; then
  echo 'Device is locked; unlock it normally before running UI tests.' >&2
  exit 1
fi
set +e
timeout 300 adb -s "$serial" shell am instrument -w -r com.gymrpg.test/androidx.test.runner.AndroidJUnitRunner | tee "$output/instrumentation.txt"
status="${PIPESTATUS[0]}"
set -e
mkdir -p "$output/screenshots"
adb -s "$serial" pull /sdcard/Android/data/com.gymrpg/files/m0-screenshots/. "$output/screenshots/" || true
if [[ "$status" -ne 0 ]] || ! grep -q 'OK (' "$output/instrumentation.txt"; then
  echo "Instrumentation failed; inspect $output/instrumentation.txt" >&2
  exit 1
fi
printf 'M0 device evidence saved to %s\n' "$output"
