.PHONY: help build-host build-framework generate-aidl stage-phase1 stage-art build-host-art \
	run \
	smoke-gles3jni test-framework test-stage-art test-stage-phase1 test-aidl \
	test-launcher-native-libs test-host-android-shims test-host-gles3jni \
	prepare-test-suite-apks prepare-deps export-deps-patches status-deps-patches

ROOT := /mnt/mydata/projects2/qos/deps/NovaART
APK ?= $(ROOT)/apks/gles3jni.apk
TIMEOUT ?= 8
RUN_APK := $(word 2,$(MAKECMDGOALS))

ifeq ($(firstword $(MAKECMDGOALS)),run)
.PHONY: $(RUN_APK)
%:
	@:
endif

help:
	@printf '%s\n' \
	  'NovaART operator targets' \
	  '' \
	  '  make build-host          Build the native NovaART host binary' \
	  '  make build-framework     Build the Java framework overlay dex/jar' \
	  '  make generate-aidl       Generate Java from Nova-owned AIDLs' \
	  '  make stage-phase1        Stage Phase 1 framework sources from frameworks/base' \
	  '  make build-host-art      Build host/glibc ART runtime artifacts' \
	  '  make stage-art           Stage ART runtime into output/' \
	  '  make prepare-deps        Apply curated dependency patches into deps/' \
	  '  make export-deps-patches Refresh curated dependency patch files' \
	  '  make status-deps-patches Show dependency patch application state' \
	  '  make run /abs/path.apk   Run NovaART directly with the given APK path' \
	  '  make smoke-gles3jni      Run the bounded gles3jni smoke test' \
	  '  make test-framework      Run framework build verification' \
	  '  make test-stage-art      Run ART staging verification' \
	  '  make test-stage-phase1   Run Phase 1 staging verification' \
	  '  make test-aidl           Run AIDL generation verification' \
	  '  make test-launcher-native-libs Run APK native-lib staging verification' \
	  '  make test-host-android-shims Run host Android shim verification' \
	  '  make test-host-gles3jni  Run host gles3jni library verification' \
	  '' \
	  'Variables:' \
	  '  APK=/abs/path/app.apk    Override APK for smoke-gles3jni' \
	  '  TIMEOUT=12               Override smoke timeout in seconds'

build-host:
	@cd "$(ROOT)" && bash ./build-host.sh

build-framework:
	@cd "$(ROOT)" && bash ./scripts/build-framework.sh

generate-aidl:
	@cd "$(ROOT)" && bash ./scripts/generate-phase1-nova-aidl.sh

stage-phase1:
	@cd "$(ROOT)" && bash ./scripts/stage-phase1-framework-sources.sh

build-host-art:
	@cd "$(ROOT)" && bash ./scripts/build-host-art-runtime.sh

stage-art:
	@cd "$(ROOT)" && bash ./scripts/stage-art.sh

run:
	@test -n "$(RUN_APK)" || { \
		echo 'usage: make run /abs/path/to/app.apk' >&2; \
		exit 1; \
	}
	@ROOT="$(ROOT)"; \
	export LD_LIBRARY_PATH="$$ROOT/output/android-data/dex/native-libs:$$ROOT/output/lib:$$ROOT/output/android-root/apex/com.android.art/lib64:$$ROOT/output/android-root/apex/com.android.art/lib:$$ROOT/deps/aosp-full/out/host/linux-x86/lib64:$$ROOT/deps/aosp-full/out/host/linux-x86/lib"; \
	"$$ROOT/output/bin/novaart" "$(RUN_APK)"

prepare-deps:
	@cd "$(ROOT)" && bash ./scripts/prepare-deps.sh apply

export-deps-patches:
	@cd "$(ROOT)" && bash ./scripts/prepare-deps.sh export

status-deps-patches:
	@cd "$(ROOT)" && bash ./scripts/prepare-deps.sh status

smoke-gles3jni:
	@cd "$(ROOT)" && bash ./scripts/smoke-run-gles3jni.sh --apk "$(APK)" --timeout "$(TIMEOUT)"

test-framework:
	@cd "$(ROOT)" && bash ./scripts/test-build-framework.sh

test-stage-art:
	@cd "$(ROOT)" && bash ./scripts/test-stage-art.sh

test-stage-phase1:
	@cd "$(ROOT)" && bash ./scripts/test-stage-phase1-framework-sources.sh

test-aidl:
	@cd "$(ROOT)" && bash ./scripts/test-generate-phase1-nova-aidl.sh

test-launcher-native-libs:
	@cd "$(ROOT)" && bash ./scripts/test-launcher-native-libs.sh

test-host-android-shims:
	@cd "$(ROOT)" && bash ./scripts/test-host-android-shims.sh

test-host-gles3jni:
	@cd "$(ROOT)" && bash ./scripts/test-host-gles3jni.sh

prepare-test-suite-apks:
	@cd "$(ROOT)" && bash ./scripts/download-test-apks.sh
