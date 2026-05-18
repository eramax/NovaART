#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
MODE="apply"

usage() {
  cat <<'EOF'
Usage: prepare-deps.sh [apply|status|export]

Curated dependency patch workflow for NovaART.

Modes:
  apply   Apply checked-in dependency patches into the local deps tree.
  status  Show whether checked-in dependency patches are applied cleanly.
  export  Regenerate checked-in dependency patches from curated local changes.
EOF
}

while (($# > 0)); do
  case "$1" in
    apply|status|export)
      MODE="$1"
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

fail() {
  echo "FAIL: $*" >&2
  exit 1
}

patch_path() {
  case "$1" in
    aosp-full:prebuilts-clang-host-linux-x86)
      printf '%s\n' "$ROOT/patches/deps/aosp-full/prebuilts-clang-host-linux-x86.patch"
      ;;
    *)
      return 1
      ;;
  esac
}

project_path() {
  case "$1" in
    aosp-full:prebuilts-clang-host-linux-x86)
      printf '%s\n' "$ROOT/deps/aosp-full/prebuilts/clang/host/linux-x86"
      ;;
    *)
      return 1
      ;;
  esac
}

export_patch() {
  local key="$1"
  local patch_file
  local project_dir

  patch_file="$(patch_path "$key")"
  project_dir="$(project_path "$key")"
  mkdir -p "$(dirname "$patch_file")"

  (
    cd "$project_dir"
    git diff -- Android.bp > "$patch_file"
  )

  if [[ ! -s "$patch_file" ]]; then
    fail "curated patch exported empty for $key"
  fi

  echo "exported: $patch_file"
}

status_patch() {
  local key="$1"
  local patch_file
  local project_dir

  patch_file="$(patch_path "$key")"
  project_dir="$(project_path "$key")"
  [[ -f "$patch_file" ]] || fail "missing patch file: $patch_file"

  if (cd "$project_dir" && git apply --reverse --check "$patch_file" >/dev/null 2>&1); then
    echo "applied: $key"
    return 0
  fi

  if (cd "$project_dir" && git apply --check "$patch_file" >/dev/null 2>&1); then
    echo "not-applied: $key"
    return 0
  fi

  echo "diverged: $key"
}

apply_patch_file() {
  local key="$1"
  local patch_file
  local project_dir

  patch_file="$(patch_path "$key")"
  project_dir="$(project_path "$key")"
  [[ -f "$patch_file" ]] || fail "missing patch file: $patch_file"

  if (cd "$project_dir" && git apply --reverse --check "$patch_file" >/dev/null 2>&1); then
    echo "already applied: $key"
    return 0
  fi

  (
    cd "$project_dir"
    git apply --check "$patch_file"
    git apply "$patch_file"
  )
  echo "applied: $key"
}

run_mode() {
  local key
  for key in aosp-full:prebuilts-clang-host-linux-x86; do
    case "$MODE" in
      apply)
        apply_patch_file "$key"
        ;;
      status)
        status_patch "$key"
        ;;
      export)
        export_patch "$key"
        ;;
    esac
  done
}

run_mode
