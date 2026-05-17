#ifndef CORE_JNI_HELPERS_H
#define CORE_JNI_HELPERS_H

#include <jni.h>

/* Log warning for unimplemented JNI */
void log_unimplemented_jni(const char *method);

/* Safe default return helpers */
jboolean return_default_boolean(void);
jint return_default_int(void);
jfloat return_default_float(void);
jdouble return_default_double(void);
jobject return_null_object(void);
jlong return_zero_handle(void);

/* Registration helper — wraps JNI RegisterNatives */
int RegisterMethodsOrDie(JNIEnv *env, const char *className,
                          const JNINativeMethod *methods, int numMethods);

/* Find class helper */
jclass FindClassOrDie(JNIEnv *env, const char *className);

/* Module registration function type */
typedef int (*RegJNIProc)(JNIEnv *env);

#endif /* CORE_JNI_HELPERS_H */
