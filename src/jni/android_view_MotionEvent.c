#include "core_jni_helpers.h"

/* android.view.MotionEvent — stubs */
static void native_classifyMotionEvent(JNIEnv *env, jclass, jobject event) {
    log_unimplemented_jni("android.view.MotionEvent.native_classifyMotionEvent");
    (void)event;
}

static const JNINativeMethod gMethods[] = {
    { "native_classifyMotionEvent", "(Landroid/view/MotionEvent;)V", (void*)native_classifyMotionEvent },
};

int register_android_view_MotionEvent(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/view/MotionEvent",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
