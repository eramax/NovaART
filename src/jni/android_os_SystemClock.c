#include "core_jni_helpers.h"

/* android.os.SystemClock — stubs */
static jlong now(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.SystemClock.now");
    return 0;
}

static jlong uptimeMillis(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.SystemClock.uptimeMillis");
    return 0;
}

static jlong elapsedRealtime(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.SystemClock.elapsedRealtime");
    return 0;
}

static jlong elapsedRealtimeNanos(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.SystemClock.elapsedRealtimeNanos");
    return 0;
}

static jlong currentThreadTimeMillis(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.SystemClock.currentThreadTimeMillis");
    return 0;
}

static jlong currentThreadTimeMicro(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.SystemClock.currentThreadTimeMicro");
    return 0;
}

static jlong currentTimeMicro(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.SystemClock.currentTimeMicro");
    return 0;
}

static const JNINativeMethod gMethods[] = {
    { "now",                     "()J", (void*)now },
    { "uptimeMillis",            "()J", (void*)uptimeMillis },
    { "elapsedRealtime",         "()J", (void*)elapsedRealtime },
    { "elapsedRealtimeNanos",    "()J", (void*)elapsedRealtimeNanos },
    { "currentThreadTimeMillis", "()J", (void*)currentThreadTimeMillis },
    { "currentThreadTimeMicro",  "()J", (void*)currentThreadTimeMicro },
    { "currentTimeMicro",        "()J", (void*)currentTimeMicro },
};

int register_android_os_SystemClock(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/os/SystemClock",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
