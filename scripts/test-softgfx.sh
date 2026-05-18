#!/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)"

cd "$ROOT"
rtk bash ./build-host.sh >/dev/null
"$ROOT/build/host/test-softgfx"
