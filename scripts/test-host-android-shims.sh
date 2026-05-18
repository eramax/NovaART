#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
LIB="$ROOT/output/lib/libandroid.so"

fail() {
  echo "FAIL: $*" >&2
  exit 1
}

[ -f "$LIB" ] || fail "missing host libandroid shim: $LIB"

rtk readelf -d "$LIB" | grep -q '(SONAME).*libandroid.so' \
  || fail "libandroid shim missing SONAME"

echo "PASS"
