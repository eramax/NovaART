#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
AOSP_ROOT="$ROOT/deps/aosp-full"
OUT_ROOT="$ROOT/out/framework"
JAVAC_BIN="${JAVAC:-$(command -v javac)}"
JAR_BIN="${JAR:-$(command -v jar)}"
D8_BIN="$AOSP_ROOT/out/host/linux-x86/bin/d8"
GENERATE_AIDL=1
RUN_D8=1

usage() {
  cat <<'EOF'
Usage: build-framework.sh [options]

Builds the minimal NovaART Phase 1 Java overlay:
- generates Java from the slim Nova-owned AIDL set
- compiles the selected OpenGL/EGL source surface plus Nova shims
- packages compiled classes into nova-framework-classes.jar
- optionally runs d8 to emit a dex jar

Options:
  --project-root PATH   Override NovaART root
  --aosp-root PATH      Override AOSP master-art tree
  --out-root PATH       Override output directory
  --javac-bin PATH      Override javac binary
  --jar-bin PATH        Override jar binary
  --d8-bin PATH         Override d8 binary
  --skip-aidl           Do not regenerate AIDL Java
  --compile-only        Stop after javac + classes jar packaging
  -h, --help            Show this help
EOF
}

while (($# > 0)); do
  case "$1" in
    --project-root)
      ROOT="$2"
      shift 2
      ;;
    --aosp-root)
      AOSP_ROOT="$2"
      shift 2
      ;;
    --out-root)
      OUT_ROOT="$2"
      shift 2
      ;;
    --javac-bin)
      JAVAC_BIN="$2"
      shift 2
      ;;
    --jar-bin)
      JAR_BIN="$2"
      shift 2
      ;;
    --d8-bin)
      D8_BIN="$2"
      shift 2
      ;;
    --skip-aidl)
      GENERATE_AIDL=0
      shift
      ;;
    --compile-only)
      RUN_D8=0
      shift
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

[ -x "$JAVAC_BIN" ] || {
  echo "missing javac: $JAVAC_BIN" >&2
  exit 1
}
[ -x "$JAR_BIN" ] || {
  echo "missing jar: $JAR_BIN" >&2
  exit 1
}
[ -d "$ROOT" ] || {
  echo "missing project root: $ROOT" >&2
  exit 1
}
[ -d "$AOSP_ROOT" ] || {
  echo "missing AOSP root: $AOSP_ROOT" >&2
  exit 1
}

if ((GENERATE_AIDL)); then
  "$ROOT/scripts/generate-phase1-nova-aidl.sh" \
    --aidl-bin "$AOSP_ROOT/out/host/linux-x86/bin/aidl" \
    --dest-root "$ROOT"
fi

mkdir -p "$OUT_ROOT"
TMPDIR="$(mktemp -d)"
trap 'rm -rf "$TMPDIR"' EXIT

SOURCES_FILE="$TMPDIR/sources.txt"
CLASSES_DIR="$OUT_ROOT/classes"
JAR_OUT="$OUT_ROOT/nova-framework-classes.jar"
DEX_OUT="$OUT_ROOT/nova-framework-dex.jar"

# Collect all sources: staged AOSP (real implementations) + Nova overrides + generated AIDL
# nova-shims/ is listed AFTER aosp/ so that if the same class appears in both,
# the build fails fast with a duplicate-class error rather than silently using the wrong one.
find \
  "$ROOT/src/java/aosp" \
  "$ROOT/src/java/nova-shims" \
  "$ROOT/src/generated/aidl/nova" \
  -type f -name '*.java' | sort > "$SOURCES_FILE"

[ -s "$SOURCES_FILE" ] || {
  echo "no Java sources found for framework build" >&2
  exit 1
}

JARS=(
  "$AOSP_ROOT/prebuilts/sdk/35/module-lib/android.jar"
  "$AOSP_ROOT/prebuilts/sdk/35/module-lib/android-non-updatable.jar"
  "$AOSP_ROOT/prebuilts/sdk/35/module-lib/art.jar"
  "$AOSP_ROOT/prebuilts/sdk/35/module-lib/framework-graphics.jar"
  "$AOSP_ROOT/out/target/product/module_x86_64/apex/com.android.art/javalib/core-oj.jar"
  "$AOSP_ROOT/out/target/product/module_x86_64/apex/com.android.art/javalib/core-libart.jar"
  "$AOSP_ROOT/out/target/product/module_x86_64/apex/com.android.art/javalib/okhttp.jar"
  "$AOSP_ROOT/out/target/product/module_x86_64/apex/com.android.art/javalib/bouncycastle.jar"
  "$AOSP_ROOT/out/target/product/module_x86_64/apex/com.android.art/javalib/apache-xml.jar"
)

for jar_path in "${JARS[@]}"; do
  [ -f "$jar_path" ] || {
    echo "missing required jar: $jar_path" >&2
    exit 1
  }
done

CP=$(printf '%s:' "${JARS[@]}")
CP=${CP%:}

rm -rf "$CLASSES_DIR"
mkdir -p "$CLASSES_DIR"

"$JAVAC_BIN" \
  -source 17 \
  -target 17 \
  -cp "$CP" \
  -d "$CLASSES_DIR" \
  @"$SOURCES_FILE"

rm -f "$JAR_OUT"
"$JAR_BIN" --create --file "$JAR_OUT" -C "$CLASSES_DIR" .

if ((RUN_D8)); then
  [ -x "$D8_BIN" ] || {
    echo "missing d8: $D8_BIN" >&2
    exit 1
  }
  rm -f "$DEX_OUT"
  "$D8_BIN" \
    --min-api 35 \
    --lib "$AOSP_ROOT/prebuilts/sdk/35/module-lib/android.jar" \
    --output "$DEX_OUT" \
    "$JAR_OUT"
fi

echo "built framework overlay into $OUT_ROOT"
