#!/usr/bin/env bash
# Watches the project and rebuilds + relaunches the app on the connected emulator/device
# whenever a source or build file changes. Stop with Ctrl+C.
set -u

cd "$(dirname "$0")"

PACKAGE="com.example.ac1calculator"
ACTIVITY=".MainActivity"
WATCH_PATHS=(app/src/main app/build.gradle.kts build.gradle.kts settings.gradle.kts gradle/libs.versions.toml)
INTERVAL=1

marker="$(mktemp)"
trap 'rm -f "$marker"; echo; echo "Watcher stopped."; exit 0' INT TERM

log() { echo "[$(date +%T)] $*"; }

deploy() {
  log "Building and installing..."
  if ./gradlew installDebug -q; then
    adb shell am start -S -n "$PACKAGE/$ACTIVITY" >/dev/null && log "App reloaded."
  else
    log "Build failed. Fix the error and save again."
  fi
}

log "Waiting for a device..."
adb wait-for-device

touch "$marker"
deploy
log "Watching for changes (Ctrl+C to stop)..."

while true; do
  sleep "$INTERVAL"
  changed="$(find "${WATCH_PATHS[@]}" -type f -newer "$marker" -print -quit 2>/dev/null)"
  if [ -n "$changed" ]; then
    sleep 0.5 # let the editor finish writing
    # Touch the marker before building so edits made during the build trigger another run.
    touch "$marker"
    log "Change detected: $changed"
    deploy
  fi
done
