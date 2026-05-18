#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
LIB="$ROOT/output/lib/libgles3jni.so"

fail() {
  echo "FAIL: $*" >&2
  exit 1
}

[ -f "$LIB" ] || fail "missing host libgles3jni: $LIB"

rtk readelf -d "$LIB" | grep -q 'Shared library: \[libEGL.so' \
  || fail "host libgles3jni is not linked against libEGL"
rtk readelf --version-info "$LIB" | grep -q 'GLIBC' \
  || fail "host libgles3jni is not glibc-linked"

echo "PASS"
