#include "core_jni_helpers.h"
#include "softgfx.h"

static jlong initRaster(JNIEnv *env, jobject, jlong bitmapHandle) {
    (void)env;
    return (jlong)(intptr_t)nova_canvas_create((struct nova_bitmap *)(intptr_t)bitmapHandle);
}

static void native_setBitmap(JNIEnv *env, jobject, jlong canvasHandle, jlong bitmapHandle) {
    (void)env;
    nova_canvas_set_bitmap((struct nova_canvas *)(intptr_t)canvasHandle,
                           (struct nova_bitmap *)(intptr_t)bitmapHandle);
}

static void native_drawRect(JNIEnv *env, jobject, jlong canvasHandle,
                             jfloat left, jfloat top, jfloat right, jfloat bottom,
                             jlong paintHandle) {
    (void)env;
    nova_canvas_draw_rect((struct nova_canvas *)(intptr_t)canvasHandle,
                          left, top, right, bottom,
                          (struct nova_paint *)(intptr_t)paintHandle);
}

static void native_drawColor(JNIEnv *env, jobject, jlong canvasHandle, jint color) {
    (void)env;
    nova_canvas_draw_color((struct nova_canvas *)(intptr_t)canvasHandle, (uint32_t)color);
}

static void native_drawText(JNIEnv *env, jobject, jlong canvasHandle, jstring text, jfloat x, jfloat y, jlong paintHandle) {
    (void)env;
    (void)canvasHandle;
    (void)text;
    (void)x;
    (void)y;
    (void)paintHandle;
}

static void native_drawBitmap(JNIEnv *env, jobject, jlong canvasHandle, jlong bitmapHandle, jfloat left, jfloat top, jlong paintHandle) {
    (void)env;
    (void)canvasHandle;
    (void)bitmapHandle;
    (void)left;
    (void)top;
    (void)paintHandle;
}

static void native_drawCircle(JNIEnv *env, jobject, jlong canvasHandle, jfloat cx, jfloat cy, jfloat radius, jlong paintHandle) {
    (void)env;
    (void)canvasHandle;
    (void)cx;
    (void)cy;
    (void)radius;
    (void)paintHandle;
}

static void native_drawLine(JNIEnv *env, jobject, jlong canvasHandle, jfloat startX, jfloat startY, jfloat stopX, jfloat stopY, jlong paintHandle) {
    (void)env;
    (void)canvasHandle;
    (void)startX;
    (void)startY;
    (void)stopX;
    (void)stopY;
    (void)paintHandle;
}

static jint native_save(JNIEnv *env, jobject, jlong canvasHandle, jint saveFlags) {
    (void)env;
    return nova_canvas_save((struct nova_canvas *)(intptr_t)canvasHandle, saveFlags);
}

static void native_restore(JNIEnv *env, jobject, jlong canvasHandle) {
    (void)env;
    nova_canvas_restore((struct nova_canvas *)(intptr_t)canvasHandle);
}

static jint native_getWidth(JNIEnv *env, jobject, jlong canvasHandle) {
    (void)env;
    return nova_canvas_width((struct nova_canvas *)(intptr_t)canvasHandle);
}

static jint native_getHeight(JNIEnv *env, jobject, jlong canvasHandle) {
    (void)env;
    return nova_canvas_height((struct nova_canvas *)(intptr_t)canvasHandle);
}

static const JNINativeMethod gMethods[] = {
    { "initRaster",    "(J)J",                         (void*)initRaster },
    { "native_setBitmap", "(JJ)V",                     (void*)native_setBitmap },
    { "native_drawRect",  "(JFFFFJ)V",                 (void*)native_drawRect },
    { "native_drawColor", "(JI)V",                     (void*)native_drawColor },
    { "native_drawText",  "(JLjava/lang/String;FFJ)V", (void*)native_drawText },
    { "native_drawBitmap", "(JJFFJ)V",                 (void*)native_drawBitmap },
    { "native_drawCircle", "(JFFFJ)V",                 (void*)native_drawCircle },
    { "native_drawLine", "(JFFFFJ)V",                  (void*)native_drawLine },
    { "native_save",      "(JI)I",                     (void*)native_save },
    { "native_restore",   "(J)V",                      (void*)native_restore },
    { "native_getWidth",  "(J)I",                      (void*)native_getWidth },
    { "native_getHeight", "(J)I",                      (void*)native_getHeight },
};

int register_android_graphics_Canvas(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/graphics/Canvas",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
