#include "core_jni_helpers.h"

/* android.view.KeyEvent — stubs */
static void native_updateMetaState(JNIEnv *env, jclass, jint keyCode, jboolean isDown, jint metaState) {
    log_unimplemented_jni("android.view.KeyEvent.native_updateMetaState");
    (void)keyCode; (void)isDown; (void)metaState;
}

static jint native_getKeyCharacterTable(JNIEnv *env, jclass, jint deviceId, jint keyCode) {
    log_unimplemented_jni("android.view.KeyEvent.native_getKeyCharacterTable");
    (void)deviceId; (void)keyCode;
    return 0;
}

static const JNINativeMethod gMethods[] = {
    { "native_updateMetaState",    "(IZI)I", (void*)native_updateMetaState },
};

int register_android_view_KeyEvent(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/view/KeyEvent",
                                 gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
