#include "core_jni_helpers.h"
#include <string.h>
#include <stdio.h>

/* android.os.SystemProperties — stubs */
static jstring native_get(JNIEnv *env, jclass, jstring key, jstring def) {
    log_unimplemented_jni("android.os.SystemProperties.native_get");
    return def;
}

static jstring native_getString(JNIEnv *env, jclass, jstring key, jstring def) {
    log_unimplemented_jni("android.os.SystemProperties.native_getString");
    return def;
}

static jint native_getInt(JNIEnv *env, jclass, jstring key, jint def) {
    log_unimplemented_jni("android.os.SystemProperties.native_getInt");
    return def;
}

static jlong native_getLong(JNIEnv *env, jclass, jstring key, jlong def) {
    log_unimplemented_jni("android.os.SystemProperties.native_getLong");
    return def;
}

static jboolean native_getBoolean(JNIEnv *env, jclass, jstring key, jboolean def) {
    log_unimplemented_jni("android.os.SystemProperties.native_getBoolean");
    return def;
}

static void native_set(JNIEnv *env, jclass, jstring key, jstring val) {
    log_unimplemented_jni("android.os.SystemProperties.native_set");
}

static void native_addChangeCallback(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.SystemProperties.native_addChangeCallback");
}

static const JNINativeMethod gMethods[] = {
    { "native_get",              "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void*)native_get },
    { "native_getString",        "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void*)native_getString },
    { "native_getInt",           "(Ljava/lang/String;I)I",                                    (void*)native_getInt },
    { "native_getLong",          "(Ljava/lang/String;J)J",                                    (void*)native_getLong },
    { "native_getBoolean",       "(Ljava/lang/String;Z)Z",                                    (void*)native_getBoolean },
    { "native_set",              "(Ljava/lang/String;Ljava/lang/String;)V",                   (void*)native_set },
    { "native_addChangeCallback", "()V",                                                      (void*)native_addChangeCallback },
};

int register_android_os_SystemProperties(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/os/SystemProperties",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
