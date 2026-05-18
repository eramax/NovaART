#include "core_jni_helpers.h"
#include "softgfx.h"

static jlong nativeCreate(JNIEnv *env, jclass, jint width, jint height, jboolean config, jboolean mutable,
                           jlong density) {
    struct nova_bitmap *bitmap;

    (void)env;
    (void)density;
    bitmap = nova_bitmap_create(width, height, config ? 1 : 0, mutable == JNI_TRUE);
    return (jlong)(intptr_t)bitmap;
}

static void nativeRecycle(JNIEnv *env, jobject, jlong bitmapHandle) {
    (void)env;
    nova_bitmap_destroy((struct nova_bitmap *)(intptr_t)bitmapHandle);
}

static jint nativeGetWidth(JNIEnv *env, jobject, jlong bitmapHandle) {
    (void)env;
    return nova_bitmap_width((struct nova_bitmap *)(intptr_t)bitmapHandle);
}

static jint nativeGetHeight(JNIEnv *env, jobject, jlong bitmapHandle) {
    (void)env;
    return nova_bitmap_height((struct nova_bitmap *)(intptr_t)bitmapHandle);
}

static jint nativeGetConfig(JNIEnv *env, jobject, jlong bitmapHandle) {
    (void)env;
    return nova_bitmap_config((struct nova_bitmap *)(intptr_t)bitmapHandle);
}

static jlong nativeGetNativeFinalizer(JNIEnv *env, jclass) {
    (void)env;
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
