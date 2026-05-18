#include "core_jni_helpers.h"
#include "softgfx.h"

static void native_setAntiAlias(JNIEnv *env, jobject, jlong paintHandle, jboolean aa) {
    (void)env;
    nova_paint_set_anti_alias((struct nova_paint *)(intptr_t)paintHandle, aa == JNI_TRUE);
}

static void native_setColor(JNIEnv *env, jobject, jlong paintHandle, jint color) {
    (void)env;
    nova_paint_set_color((struct nova_paint *)(intptr_t)paintHandle, (uint32_t)color);
}

static void native_setStrokeWidth(JNIEnv *env, jobject, jlong paintHandle, jfloat width) {
    (void)env;
    nova_paint_set_stroke_width((struct nova_paint *)(intptr_t)paintHandle, width);
}

static void native_setStyle(JNIEnv *env, jobject, jlong paintHandle, jint style) {
    (void)env;
    nova_paint_set_style((struct nova_paint *)(intptr_t)paintHandle, style);
}

static jint native_getColor(JNIEnv *env, jobject, jlong paintHandle) {
    (void)env;
    return (jint)nova_paint_get_color((struct nova_paint *)(intptr_t)paintHandle);
}

static jfloat native_getStrokeWidth(JNIEnv *env, jobject, jlong paintHandle) {
    (void)env;
    return (jfloat)nova_paint_get_stroke_width((struct nova_paint *)(intptr_t)paintHandle);
}

static jlong native_getNativeFinalizer(JNIEnv *env, jclass) {
    (void)env;
    return return_zero_handle();
}

static jlong native_init(JNIEnv *env, jobject) {
    (void)env;
    return (jlong)(intptr_t)nova_paint_create();
}

static jlong native_initWithPaint(JNIEnv *env, jobject, jlong paintHandle) {
    (void)env;
    return (jlong)(intptr_t)nova_paint_clone((struct nova_paint *)(intptr_t)paintHandle);
}

static const JNINativeMethod gMethods[] = {
    { "native_setAntiAlias",    "(JZ)V",   (void*)native_setAntiAlias },
    { "native_setColor",        "(JI)V",   (void*)native_setColor },
    { "native_setStrokeWidth",  "(JF)V",   (void*)native_setStrokeWidth },
    { "native_setStyle",        "(JI)V",   (void*)native_setStyle },
    { "native_getColor",          "(J)I",  (void*)native_getColor },
    { "native_getStrokeWidth",    "(J)F",  (void*)native_getStrokeWidth },
    { "native_getNativeFinalizer", "()J",  (void*)native_getNativeFinalizer },
    { "native_init",            "()J",     (void*)native_init },
    { "native_initWithPaint",   "(J)J",    (void*)native_initWithPaint },
};

int register_android_graphics_Paint(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/graphics/Paint",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
