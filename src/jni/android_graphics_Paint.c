#include "core_jni_helpers.h"

/* android.graphics.Paint — stubs */
static void native_setAntiAlias(JNIEnv *env, jobject, jlong paintHandle, jboolean aa) {
    log_unimplemented_jni("android.graphics.Paint.native_setAntiAlias");
    (void)paintHandle; (void)aa;
}

static void native_setColor(JNIEnv *env, jobject, jlong paintHandle, jint color) {
    log_unimplemented_jni("android.graphics.Paint.native_setColor");
    (void)paintHandle; (void)color;
}

static void native_setStrokeWidth(JNIEnv *env, jobject, jlong paintHandle, jfloat width) {
    log_unimplemented_jni("android.graphics.Paint.native_setStrokeWidth");
    (void)paintHandle; (void)width;
}

static void native_setStyle(JNIEnv *env, jobject, jlong paintHandle, jint style) {
    log_unimplemented_jni("android.graphics.Paint.native_setStyle");
    (void)paintHandle; (void)style;
}

static jlong native_getNativeFinalizer(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.graphics.Paint.native_getNativeFinalizer");
    return return_zero_handle();
}

static jlong native_init(JNIEnv *env, jobject) {
    log_unimplemented_jni("android.graphics.Paint.native_init");
    return return_zero_handle();
}

static jlong native_initWithPaint(JNIEnv *env, jobject, jlong paintHandle) {
    log_unimplemented_jni("android.graphics.Paint.native_initWithPaint");
    (void)paintHandle;
    return return_zero_handle();
}

static const JNINativeMethod gMethods[] = {
    { "native_setAntiAlias",    "(JZ)V",   (void*)native_setAntiAlias },
    { "native_setColor",        "(JI)V",   (void*)native_setColor },
    { "native_setStrokeWidth",  "(JF)V",   (void*)native_setStrokeWidth },
    { "native_setStyle",        "(JI)V",   (void*)native_setStyle },
    { "native_getNativeFinalizer", "()J",  (void*)native_getNativeFinalizer },
    { "native_init",            "()J",     (void*)native_init },
    { "native_initWithPaint",   "(J)J",    (void*)native_initWithPaint },
};

int register_android_graphics_Paint(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/graphics/Paint",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
