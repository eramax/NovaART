#include "core_jni_helpers.h"

/* android.os.Process — stubs */
static void setArgV0(JNIEnv *env, jclass, jstring name) {
    log_unimplemented_jni("android.os.Process.setArgV0");
    (void)name;
}

static void setProcessGroup(JNIEnv *env, jclass, jint pid, jint group) {
    log_unimplemented_jni("android.os.Process.setProcessGroup");
    (void)pid; (void)group;
}

static void setThreadPriority(JNIEnv *env, jclass, jint tid, jint priority) {
    log_unimplemented_jni("android.os.Process.setThreadPriority");
    (void)tid; (void)priority;
}

static jint myPid(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.Process.myPid");
    return 1;
}

static jint myUid(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.Process.myUid");
    return 1000;
}

static jint getUidForName(JNIEnv *env, jclass, jstring name) {
    log_unimplemented_jni("android.os.Process.getUidForName");
    (void)name;
    return 1000;
}

static const JNINativeMethod gMethods[] = {
    { "setArgV0",            "(Ljava/lang/String;)V", (void*)setArgV0 },
    { "setProcessGroup",     "(II)V",                  (void*)setProcessGroup },
    { "setThreadPriority",   "(II)V",                  (void*)setThreadPriority },
    { "myPid",               "()I",                    (void*)myPid },
    { "myUid",               "()I",                    (void*)myUid },
    { "getUidForName",       "(Ljava/lang/String;)I",  (void*)getUidForName },
};

int register_android_os_Process(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/os/Process",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
