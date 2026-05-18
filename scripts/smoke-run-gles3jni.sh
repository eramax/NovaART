#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
APK="$ROOT/apks/gles3jni.apk"
TIMEOUT_SECONDS=8

usage() {
  cat <<'EOF'
Usage: smoke-run-gles3jni.sh [options]

Runs NovaART against gles3jni under a bounded timeout and fails fast on the
known bootstrap/runtime integration errors we have already eliminated.

Options:
  --project-root PATH   Override NovaART root
  --apk PATH            Override APK path
  --timeout SEC         Timeout in seconds (default: 8)
  -h, --help            Show this help
EOF
}

while (($# > 0)); do
  case "$1" in
    --project-root)
      ROOT="$2"
      shift 2
      ;;
    --apk)
      APK="$2"
      shift 2
      ;;
    --timeout)
      TIMEOUT_SECONDS="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "unknown argument: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

BIN="$ROOT/output/bin/novaart"
[ -x "$BIN" ] || {
  echo "missing NovaART binary: $BIN" >&2
  exit 1
}
[ -f "$APK" ] || {
  echo "missing APK: $APK" >&2
  exit 1
}

export LD_LIBRARY_PATH="$ROOT/output/lib:$ROOT/output/android-root/apex/com.android.art/lib64:$ROOT/output/android-root/apex/com.android.art/lib:$ROOT/deps/aosp-full/out/host/linux-x86/lib64:$ROOT/deps/aosp-full/out/host/linux-x86/lib"
export LD_LIBRARY_PATH="$ROOT/output/android-data/dex/native-libs:$LD_LIBRARY_PATH"

LOG_FILE="$(mktemp)"
trap 'rm -f "$LOG_FILE"' EXIT

set +e
timeout --signal=TERM "${TIMEOUT_SECONDS}s" "$BIN" "$APK" >"$LOG_FILE" 2>&1
STATUS=$?
set -e

cat "$LOG_FILE"

if grep -Eq 'Failed to register JNI stub|Failed to register natives for|ClassNotFoundException|NoSuchMethodError|Runtime aborting|Unable to initialize main class|UnsatisfiedLinkError|No implementation found for|Exception in thread "GLThread' "$LOG_FILE"; then
  echo "smoke run hit a known bootstrap/runtime failure" >&2
  exit 1
fi

if ! grep -Fq '[GLES3JNI] JNI init' "$LOG_FILE"; then
  echo "smoke run did not reach native gles3jni init" >&2
  exit 1
fi

if ! grep -Fq '[GLES3JNI] JNI resize' "$LOG_FILE"; then
  echo "smoke run did not reach native gles3jni resize" >&2
  exit 1
fi

if ! grep -Fq '[GLES3JNI] JNI first frame' "$LOG_FILE"; then
  echo "smoke run did not reach first rendered frame" >&2
  exit 1
fi

case "$STATUS" in
  124)
    echo "smoke run passed: renderer initialized and first frame executed within ${TIMEOUT_SECONDS}s"
    ;;
  0)
    echo "smoke run completed after renderer initialization and first frame"
    ;;
  *)
    echo "smoke run exited with status $STATUS" >&2
    exit "$STATUS"
    ;;
esac
