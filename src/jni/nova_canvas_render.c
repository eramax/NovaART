#include "core_jni_helpers.h"
#include "../canvas_render.h"

static struct nova_canvas_render *g_canvas_render = NULL;

static void notifyVsync(JNIEnv *env, jclass, jlong frameTimeNanos) {
    jclass choreographer_class = (*env)->FindClass(env, "android/view/Choreographer");
    if (!choreographer_class) {
        return;
    }

    jmethodID notify_method = (*env)->GetStaticMethodID(env, choreographer_class,
                                                        "notifyFrameTime", "(J)V");
    if (!notify_method) {
        (*env)->DeleteLocalRef(env, choreographer_class);
        return;
    }

    (*env)->CallStaticVoidMethod(env, choreographer_class, notify_method, frameTimeNanos);
    (*env)->DeleteLocalRef(env, choreographer_class);
}

static void dispatchMotionEvent(JNIEnv *env, jclass, jlong eventTime, jint action, jfloat x, jfloat y) {
    jclass dispatcher_class = (*env)->FindClass(env, "nova/internal/ViewDispatcher");
    if (!dispatcher_class) {
        return;
    }

    jmethodID dispatch_method = (*env)->GetStaticMethodID(env, dispatcher_class,
                                                          "dispatchMotionEvent", "(JIFF)V");
    if (!dispatch_method) {
        (*env)->DeleteLocalRef(env, dispatcher_class);
        return;
    }

    (*env)->CallStaticVoidMethod(env, dispatcher_class, dispatch_method, eventTime, action, x, y);
    (*env)->DeleteLocalRef(env, dispatcher_class);
}

static void dispatchKeyEvent(JNIEnv *env, jclass, jint action, jint keyCode, jlong eventTime, jint metaState) {
    jclass dispatcher_class = (*env)->FindClass(env, "nova/internal/ViewDispatcher");
    if (!dispatcher_class) {
        return;
    }

    jmethodID dispatch_method = (*env)->GetStaticMethodID(env, dispatcher_class,
                                                          "dispatchKeyEvent", "(IIJI)V");
    if (!dispatch_method) {
        (*env)->DeleteLocalRef(env, dispatcher_class);
        return;
    }

    (*env)->CallStaticVoidMethod(env, dispatcher_class, dispatch_method, action, keyCode, eventTime, metaState);
    (*env)->DeleteLocalRef(env, dispatcher_class);
}

static void submitFrame(JNIEnv *env, jclass, jobject bitmap) {
    (void)env;
    (void)bitmap;
}

static const JNINativeMethod gMethods[] = {
    { "notifyVsync",         "(J)V",                  (void*)notifyVsync },
    { "dispatchMotionEvent", "(JIFF)V",              (void*)dispatchMotionEvent },
    { "dispatchKeyEvent",    "(IIJI)V",              (void*)dispatchKeyEvent },
    { "submitFrame",         "(Landroid/graphics/Bitmap;)V", (void*)submitFrame },
};

int register_nova_canvas_render(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "nova/internal/CanvasRender",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
