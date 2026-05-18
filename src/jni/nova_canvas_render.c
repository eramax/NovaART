#include "core_jni_helpers.h"
#include "../canvas_render.h"
#include "../nova.h"
#include "../softgfx.h"
#include <string.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <unistd.h>

static struct nova_canvas_render *g_canvas_render = NULL;
static struct nova_state *g_state = NULL;
static struct nova_window *g_window = NULL;

static void notifyVsync(JNIEnv *env, jclass cls, jlong frameTimeNanos) {
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

static void dispatchMotionEvent(JNIEnv *env, jclass cls, jlong eventTime, jint action, jfloat x, jfloat y) {
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

static void dispatchKeyEvent(JNIEnv *env, jclass cls, jint action, jint keyCode, jlong eventTime, jint metaState) {
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

static void initRender(JNIEnv *env, jclass cls, jlong state, jlong window) {
    (void)env;
    g_state = (struct nova_state *)(intptr_t)state;
    g_window = (struct nova_window *)(intptr_t)window;
}

static jlong getRenderState(JNIEnv *env, jclass cls) {
    (void)env;
    return (jlong)(intptr_t)g_state;
}

static jlong getRenderWindow(JNIEnv *env, jclass cls) {
    (void)env;
    return (jlong)(intptr_t)g_window;
}

static void setRenderState(JNIEnv *env, jclass cls, jlong state) {
    (void)env;
    g_state = (struct nova_state *)(intptr_t)state;
}

static void setRenderWindow(JNIEnv *env, jclass cls, jlong window) {
    (void)env;
    g_window = (struct nova_window *)(intptr_t)window;
}

static void cleanupRender(JNIEnv *env, jclass cls) {
    (void)env;
    if (g_canvas_render) {
        nova_canvas_render_destroy(g_canvas_render);
        g_canvas_render = NULL;
    }
}

static void submitFrame(JNIEnv *env, jclass cls, jobject bitmap) {
    if (!g_state || !g_window || !bitmap) {
        return;
    }

    jclass bitmap_class = (*env)->GetObjectClass(env, bitmap);
    if (!bitmap_class) return;

    jmethodID get_width = (*env)->GetMethodID(env, bitmap_class, "getWidth", "()I");
    jmethodID get_height = (*env)->GetMethodID(env, bitmap_class, "getHeight", "()I");
    jmethodID get_pixels = (*env)->GetMethodID(env, bitmap_class, "getPixels", "()[I");

    if (!get_width || !get_height || !get_pixels) {
        (*env)->DeleteLocalRef(env, bitmap_class);
        return;
    }

    jint width = (*env)->CallIntMethod(env, bitmap, get_width);
    jint height = (*env)->CallIntMethod(env, bitmap, get_height);

    jintArray pixel_array = (jintArray)(*env)->CallObjectMethod(env, bitmap, get_pixels);
    if (!pixel_array) {
        (*env)->DeleteLocalRef(env, bitmap_class);
        return;
    }

    jint *pixels = (*env)->GetIntArrayElements(env, pixel_array, NULL);
    if (!pixels) {
        (*env)->DeleteLocalRef(env, pixel_array);
        (*env)->DeleteLocalRef(env, bitmap_class);
        return;
    }

    /* Create or recreate canvas_render if needed */
    if (!g_canvas_render || nova_canvas_render_get_width(g_canvas_render) != width || nova_canvas_render_get_height(g_canvas_render) != height) {
        if (g_canvas_render) {
            nova_canvas_render_destroy(g_canvas_render);
        }
        g_canvas_render = nova_canvas_render_create(g_state, width, height);
        if (!g_canvas_render) {
            (*env)->ReleaseIntArrayElements(env, pixel_array, pixels, JNI_ABORT);
            (*env)->DeleteLocalRef(env, pixel_array);
            (*env)->DeleteLocalRef(env, bitmap_class);
            return;
        }
    }

    /* Copy pixels from Java bitmap to the canvas render backbuffer */
    struct nova_bitmap *backbuffer = nova_canvas_render_get_backbuffer(g_canvas_render);
    if (backbuffer) {
        uint32_t *dest_pixels = nova_bitmap_pixels(backbuffer);
        if (dest_pixels) {
            int pixel_count = width * height;
            memcpy(dest_pixels, pixels, pixel_count * sizeof(uint32_t));
        }
    }

    /* Submit the frame to Wayland */
    nova_canvas_render_submit(g_canvas_render, g_window);

    (*env)->ReleaseIntArrayElements(env, pixel_array, pixels, JNI_ABORT);
    (*env)->DeleteLocalRef(env, pixel_array);
    (*env)->DeleteLocalRef(env, bitmap_class);
}

static const JNINativeMethod gMethods[] = {
    { "notifyVsync",         "(J)V",                  (void*)notifyVsync },
    { "dispatchMotionEvent", "(JIFF)V",              (void*)dispatchMotionEvent },
    { "dispatchKeyEvent",    "(IIJI)V",              (void*)dispatchKeyEvent },
    { "submitFrame",         "(Landroid/graphics/Bitmap;)V", (void*)submitFrame },
    { "initRender",          "(JJ)V",                (void*)initRender },
    { "getRenderState",      "()J",                  (void*)getRenderState },
    { "getRenderWindow",     "()J",                  (void*)getRenderWindow },
    { "setRenderState",      "(J)V",                 (void*)setRenderState },
    { "setRenderWindow",     "(J)V",                 (void*)setRenderWindow },
    { "cleanupRender",       "()V",                  (void*)cleanupRender },
};

int register_nova_canvas_render(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "nova/internal/CanvasRender",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
