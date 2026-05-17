#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* Logging */
void log_unimplemented_jni(const char *method) {
    fprintf(stderr, "[NovaART] Unimplemented JNI stub: %s\n", method);
}

/* Registration helper — wraps JNI RegisterNatives */
int RegisterMethodsOrDie(JNIEnv *env, const char *className,
                          const JNINativeMethod *methods, int numMethods) {
    jclass clazz = (*env)->FindClass(env, className);
    if (!clazz) {
        fprintf(stderr, "[NovaART] Failed to find class: %s\n", className);
        return -1;
    }
    int ret = (*env)->RegisterNatives(env, clazz, methods, numMethods);
    if (ret < 0) {
        fprintf(stderr, "[NovaART] Failed to register natives for: %s\n", className);
    }
    return ret;
}

/* FindClass helper */
jclass FindClassOrDie(JNIEnv *env, const char *className) {
    jclass clazz = (*env)->FindClass(env, className);
    if (!clazz) {
        fprintf(stderr, "[NovaART] FindClassOrDie failed: %s\n", className);
    }
    return clazz;
}

/* Default return stubs */
jboolean return_default_boolean(void) {
    return JNI_FALSE;
}

jint return_default_int(void) {
    return 0;
}

jfloat return_default_float(void) {
    return 0.0f;
}

jdouble return_default_double(void) {
    return 0.0;
}

jobject return_null_object(void) {
    return NULL;
}

jlong return_zero_handle(void) {
    /* For native handles: return 0 (caller handles NPE) */
    return 0;
}
