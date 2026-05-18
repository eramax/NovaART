#include "core_jni_helpers.h"
#include "../nova.h"

#include <EGL/egl.h>
#include <stdint.h>
#include <stdlib.h>

static jfieldID get_long_field(JNIEnv *env, jclass cls, const char *name) {
    return (*env)->GetFieldID(env, cls, name, "J");
}

static EGLDisplay unwrap_display(JNIEnv *env, jobject display_obj) {
    jclass cls;
    jfieldID field;
    if (display_obj == NULL) {
        return EGL_NO_DISPLAY;
    }
    cls = (*env)->GetObjectClass(env, display_obj);
    field = get_long_field(env, cls, "mEGLDisplay");
    return (EGLDisplay)(uintptr_t)(*env)->GetLongField(env, display_obj, field);
}

static EGLContext unwrap_context(JNIEnv *env, jobject context_obj) {
    jclass cls;
    jfieldID field;
    if (context_obj == NULL) {
        return EGL_NO_CONTEXT;
    }
    cls = (*env)->GetObjectClass(env, context_obj);
    field = get_long_field(env, cls, "mEGLContext");
    return (EGLContext)(uintptr_t)(*env)->GetLongField(env, context_obj, field);
}

static EGLSurface unwrap_surface(JNIEnv *env, jobject surface_obj) {
    jclass cls;
    jfieldID field;
    if (surface_obj == NULL) {
        return EGL_NO_SURFACE;
    }
    cls = (*env)->GetObjectClass(env, surface_obj);
    field = get_long_field(env, cls, "mEGLSurface");
    return (EGLSurface)(uintptr_t)(*env)->GetLongField(env, surface_obj, field);
}

static EGLConfig unwrap_config(JNIEnv *env, jobject config_obj) {
    jclass cls;
    jfieldID field;
    if (config_obj == NULL) {
        return NULL;
    }
    cls = (*env)->GetObjectClass(env, config_obj);
    field = get_long_field(env, cls, "mEGLConfig");
    return (EGLConfig)(uintptr_t)(*env)->GetLongField(env, config_obj, field);
}

static jboolean fill_int_array(JNIEnv *env, jintArray array, const EGLint *values, jint count) {
    if (array == NULL || values == NULL) {
        return JNI_FALSE;
    }
    (*env)->SetIntArrayRegion(env, array, 0, count, values);
    return (*env)->ExceptionCheck(env) ? JNI_FALSE : JNI_TRUE;
}

static EGLint *copy_attrib_list(JNIEnv *env, jintArray attrib_list) {
    EGLint *copy;
    jint *src;
    jsize len;

    if (attrib_list == NULL) {
        return NULL;
    }

    len = (*env)->GetArrayLength(env, attrib_list);
    if (len <= 0) {
        return NULL;
    }

    src = (*env)->GetIntArrayElements(env, attrib_list, NULL);
    if (src == NULL) {
        return NULL;
    }

    copy = calloc((size_t)len, sizeof(EGLint));
    if (copy != NULL) {
        for (jsize i = 0; i < len; ++i) {
            copy[i] = (EGLint)src[i];
        }
    }
    (*env)->ReleaseIntArrayElements(env, attrib_list, src, JNI_ABORT);
    return copy;
}

static jobject new_config_wrapper(JNIEnv *env, EGLConfig config) {
    jclass cls = FindClassOrDie(env, "com/google/android/gles_jni/EGLConfigImpl");
    jmethodID ctor;
    if (cls == NULL) {
        return NULL;
    }
    ctor = (*env)->GetMethodID(env, cls, "<init>", "(J)V");
    if (ctor == NULL) {
        return NULL;
    }
    return (*env)->NewObject(env, cls, ctor, (jlong)(uintptr_t)config);
}

static void jni_nativeClassInit(JNIEnv *env, jclass clazz) {
    (void)env;
    (void)clazz;
}

static jboolean jni_eglInitialize(JNIEnv *env, jobject thiz, jobject display_obj, jintArray major_minor) {
    EGLDisplay display = unwrap_display(env, display_obj);
    EGLint major = 0;
    EGLint minor = 0;
    if (display == EGL_NO_DISPLAY) {
        return JNI_FALSE;
    }
    if (!eglInitialize(display, &major, &minor)) {
        return JNI_FALSE;
    }
    if (major_minor != NULL) {
        EGLint values[2] = { major, minor };
        fill_int_array(env, major_minor, values, 2);
    }
    return JNI_TRUE;
}

static jboolean jni_eglQueryContext(JNIEnv *env, jobject thiz, jobject display_obj, jobject context_obj,
                                    jint attribute, jintArray value_out) {
    EGLDisplay display = unwrap_display(env, display_obj);
    EGLContext context = unwrap_context(env, context_obj);
    EGLint value = 0;
    if (!eglQueryContext(display, context, (EGLint)attribute, &value)) {
        return JNI_FALSE;
    }
    if (value_out != NULL) {
        fill_int_array(env, value_out, &value, 1);
    }
    return JNI_TRUE;
}

static jboolean jni_eglQuerySurface(JNIEnv *env, jobject thiz, jobject display_obj, jobject surface_obj,
                                    jint attribute, jintArray value_out) {
    EGLDisplay display = unwrap_display(env, display_obj);
    EGLSurface surface = unwrap_surface(env, surface_obj);
    EGLint value = 0;
    if (!eglQuerySurface(display, surface, (EGLint)attribute, &value)) {
        return JNI_FALSE;
    }
    if (value_out != NULL) {
        fill_int_array(env, value_out, &value, 1);
    }
    return JNI_TRUE;
}

static jboolean jni_eglReleaseThread(JNIEnv *env, jobject thiz) {
    (void)env;
    (void)thiz;
    return eglReleaseThread() ? JNI_TRUE : JNI_FALSE;
}

static jboolean jni_eglChooseConfig(JNIEnv *env, jobject thiz, jobject display_obj, jintArray attrib_list,
                                    jobjectArray configs, jint config_size, jintArray num_config_out) {
    struct nova_egl *active = nova_egl_get_active();
    EGLDisplay display = unwrap_display(env, display_obj);
    EGLint *attrs = copy_attrib_list(env, attrib_list);
    EGLint num_config = 0;
    EGLConfig *native_configs = NULL;
    jboolean ok = JNI_FALSE;

    (void)thiz;

    if (active != NULL && active->display == display && active->config != NULL) {
        num_config = 1;
        if (num_config_out != NULL) {
            fill_int_array(env, num_config_out, &num_config, 1);
        }
        if (configs != NULL && config_size > 0) {
            jobject config_obj = new_config_wrapper(env, active->config);
            if (config_obj == NULL) {
                return JNI_FALSE;
            }
            (*env)->SetObjectArrayElement(env, configs, 0, config_obj);
        }
        free(attrs);
        return JNI_TRUE;
    }

    if (!eglChooseConfig(display, attrs, NULL, 0, &num_config)) {
        free(attrs);
        return JNI_FALSE;
    }

    if (num_config_out != NULL) {
        fill_int_array(env, num_config_out, &num_config, 1);
    }

    if (configs == NULL || config_size <= 0 || num_config <= 0) {
        free(attrs);
        return JNI_TRUE;
    }

    native_configs = calloc((size_t)config_size, sizeof(EGLConfig));
    if (native_configs == NULL) {
        free(attrs);
        return JNI_FALSE;
    }

    if (!eglChooseConfig(display, attrs, native_configs, config_size, &num_config)) {
        free(native_configs);
        free(attrs);
        return JNI_FALSE;
    }

    for (EGLint i = 0; i < num_config && i < config_size; ++i) {
        jobject config_obj = new_config_wrapper(env, native_configs[i]);
        if (config_obj == NULL) {
            free(native_configs);
            free(attrs);
            return JNI_FALSE;
        }
        (*env)->SetObjectArrayElement(env, configs, i, config_obj);
    }

    ok = JNI_TRUE;
    free(native_configs);
    free(attrs);
    return ok;
}

static jboolean jni_eglGetConfigAttrib(JNIEnv *env, jobject thiz, jobject display_obj, jobject config_obj,
                                       jint attribute, jintArray value_out) {
    struct nova_egl *active = nova_egl_get_active();
    EGLDisplay display = unwrap_display(env, display_obj);
    EGLConfig config = unwrap_config(env, config_obj);
    EGLint value = 0;
    (void)thiz;
    if (active != NULL && active->display == display && active->config != NULL) {
        switch ((EGLint)attribute) {
            case EGL_RED_SIZE:
            case EGL_GREEN_SIZE:
            case EGL_BLUE_SIZE:
                value = 8;
                break;
            case EGL_ALPHA_SIZE:
            case EGL_STENCIL_SIZE:
                value = 0;
                break;
            case EGL_DEPTH_SIZE:
                value = 16;
                break;
            case EGL_SURFACE_TYPE:
                value = EGL_WINDOW_BIT;
                break;
            case EGL_RENDERABLE_TYPE:
                value = EGL_OPENGL_ES2_BIT | EGL_OPENGL_ES3_BIT_KHR;
                break;
            case EGL_NONE:
                value = EGL_NONE;
                break;
            default:
                config = active->config;
                if (!eglGetConfigAttrib(display, config, (EGLint)attribute, &value)) {
                    return JNI_FALSE;
                }
                if (value_out != NULL) {
                    fill_int_array(env, value_out, &value, 1);
                }
                return JNI_TRUE;
        }
        if (value_out != NULL) {
            fill_int_array(env, value_out, &value, 1);
        }
        return JNI_TRUE;
    }
    if (!eglGetConfigAttrib(display, config, (EGLint)attribute, &value)) {
        return JNI_FALSE;
    }
    if (value_out != NULL) {
        fill_int_array(env, value_out, &value, 1);
    }
    return JNI_TRUE;
}

static jboolean jni_eglGetConfigs(JNIEnv *env, jobject thiz, jobject display_obj, jobjectArray configs,
                                  jint config_size, jintArray num_config_out) {
    struct nova_egl *active = nova_egl_get_active();
    EGLDisplay display = unwrap_display(env, display_obj);
    EGLint num_config = 0;
    (void)thiz;
    if (active != NULL && active->display == display && active->config != NULL) {
        num_config = 1;
        if (num_config_out != NULL) {
            fill_int_array(env, num_config_out, &num_config, 1);
        }
        if (configs != NULL && config_size > 0) {
            jobject config_obj = new_config_wrapper(env, active->config);
            if (config_obj == NULL) {
                return JNI_FALSE;
            }
            (*env)->SetObjectArrayElement(env, configs, 0, config_obj);
        }
        return JNI_TRUE;
    }
    if (!eglGetConfigs(display, NULL, 0, &num_config)) {
        return JNI_FALSE;
    }
    if (num_config_out != NULL) {
        fill_int_array(env, num_config_out, &num_config, 1);
    }
    (void)configs;
    (void)config_size;
    return JNI_TRUE;
}

static jint jni_eglGetError(JNIEnv *env, jobject thiz) {
    (void)env;
    (void)thiz;
    return (jint)eglGetError();
}

static jboolean jni_eglDestroyContext(JNIEnv *env, jobject thiz, jobject display_obj, jobject context_obj) {
    return eglDestroyContext(unwrap_display(env, display_obj), unwrap_context(env, context_obj))
        ? JNI_TRUE : JNI_FALSE;
}

static jboolean jni_eglDestroySurface(JNIEnv *env, jobject thiz, jobject display_obj, jobject surface_obj) {
    return eglDestroySurface(unwrap_display(env, display_obj), unwrap_surface(env, surface_obj))
        ? JNI_TRUE : JNI_FALSE;
}

static jboolean jni_eglMakeCurrent(JNIEnv *env, jobject thiz, jobject display_obj,
                                   jobject draw_obj, jobject read_obj, jobject context_obj) {
    return eglMakeCurrent(unwrap_display(env, display_obj),
                          unwrap_surface(env, draw_obj),
                          unwrap_surface(env, read_obj),
                          unwrap_context(env, context_obj)) ? JNI_TRUE : JNI_FALSE;
}

static jstring jni_eglQueryString(JNIEnv *env, jobject thiz, jobject display_obj, jint name) {
    const char *value = eglQueryString(unwrap_display(env, display_obj), (EGLint)name);
    if (value == NULL) {
        return NULL;
    }
    return (*env)->NewStringUTF(env, value);
}

static jboolean jni_eglSwapBuffers(JNIEnv *env, jobject thiz, jobject display_obj, jobject surface_obj) {
    return eglSwapBuffers(unwrap_display(env, display_obj), unwrap_surface(env, surface_obj))
        ? JNI_TRUE : JNI_FALSE;
}

static jboolean jni_eglTerminate(JNIEnv *env, jobject thiz, jobject display_obj) {
    return eglTerminate(unwrap_display(env, display_obj)) ? JNI_TRUE : JNI_FALSE;
}

static jboolean jni_eglCopyBuffers(JNIEnv *env, jobject thiz, jobject display_obj, jobject surface_obj, jobject native_pixmap) {
    (void)env;
    (void)thiz;
    (void)display_obj;
    (void)surface_obj;
    (void)native_pixmap;
    return JNI_FALSE;
}

static jboolean jni_eglWaitGL(JNIEnv *env, jobject thiz) {
    (void)env;
    (void)thiz;
    return eglWaitGL() ? JNI_TRUE : JNI_FALSE;
}

static jboolean jni_eglWaitNative(JNIEnv *env, jobject thiz, jint engine, jobject bindTarget) {
    (void)bindTarget;
    return eglWaitNative((EGLint)engine) ? JNI_TRUE : JNI_FALSE;
}

static jint jni_getInitCount(JNIEnv *env, jclass clazz, jobject display_obj) {
    (void)env;
    (void)clazz;
    (void)display_obj;
    return 1;
}

static jlong _eglCreateContext(JNIEnv *env, jobject thiz, jobject display_obj, jobject config_obj,
                               jobject share_context_obj, jintArray attrib_list) {
    struct nova_egl *active = nova_egl_get_active();
    EGLDisplay display = unwrap_display(env, display_obj);
    EGLConfig config = unwrap_config(env, config_obj);
    EGLContext share_context = unwrap_context(env, share_context_obj);
    EGLint *attrs = copy_attrib_list(env, attrib_list);
    (void)thiz;
    if (active != NULL && active->display == display && active->config != NULL) {
        config = active->config;
    }
    EGLContext context = eglCreateContext(display, config, share_context, attrs);
    free(attrs);
    return (jlong)(uintptr_t)context;
}

static jlong _eglCreatePbufferSurface(JNIEnv *env, jobject thiz, jobject display_obj, jobject config_obj,
                                      jintArray attrib_list) {
    EGLDisplay display = unwrap_display(env, display_obj);
    EGLConfig config = unwrap_config(env, config_obj);
    EGLint *attrs = copy_attrib_list(env, attrib_list);
    EGLSurface surface = eglCreatePbufferSurface(display, config, attrs);
    free(attrs);
    return (jlong)(uintptr_t)surface;
}

static void _eglCreatePixmapSurface(JNIEnv *env, jobject thiz, jobject surface_wrapper, jobject display_obj,
                                    jobject config_obj, jobject native_pixmap, jintArray attrib_list) {
    (void)env;
    (void)thiz;
    (void)surface_wrapper;
    (void)display_obj;
    (void)config_obj;
    (void)native_pixmap;
    (void)attrib_list;
}

static jlong _eglCreateWindowSurface(JNIEnv *env, jobject thiz, jobject display_obj, jobject config_obj,
                                     jobject native_window, jintArray attrib_list) {
    struct nova_egl *active = nova_egl_get_active();
    EGLDisplay display = active ? active->display : unwrap_display(env, display_obj);
    EGLConfig config = active && active->config ? active->config : unwrap_config(env, config_obj);
    EGLint *attrs = copy_attrib_list(env, attrib_list);
    EGLSurface surface;
    (void)thiz;
    (void)native_window;
    if (active == NULL || active->wl_window == NULL) {
        free(attrs);
        return 0;
    }
    surface = eglCreateWindowSurface(display, config, (EGLNativeWindowType)active->wl_window, attrs);
    free(attrs);
    return (jlong)(uintptr_t)surface;
}

static jlong _eglCreateWindowSurfaceTexture(JNIEnv *env, jobject thiz, jobject display_obj, jobject config_obj,
                                            jobject native_window, jintArray attrib_list) {
    return _eglCreateWindowSurface(env, thiz, display_obj, config_obj, native_window, attrib_list);
}

static jlong _eglGetDisplay(JNIEnv *env, jobject thiz, jobject native_display) {
    struct nova_egl *active = nova_egl_get_active();
    (void)env;
    (void)thiz;
    (void)native_display;
    if (active != NULL && active->display != EGL_NO_DISPLAY) {
        return (jlong)(uintptr_t)active->display;
    }
    return (jlong)(uintptr_t)eglGetDisplay(EGL_DEFAULT_DISPLAY);
}

static jlong _eglGetCurrentContext(JNIEnv *env, jobject thiz) {
    (void)env;
    (void)thiz;
    return (jlong)(uintptr_t)eglGetCurrentContext();
}

static jlong _eglGetCurrentDisplay(JNIEnv *env, jobject thiz) {
    (void)env;
    (void)thiz;
    return (jlong)(uintptr_t)eglGetCurrentDisplay();
}

static jlong _eglGetCurrentSurface(JNIEnv *env, jobject thiz, jint readdraw) {
    (void)env;
    (void)thiz;
    return (jlong)(uintptr_t)eglGetCurrentSurface((EGLint)readdraw);
}

static const JNINativeMethod gEglMethods[] = {
    { "_nativeClassInit", "()V", (void *)jni_nativeClassInit },
    { "eglInitialize", "(Ljavax/microedition/khronos/egl/EGLDisplay;[I)Z", (void *)jni_eglInitialize },
    { "eglQueryContext", "(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLContext;I[I)Z", (void *)jni_eglQueryContext },
    { "eglQuerySurface", "(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLSurface;I[I)Z", (void *)jni_eglQuerySurface },
    { "eglReleaseThread", "()Z", (void *)jni_eglReleaseThread },
    { "eglChooseConfig", "(Ljavax/microedition/khronos/egl/EGLDisplay;[I[Ljavax/microedition/khronos/egl/EGLConfig;I[I)Z", (void *)jni_eglChooseConfig },
    { "eglGetConfigAttrib", "(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLConfig;I[I)Z", (void *)jni_eglGetConfigAttrib },
    { "eglGetConfigs", "(Ljavax/microedition/khronos/egl/EGLDisplay;[Ljavax/microedition/khronos/egl/EGLConfig;I[I)Z", (void *)jni_eglGetConfigs },
    { "eglGetError", "()I", (void *)jni_eglGetError },
    { "eglDestroyContext", "(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLContext;)Z", (void *)jni_eglDestroyContext },
    { "eglDestroySurface", "(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLSurface;)Z", (void *)jni_eglDestroySurface },
    { "eglMakeCurrent", "(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLSurface;Ljavax/microedition/khronos/egl/EGLSurface;Ljavax/microedition/khronos/egl/EGLContext;)Z", (void *)jni_eglMakeCurrent },
    { "eglQueryString", "(Ljavax/microedition/khronos/egl/EGLDisplay;I)Ljava/lang/String;", (void *)jni_eglQueryString },
    { "eglSwapBuffers", "(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLSurface;)Z", (void *)jni_eglSwapBuffers },
    { "eglTerminate", "(Ljavax/microedition/khronos/egl/EGLDisplay;)Z", (void *)jni_eglTerminate },
    { "eglCopyBuffers", "(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLSurface;Ljava/lang/Object;)Z", (void *)jni_eglCopyBuffers },
    { "eglWaitGL", "()Z", (void *)jni_eglWaitGL },
    { "eglWaitNative", "(ILjava/lang/Object;)Z", (void *)jni_eglWaitNative },
    { "getInitCount", "(Ljavax/microedition/khronos/egl/EGLDisplay;)I", (void *)jni_getInitCount },
    { "_eglCreateContext", "(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLConfig;Ljavax/microedition/khronos/egl/EGLContext;[I)J", (void *)_eglCreateContext },
    { "_eglCreatePbufferSurface", "(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLConfig;[I)J", (void *)_eglCreatePbufferSurface },
    { "_eglCreatePixmapSurface", "(Ljavax/microedition/khronos/egl/EGLSurface;Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLConfig;Ljava/lang/Object;[I)V", (void *)_eglCreatePixmapSurface },
    { "_eglCreateWindowSurface", "(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLConfig;Ljava/lang/Object;[I)J", (void *)_eglCreateWindowSurface },
    { "_eglCreateWindowSurfaceTexture", "(Ljavax/microedition/khronos/egl/EGLDisplay;Ljavax/microedition/khronos/egl/EGLConfig;Ljava/lang/Object;[I)J", (void *)_eglCreateWindowSurfaceTexture },
    { "_eglGetDisplay", "(Ljava/lang/Object;)J", (void *)_eglGetDisplay },
    { "_eglGetCurrentContext", "()J", (void *)_eglGetCurrentContext },
    { "_eglGetCurrentDisplay", "()J", (void *)_eglGetCurrentDisplay },
    { "_eglGetCurrentSurface", "(I)J", (void *)_eglGetCurrentSurface },
};

int register_com_google_android_gles_jni_EGLImpl(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "com/google/android/gles_jni/EGLImpl",
                                gEglMethods, sizeof(gEglMethods) / sizeof(gEglMethods[0]));
}
