#!/usr/bin/env bash
set -euo pipefail

cat <<'EOF'
# Reproducible NovaART ART sync/build commands
#
# Purpose:
# - align deps/aosp-full to the ART thin manifest (`master-art`)
# - sync the tree in place using existing repo object storage
# - build host-side ART outputs needed by NovaART Stage 0
#
# Run from: /mnt/mydata/projects2/qos/deps/NovaART/deps/aosp-full

repo init --partial-clone --no-use-superproject \
  -b master-art \
  -u https://android.googlesource.com/platform/manifest

# Use this if the tree is already dirty from a previous broader manifest and
# repo needs to remove projects that are no longer part of master-art.
repo sync -c -j1 --fail-fast --force-remove-dirty

# Normal follow-up sync once the tree is aligned:
# repo sync -c -j"$(nproc)"

source build/envsetup.sh
banchan com.android.art x86_64
m com.android.art -j"$(nproc)"

# Optional logging:
# m com.android.art -j"$(nproc)" 2>&1 | tee /tmp/novaart-art-build.log
EOF
