#!/usr/bin/env bash
set -euo pipefail

cat <<'EOF'
# Reproducible NovaART framework-source worktree commands
#
# Purpose:
# - keep deps/aosp-full as the known-good ART build tree on master-art
# - create a second repo client using repo's experimental `--worktree` mode
# - use that second tree only for framework source acquisition
#
# Recommended paths:
# - ART tree: /mnt/mydata/projects2/qos/deps/NovaART/deps/aosp-full
# - framework source tree: /mnt/mydata/projects2/qos/deps/NovaART/deps/aosp-framework-src
#
# Run from anywhere.

# 1. Create the framework-source repo client.
mkdir -p /mnt/mydata/projects2/qos/deps/NovaART/deps/aosp-framework-src
cd /mnt/mydata/projects2/qos/deps/NovaART/deps/aosp-framework-src

# 2. Initialize the manifest for a broader source tree.
# If you later decide to pin to a different branch, change only -b.
repo init --worktree --partial-clone --no-use-superproject \
  -b android-latest-release \
  -u https://android.googlesource.com/platform/manifest

# 3. Sync only the source repos NovaART needs for framework extraction/forking.
repo sync -c -j"$(nproc)" \
  frameworks/base \
  external/skia \
  external/freetype \
  external/harfbuzz_ng \
  external/libpng \
  external/icu

# 4. Use this worktree as source input only.
# Do not run the ART Stage 0 build here.
EOF
