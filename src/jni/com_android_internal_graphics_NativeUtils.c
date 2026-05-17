#include "core_jni_helpers.h"

/* com.android.internal.graphics.NativeUtils — stubs */
static void native_configureHdrScreenSdcard(JNIEnv *env, jclass) {
    log_unimplemented_jni("com.android.internal.graphics.NativeUtils.native_configureHdrScreenSdcard");
}

static const JNINativeMethod gMethods[] = {
    { "native_configureHdrScreenSdcard", "()V", (void*)native_configureHdrScreenSdcard },
};

int register_com_android_internal_graphics_NativeUtils(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "com/android/internal/graphics/NativeUtils",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
