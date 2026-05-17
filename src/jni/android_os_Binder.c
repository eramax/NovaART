#include "core_jni_helpers.h"

/* android.os.Binder — stubs */
static void blockUntilThreadAvailable(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.Binder.blockUntilThreadAvailable");
}

static jlong getNativeBBinderHolder(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.Binder.getNativeBBinderHolder");
    return return_zero_handle();
}

static jlong getNativeFinalizer(JNIEnv *env, jclass) {
    log_unimplemented_jni("android.os.Binder.getNativeFinalizer");
    return return_zero_handle();
}

static void destroy(JNIEnv *env, jobject, jlong holder) {
    log_unimplemented_jni("android.os.Binder.destroy");
    (void)holder;
}

static const JNINativeMethod gMethods[] = {
    { "blockUntilThreadAvailable", "()V",                                    (void*)blockUntilThreadAvailable },
    { "getNativeBBinderHolder",    "()J",                                    (void*)getNativeBBinderHolder },
    { "getNativeFinalizer",        "()J",                                    (void*)getNativeFinalizer },
    { "destroy",                   "(J)V",                                   (void*)destroy },
};

int register_android_os_Binder(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/os/Binder",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
