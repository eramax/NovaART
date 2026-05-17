#include <stdio.h>
#include <stdlib.h>
#include <dlfcn.h>
#include "nova.h"

typedef jint (*JNI_CreateJavaVM_t)(JavaVM **pvm, void **penv, JavaVMInitArgs *args);

int nova_art_init(struct nova_state *state, int argc, char *argv[]) {
    (void)argc;
    (void)argv;
    state->libart_handle = NULL;
    state->jvm = NULL;
    state->env = NULL;

    /* Attempt to load libart.so */
    state->libart_handle = dlopen("libart.so", RTLD_NOW | RTLD_GLOBAL);
    if (!state->libart_handle) {
        fprintf(stderr, "Warning: libart.so not found: %s\n", dlerror());
        fprintf(stderr, "Set LD_LIBRARY_PATH to point to ART build output\n");
        /* Non-fatal for now — skeleton works without ART */
        return 0;
    }

    JNI_CreateJavaVM_t JNI_CreateJavaVM = dlsym(state->libart_handle, "JNI_CreateJavaVM");
    if (!JNI_CreateJavaVM) {
        fprintf(stderr, "Warning: JNI_CreateJavaVM not found in libart.so: %s\n", dlerror());
        return 0;
    }

    JavaVMInitArgs args;
    args.version = JNI_VERSION_1_6;
    args.nOptions = 0;
    args.options = NULL;
    args.ignoreUnrecognized = JNI_TRUE;

    jint ret = JNI_CreateJavaVM(&state->jvm, (void**)&state->env, &args);
    if (ret < 0) {
        fprintf(stderr, "Warning: JNI_CreateJavaVM failed: %d\n", ret);
        return 0;
    }

    printf("ART runtime initialized (JavaVM: %p, JNIEnv: %p)\n",
           (void*)state->jvm, (void*)state->env);

    /* Register JNI stubs */
    if (state->env) {
        register_all_jni_stubs(state->env);
    }

    return 0;
}

void nova_art_shutdown(struct nova_state *state) {
    if (state->jvm) {
        (*state->jvm)->DestroyJavaVM(state->jvm);
        state->jvm = NULL;
        state->env = NULL;
    }
    if (state->libart_handle) {
        dlclose(state->libart_handle);
        state->libart_handle = NULL;
    }
}

jclass nova_art_find_class(struct nova_state *state, const char *name) {
    if (!state->env) return NULL;
    return (*state->env)->FindClass(state->env, name);
}

jmethodID nova_art_get_static_method(struct nova_state *state, jclass cls,
                                      const char *name, const char *sig) {
    if (!state->env) return NULL;
    return (*state->env)->GetStaticMethodID(state->env, cls, name, sig);
}

jmethodID nova_art_get_method(struct nova_state *state, jclass cls,
                               const char *name, const char *sig) {
    if (!state->env) return NULL;
    return (*state->env)->GetMethodID(state->env, cls, name, sig);
}
