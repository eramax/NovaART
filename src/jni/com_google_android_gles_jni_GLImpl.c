#include "core_jni_helpers.h"

static void _nativeClassInit(JNIEnv *env, jclass clazz) {
    (void)env;
    (void)clazz;
}

static const JNINativeMethod gMethods[] = {
    { "_nativeClassInit", "()V", (void *)_nativeClassInit },
};

int register_com_google_android_gles_jni_GLImpl(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "com/google/android/gles_jni/GLImpl",
                                gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
