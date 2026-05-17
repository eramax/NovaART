#include "core_jni_helpers.h"

/* android.graphics.Bitmap — stubs */
static jlong nativeCreate(JNIEnv *env, jclass, jint width, jint height, jint config, jboolean mutable,
                           jlong density) {
    log_unimplemented_jni("android.graphics.Bitmap.nativeCreate");
    (void)width; (void)height; (void)config; (void)mutable; (void)density;
    return return_zero_handle();
}

static void nativeRecycle(JNIEnv *env, jobject, jlong bitmapHandle) {
    log_unimplemented_jni("android.graphics.Bitmap.nativeRecycle");
    (void)bitmapHandle;
}

static jint nativeGetWidth(JNIEnv *env, jobject, jlong bitmapHandle) {
    log_unimplemented_jni("android.graphics.Bitmap.nativeGetWidth");
    (void)bitmapHandle;
    return 0;
}

static jint nativeGetHeight(JNIEnv *env, jobject, jlong bitmapHandle) {
    log_unimplemented_jni("android.graphics.Bitmap.nativeGetHeight");
    (void)bitmapHandle;
    return 0;
}

static jint nativeGetConfig(JNIEnv *env, jobject, jlong bitmapHandle) {
    log_unimplemented_jni("android.graphics.Bitmap.nativeGetConfig");
    (void)bitmapHandle;
    return 0;
}

static jlong nativeGetNativeFinalizer(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.graphics.Bitmap.nativeGetNativeFinalizer");
    return return_zero_handle();
}

static const JNINativeMethod gMethods[] = {
    { "nativeCreate",              "(IIZZJ)J", (void*)nativeCreate },
    { "nativeRecycle",             "(J)V",     (void*)nativeRecycle },
    { "nativeGetWidth",            "(J)I",     (void*)nativeGetWidth },
    { "nativeGetHeight",           "(J)I",     (void*)nativeGetHeight },
    { "nativeGetConfig",           "(J)I",     (void*)nativeGetConfig },
    { "nativeGetNativeFinalizer",  "()J",      (void*)nativeGetNativeFinalizer },
};

int register_android_graphics_Bitmap(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/graphics/Bitmap",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
