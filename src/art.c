#define _GNU_SOURCE
#define _POSIX_C_SOURCE 200809L

#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <unistd.h>
#include <dlfcn.h>
#include "nova.h"

typedef jint (*JNI_CreateJavaVM_t)(JavaVM **pvm, void **penv, JavaVMInitArgs *args);

#ifndef PATH_MAX
#define PATH_MAX 4096
#endif

static int mkdir_p(const char *path) {
    char buf[PATH_MAX];
    size_t len;

    if (!path) return -1;
    len = strlen(path);
    if (len == 0 || len >= sizeof(buf)) return -1;

    memcpy(buf, path, len + 1);
    for (char *p = buf + 1; *p != '\0'; ++p) {
        if (*p == '/') {
            *p = '\0';
            if (mkdir(buf, 0777) != 0 && errno != EEXIST) {
                return -1;
            }
            *p = '/';
        }
    }
    if (mkdir(buf, 0777) != 0 && errno != EEXIST) {
        return -1;
    }
    return 0;
}

static int file_exists(const char *path) {
    return path != NULL && access(path, F_OK) == 0;
}

static void dirname_inplace(char *path) {
    char *slash = strrchr(path, '/');
    if (slash == NULL) {
        strcpy(path, ".");
        return;
    }
    if (slash == path) {
        slash[1] = '\0';
        return;
    }
    *slash = '\0';
}

static void build_project_root(char *out, size_t out_size) {
    ssize_t len;

    if (out_size == 0) {
        return;
    }

    len = readlink("/proc/self/exe", out, out_size - 1);
    if (len <= 0 || (size_t)len >= out_size - 1) {
        strncpy(out, ".", out_size - 1);
        out[out_size - 1] = '\0';
        return;
    }
    out[len] = '\0';
    dirname_inplace(out);
    dirname_inplace(out);
    dirname_inplace(out);
}

static void set_env_default(const char *key, const char *value) {
    const char *existing = getenv(key);
    if (existing == NULL || existing[0] == '\0') {
        setenv(key, value, 1);
    }
}

static int append_option(JavaVMOption *options, int *count, const char *text) {
    char *copy = strdup(text);
    if (copy == NULL) {
        return -1;
    }
    options[*count].optionString = copy;
    options[*count].extraInfo = NULL;
    (*count)++;
    return 0;
}

int nova_art_init(struct nova_state *state, int argc, char *argv[]) {
    (void)argc;
    (void)argv;
    char project_root[PATH_MAX];
    char output_root[PATH_MAX];
    char android_root[PATH_MAX];
    char android_art_root[PATH_MAX];
    char android_data[PATH_MAX];
    char framework_jar[PATH_MAX];
    char image_path[PATH_MAX];
    char bootclasspath[PATH_MAX * 2];
    char bootclasspath_locations[1024];
    JavaVMOption options[8];
    int option_count = 0;
    int i;

    state->libart_handle = NULL;
    state->jvm = NULL;
    state->env = NULL;

    build_project_root(project_root, sizeof(project_root));
    snprintf(output_root, sizeof(output_root), "%s/output", project_root);
    snprintf(android_root, sizeof(android_root), "%s/android-root", output_root);
    snprintf(android_art_root, sizeof(android_art_root), "%s/com.android.art", android_root);
    snprintf(android_data, sizeof(android_data), "%s/android-data", output_root);
    snprintf(framework_jar, sizeof(framework_jar), "%s/out/framework/nova-framework-dex.jar", project_root);

    set_env_default("ANDROID_ROOT", android_root);
    set_env_default("ANDROID_ART_ROOT", android_art_root);
    set_env_default("ANDROID_DATA", android_data);

    if (mkdir_p(getenv("ANDROID_DATA")) != 0) {
        fprintf(stderr, "Failed to create ANDROID_DATA at %s\n", getenv("ANDROID_DATA"));
        return -1;
    }

    /* Attempt to load libart.so */
    state->libart_handle = dlopen("libart.so", RTLD_NOW | RTLD_GLOBAL);
    if (!state->libart_handle) {
        fprintf(stderr, "libart.so not found: %s\n", dlerror());
        fprintf(stderr, "Set LD_LIBRARY_PATH to point to ART build output\n");
        return -1;
    }

    JNI_CreateJavaVM_t JNI_CreateJavaVM = dlsym(state->libart_handle, "JNI_CreateJavaVM");
    if (!JNI_CreateJavaVM) {
        fprintf(stderr, "JNI_CreateJavaVM not found in libart.so: %s\n", dlerror());
        dlclose(state->libart_handle);
        state->libart_handle = NULL;
        return -1;
    }

    snprintf(bootclasspath, sizeof(bootclasspath),
             "%s/apex/com.android.art/javalib/core-oj.jar:"
             "%s/apex/com.android.art/javalib/core-libart.jar:"
             "%s/apex/com.android.art/javalib/okhttp.jar:"
             "%s/apex/com.android.art/javalib/bouncycastle.jar:"
             "%s/apex/com.android.art/javalib/apache-xml.jar",
             getenv("ANDROID_ROOT"),
             getenv("ANDROID_ROOT"),
             getenv("ANDROID_ROOT"),
             getenv("ANDROID_ROOT"),
             getenv("ANDROID_ROOT"));
    snprintf(bootclasspath_locations, sizeof(bootclasspath_locations),
             "/apex/com.android.art/javalib/core-oj.jar:"
             "/apex/com.android.art/javalib/core-libart.jar:"
             "/apex/com.android.art/javalib/okhttp.jar:"
             "/apex/com.android.art/javalib/bouncycastle.jar:"
             "/apex/com.android.art/javalib/apache-xml.jar");

    snprintf(image_path, sizeof(image_path), "%s/framework/boot.art", getenv("ANDROID_ART_ROOT"));
    if (!file_exists(image_path)) {
        snprintf(image_path, sizeof(image_path), "/non/existent/novaart.art");
    }

    if (append_option(options, &option_count, "-Xcompiler-option --compiler-filter=verify") != 0) {
        fprintf(stderr, "Failed to allocate JVM option\n");
        return -1;
    }
    if (append_option(options, &option_count, "-Xmx256m") != 0) {
        fprintf(stderr, "Failed to allocate JVM option\n");
        return -1;
    }
    {
        char arg[PATH_MAX * 2];
        snprintf(arg, sizeof(arg), "-Ximage:%s", image_path);
        if (append_option(options, &option_count, arg) != 0) {
            fprintf(stderr, "Failed to allocate JVM option\n");
            return -1;
        }
        snprintf(arg, sizeof(arg), "-Xbootclasspath:%s", bootclasspath);
        if (append_option(options, &option_count, arg) != 0) {
            fprintf(stderr, "Failed to allocate JVM option\n");
            return -1;
        }
        snprintf(arg, sizeof(arg), "-Xbootclasspath-locations:%s", bootclasspath_locations);
        if (append_option(options, &option_count, arg) != 0) {
            fprintf(stderr, "Failed to allocate JVM option\n");
            return -1;
        }
        if (file_exists(framework_jar)) {
            snprintf(arg, sizeof(arg), "-Djava.class.path=%s", framework_jar);
            if (append_option(options, &option_count, arg) != 0) {
                fprintf(stderr, "Failed to allocate JVM option\n");
                return -1;
            }
        }
    }

    JavaVMInitArgs args;
    args.version = JNI_VERSION_1_6;
    args.nOptions = option_count;
    args.options = options;
    args.ignoreUnrecognized = JNI_TRUE;

    jint ret = JNI_CreateJavaVM(&state->jvm, (void**)&state->env, &args);
    for (i = 0; i < option_count; ++i) {
        free(options[i].optionString);
        options[i].optionString = NULL;
    }
    if (ret < 0) {
        fprintf(stderr, "JNI_CreateJavaVM failed: %d\n", ret);
        dlclose(state->libart_handle);
        state->libart_handle = NULL;
        state->jvm = NULL;
        state->env = NULL;
        return -1;
    }

    printf("ART runtime initialized (JavaVM: %p, JNIEnv: %p)\n",
           (void*)state->jvm, (void*)state->env);
    printf("  ANDROID_ROOT=%s\n", getenv("ANDROID_ROOT"));
    printf("  ANDROID_ART_ROOT=%s\n", getenv("ANDROID_ART_ROOT"));
    printf("  ANDROID_DATA=%s\n", getenv("ANDROID_DATA"));

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
