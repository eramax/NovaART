#include "core_jni_helpers.h"

/* android.graphics.Canvas — stubs */
static jlong initRaster(JNIEnv *env, jobject, jlong bitmapHandle) {
    log_unimplemented_jni("android.graphics.Canvas.initRaster");
    (void)bitmapHandle;
    return return_zero_handle();
}

static void native_setBitmap(JNIEnv *env, jobject, jlong canvasHandle, jlong bitmapHandle) {
    log_unimplemented_jni("android.graphics.Canvas.native_setBitmap");
    (void)canvasHandle; (void)bitmapHandle;
}

static void native_drawRect(JNIEnv *env, jobject, jlong canvasHandle,
                             jfloat left, jfloat top, jfloat right, jfloat bottom,
                             jlong paintHandle) {
    log_unimplemented_jni("android.graphics.Canvas.native_drawRect");
    (void)canvasHandle; (void)left; (void)top; (void)right; (void)bottom; (void)paintHandle;
}

static jint native_save(JNIEnv *env, jobject, jlong canvasHandle, jint saveFlags) {
    log_unimplemented_jni("android.graphics.Canvas.native_save");
    (void)canvasHandle; (void)saveFlags;
    return 0;
}

static void native_restore(JNIEnv *env, jobject, jlong canvasHandle) {
    log_unimplemented_jni("android.graphics.Canvas.native_restore");
    (void)canvasHandle;
}

static jint native_getWidth(JNIEnv *env, jobject, jlong canvasHandle) {
    log_unimplemented_jni("android.graphics.Canvas.native_getWidth");
    (void)canvasHandle;
    return 0;
}

static jint native_getHeight(JNIEnv *env, jobject, jlong canvasHandle) {
    log_unimplemented_jni("android.graphics.Canvas.native_getHeight");
    (void)canvasHandle;
    return 0;
}

static const JNINativeMethod gMethods[] = {
    { "initRaster",    "(J)J",                         (void*)initRaster },
    { "native_setBitmap", "(JJ)V",                     (void*)native_setBitmap },
    { "native_drawRect",  "(JFFFFJ)V",                 (void*)native_drawRect },
    { "native_save",      "(JI)I",                     (void*)native_save },
    { "native_restore",   "(J)V",                      (void*)native_restore },
    { "native_getWidth",  "(J)I",                      (void*)native_getWidth },
    { "native_getHeight", "(J)I",                      (void*)native_getHeight },
};

int register_android_graphics_Canvas(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/graphics/Canvas",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
