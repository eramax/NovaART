#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
APKS_DIR="$ROOT/apks"

PHASE_DIRS=(
  "$APKS_DIR/phase1"
  "$APKS_DIR/phase2"
  "$APKS_DIR/phase3"
  "$APKS_DIR/phase4-native"
  "$APKS_DIR/phase5-store"
)

require() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "missing required tool: $1" >&2
    exit 1
  }
}

require fdroidcl
require apkeep

mkdir -p "${PHASE_DIRS[@]}"

echo "=== Updating F-Droid index ==="
fdroidcl update

echo ""
echo "=== Phase 1: GLES Pipeline ==="
fdroidcl download org.linaro.glmark2
cp "$HOME/.cache/fdroidcl/apks/"org.linaro.glmark2_*.apk "$APKS_DIR/phase1/glmark2.apk"

echo ""
echo "=== Phase 2: Java Canvas + Input ==="
fdroidcl download com.shatteredpixel.shatteredpixeldungeon
fdroidcl download io.anuke.mindustry
fdroidcl download dev.lucanlm.antimine
fdroidcl download org.fossify.math

cp "$HOME/.cache/fdroidcl/apks/"com.shatteredpixel.shatteredpixeldungeon_*.apk "$APKS_DIR/phase2/shattered-pixel-dungeon.apk"
cp "$HOME/.cache/fdroidcl/apks/"io.anuke.mindustry_*.apk "$APKS_DIR/phase2/mindustry.apk"
cp "$HOME/.cache/fdroidcl/apks/"dev.lucanlm.antimine_*.apk "$APKS_DIR/phase2/antimine.apk"
cp "$HOME/.cache/fdroidcl/apks/"org.fossify.math_*.apk "$APKS_DIR/phase2/simple-calculator.apk"

if [ -f "$APKS_DIR/2048.apk" ]; then
  cp "$APKS_DIR/2048.apk" "$APKS_DIR/phase2/2048.apk"
else
  echo "WARNING: 2048.apk not found at apk root — download manually via fdroidcl download com.androbaby.game2048" >&2
fi

echo ""
echo "=== Phase 3: Full Framework ==="
fdroidcl download org.wikipedia
fdroidcl download org.schabi.newpipe
fdroidcl download de.danoeh.antennapod
fdroidcl download com.fsck.k9
fdroidcl download org.lichess.mobileV2

cp "$HOME/.cache/fdroidcl/apks/"org.wikipedia_*.apk "$APKS_DIR/phase3/wikipedia.apk"
cp "$HOME/.cache/fdroidcl/apks/"org.schabi.newpipe_*.apk "$APKS_DIR/phase3/newpipe.apk"
cp "$HOME/.cache/fdroidcl/apks/"de.danoeh.antennapod_*.apk "$APKS_DIR/phase3/antennapod.apk"
cp "$HOME/.cache/fdroidcl/apks/"com.fsck.k9_*.apk "$APKS_DIR/phase3/k9mail.apk"
cp "$HOME/.cache/fdroidcl/apks/"org.lichess.mobileV2_*.apk "$APKS_DIR/phase3/lichess.apk"

apkeep -a app.organicmaps -d f-droid "$APKS_DIR/phase3/"
mv "$APKS_DIR/phase3/app.organicmaps.apk" "$APKS_DIR/phase3/organic-maps.apk" 2>/dev/null || true

echo ""
echo "=== Phase 4: Native x86_64 APKs (APKPure) ==="
apkeep -a org.videolan.vlc -d apk-pure "$APKS_DIR/phase4-native/"
mv "$APKS_DIR/phase4-native/org.videolan.vlc.apk" "$APKS_DIR/phase4-native/vlc-x86_64.apk" 2>/dev/null || true

apkeep -a org.mozilla.firefox -d apk-pure "$APKS_DIR/phase4-native/"
mv "$APKS_DIR/phase4-native/org.mozilla.firefox.xapk" "$APKS_DIR/phase4-native/firefox-x86_64.xapk" 2>/dev/null || true

apkeep -a org.telegram.messenger -d apk-pure "$APKS_DIR/phase4-native/"
mv "$APKS_DIR/phase4-native/org.telegram.messenger.xapk" "$APKS_DIR/phase4-native/telegram-universal.xapk" 2>/dev/null || true

apkeep -a org.thoughtcrime.securesms -d apk-pure "$APKS_DIR/phase4-native/"
mv "$APKS_DIR/phase4-native/org.thoughtcrime.securesms.xapk" "$APKS_DIR/phase4-native/signal-universal.xapk" 2>/dev/null || true

echo ""
echo "=== Phase 5: App Store Clients ==="
fdroidcl download org.fdroid.fdroid
fdroidcl download com.aurora.store
fdroidcl download com.looker.droidify

cp "$HOME/.cache/fdroidcl/apks/"org.fdroid.fdroid_*.apk "$APKS_DIR/phase5-store/fdroid.apk"
cp "$HOME/.cache/fdroidcl/apks/"com.aurora.store_*.apk "$APKS_DIR/phase5-store/aurora-store.apk"
cp "$HOME/.cache/fdroidcl/apks/"com.looker.droidify_*.apk "$APKS_DIR/phase5-store/droidify.apk"

echo ""
echo "=== Summary ==="
find "$APKS_DIR" -type f \( -name "*.apk" -o -name "*.xapk" \) \
  -not -path "$APKS_DIR/[^/]*" \
  | sort \
  | while read -r f; do
      ls -lh "$f" | awk '{printf "  %-55s %s\n", $NF, $5}'
    done

echo ""
echo "Done — test suite APKs staged under apks/"
