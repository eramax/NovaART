#define _GNU_SOURCE
#define _POSIX_C_SOURCE 200809L

#include <errno.h>
#include <fcntl.h>
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

static const char *kGlesV2Candidates[] = {
    "/lib/x86_64-linux-gnu/libGLESv2.so.2",
    "/lib/x86_64-linux-gnu/libGLESv2.so",
    "/usr/lib/x86_64-linux-gnu/libGLESv2.so.2",
    "/usr/lib/x86_64-linux-gnu/libGLESv2.so",
    "/lib64/libGLESv2.so.2",
    "/lib64/libGLESv2.so",
    "/usr/lib64/libGLESv2.so.2",
    "/usr/lib64/libGLESv2.so",
};

struct compat_library_entry {
    const char *name;
    const char *path;
};

static const struct compat_library_entry kHostCompatLibraries[] = {
    { "libandroid.so", NULL },
    { "liblog.so", NULL },
    { "libc.so", "/lib/x86_64-linux-gnu/libc.so.6" },
    { "libm.so", "/lib/x86_64-linux-gnu/libm.so.6" },
    { "libdl.so", "/lib/x86_64-linux-gnu/libdl.so.2" },
};

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

static int copy_file(const char *src, const char *dst) {
    int in_fd = -1;
    int out_fd = -1;
    char buffer[16384];
    ssize_t read_count;

    in_fd = open(src, O_RDONLY);
    if (in_fd < 0) {
        return -1;
    }

    out_fd = open(dst, O_WRONLY | O_CREAT | O_TRUNC, 0666);
    if (out_fd < 0) {
        close(in_fd);
        return -1;
    }

    while ((read_count = read(in_fd, buffer, sizeof(buffer))) > 0) {
        char *cursor = buffer;
        ssize_t remaining = read_count;
        while (remaining > 0) {
            ssize_t written = write(out_fd, cursor, (size_t)remaining);
            if (written < 0) {
                close(in_fd);
                close(out_fd);
                return -1;
            }
            cursor += written;
            remaining -= written;
        }
    }

    close(in_fd);
    if (close(out_fd) != 0) {
        return -1;
    }
    return read_count < 0 ? -1 : 0;
}

static int jni_log_and_clear_exception(JNIEnv *env, const char *context) {
    if (!(*env)->ExceptionCheck(env)) {
        return 0;
    }

    fprintf(stderr, "[NovaART] Java exception during %s\n", context);
    (*env)->ExceptionDescribe(env);
    (*env)->ExceptionClear(env);
    return -1;
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

static int detect_launchable_activity(const char *project_root, const char *apk_path,
                                      char *out, size_t out_size) {
    char aapt2_path[PATH_MAX];
    char command[PATH_MAX * 2];
    char line[1024];
    FILE *fp;

    snprintf(aapt2_path, sizeof(aapt2_path),
             "%s/deps/aosp-full/prebuilts/sdk/tools/linux/bin/aapt2",
             project_root);
    if (!file_exists(aapt2_path)) {
        fprintf(stderr, "[NovaART] aapt2 not found at %s\n", aapt2_path);
        return -1;
    }

    snprintf(command, sizeof(command), "\"%s\" dump badging \"%s\" 2>/dev/null",
             aapt2_path, apk_path);
    fp = popen(command, "r");
    if (fp == NULL) {
        fprintf(stderr, "[NovaART] Failed to run aapt2 for %s\n", apk_path);
        return -1;
    }

    while (fgets(line, sizeof(line), fp) != NULL) {
        char *start;
        char *end;

        if (strncmp(line, "launchable-activity:", 20) != 0) {
            continue;
        }

        start = strstr(line, "name='");
        if (start == NULL) {
            continue;
        }
        start += 6;
        end = strchr(start, '\'');
        if (end == NULL) {
            continue;
        }

        if ((size_t)(end - start) >= out_size) {
            pclose(fp);
            fprintf(stderr, "[NovaART] Launchable activity name too long\n");
            return -1;
        }

        memcpy(out, start, (size_t)(end - start));
        out[end - start] = '\0';
        pclose(fp);
        return 0;
    }

    pclose(fp);
    fprintf(stderr, "[NovaART] No launchable activity found in %s\n", apk_path);
    return -1;
}

static int detect_package_name(const char *project_root, const char *apk_path,
                               char *out, size_t out_size) {
    char aapt2_path[PATH_MAX];
    char command[PATH_MAX * 2];
    char line[1024];
    FILE *fp;

    snprintf(aapt2_path, sizeof(aapt2_path),
             "%s/deps/aosp-full/prebuilts/sdk/tools/linux/bin/aapt2",
             project_root);
    if (!file_exists(aapt2_path)) {
        fprintf(stderr, "[NovaART] aapt2 not found at %s\n", aapt2_path);
        return -1;
    }

    snprintf(command, sizeof(command), "\"%s\" dump badging \"%s\" 2>/dev/null",
             aapt2_path, apk_path);
    fp = popen(command, "r");
    if (fp == NULL) {
        fprintf(stderr, "[NovaART] Failed to run aapt2 for %s\n", apk_path);
        return -1;
    }

    while (fgets(line, sizeof(line), fp) != NULL) {
        char *start;
        char *end;

        if (strncmp(line, "package:", 8) != 0) {
            continue;
        }

        start = strstr(line, "name='");
        if (start == NULL) {
            continue;
        }
        start += 6;
        end = strchr(start, '\'');
        if (end == NULL) {
            continue;
        }
        if ((size_t)(end - start) >= out_size) {
            pclose(fp);
            fprintf(stderr, "[NovaART] Package name too long\n");
            return -1;
        }

        memcpy(out, start, (size_t)(end - start));
        out[end - start] = '\0';
        pclose(fp);
        return 0;
    }

    pclose(fp);
    fprintf(stderr, "[NovaART] No package name found in %s\n", apk_path);
    return -1;
}

static void set_env_default(const char *key, const char *value) {
    const char *existing = getenv(key);
    if (existing == NULL || existing[0] == '\0') {
        setenv(key, value, 1);
    }
}

static int prepend_env_path(const char *key, const char *value) {
    const char *existing;
    char joined[PATH_MAX * 2];

    if (value == NULL || value[0] == '\0') {
        return 0;
    }

    existing = getenv(key);
    if (existing == NULL || existing[0] == '\0') {
        return setenv(key, value, 1);
    }

    if (strstr(existing, value) != NULL) {
        return 0;
    }

    if (snprintf(joined, sizeof(joined), "%s:%s", value, existing) >= (int)sizeof(joined)) {
        errno = ENAMETOOLONG;
        return -1;
    }

    return setenv(key, joined, 1);
}

static const char *find_system_gles_library(void) {
    size_t i;
    for (i = 0; i < sizeof(kGlesV2Candidates) / sizeof(kGlesV2Candidates[0]); ++i) {
        if (file_exists(kGlesV2Candidates[i])) {
            return kGlesV2Candidates[i];
        }
    }
    return NULL;
}

static int ensure_host_gles_compat_library(const char *native_lib_root) {
    char compat_path[PATH_MAX];
    const char *system_lib;

    if (snprintf(compat_path, sizeof(compat_path), "%s/libGLESv3.so", native_lib_root) >= (int)sizeof(compat_path)) {
        errno = ENAMETOOLONG;
        return -1;
    }
    if (file_exists(compat_path)) {
        return 0;
    }

    system_lib = find_system_gles_library();
    if (system_lib == NULL) {
        fprintf(stderr, "[NovaART] WARNING: no host libGLESv2 candidate found for libGLESv3.so\n");
        return 0;
    }

    unlink(compat_path);
    if (symlink(system_lib, compat_path) == 0) {
        printf("[NovaART] Linked %s -> %s\n", compat_path, system_lib);
        return 0;
    }
    if (copy_file(system_lib, compat_path) == 0) {
        printf("[NovaART] Copied %s from %s\n", compat_path, system_lib);
        return 0;
    }

    fprintf(stderr, "[NovaART] Failed to stage %s from %s: %s\n",
            compat_path, system_lib, strerror(errno));
    return -1;
}

static int ensure_host_compat_library(const char *native_lib_root,
                                      const char *name,
                                      const char *source_path) {
    char compat_path[PATH_MAX];

    if (snprintf(compat_path, sizeof(compat_path), "%s/%s", native_lib_root, name) >= (int)sizeof(compat_path)) {
        errno = ENAMETOOLONG;
        return -1;
    }
    if (file_exists(compat_path)) {
        return 0;
    }

    if (source_path == NULL || !file_exists(source_path)) {
        return 0;
    }

    unlink(compat_path);
    if (symlink(source_path, compat_path) == 0) {
        printf("[NovaART] Linked %s -> %s\n", compat_path, source_path);
        return 0;
    }
    if (copy_file(source_path, compat_path) == 0) {
        printf("[NovaART] Copied %s from %s\n", compat_path, source_path);
        return 0;
    }

    fprintf(stderr, "[NovaART] Failed to stage %s from %s: %s\n",
            compat_path, source_path, strerror(errno));
    return -1;
}

static int ensure_host_runtime_compat_libraries(const char *output_root,
                                                const char *native_lib_root) {
    char host_lib_dir[PATH_MAX];
    char source_path[PATH_MAX];
    size_t i;

    if (snprintf(host_lib_dir, sizeof(host_lib_dir), "%s/lib", output_root) >= (int)sizeof(host_lib_dir)) {
        errno = ENAMETOOLONG;
        return -1;
    }

    for (i = 0; i < sizeof(kHostCompatLibraries) / sizeof(kHostCompatLibraries[0]); ++i) {
        const char *source = kHostCompatLibraries[i].path;
        if (source == NULL) {
            if (snprintf(source_path, sizeof(source_path), "%s/%s", host_lib_dir, kHostCompatLibraries[i].name)
                >= (int)sizeof(source_path)) {
                errno = ENAMETOOLONG;
                return -1;
            }
            source = source_path;
        }
        if (ensure_host_compat_library(native_lib_root, kHostCompatLibraries[i].name, source) != 0) {
            return -1;
        }
    }

    return 0;
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
    char android_i18n_root[PATH_MAX];
    char android_tzdata_root[PATH_MAX];
    char android_data[PATH_MAX];
    char native_lib_root[PATH_MAX];
    char framework_jar[PATH_MAX];
    char image_path[PATH_MAX];
    char bootclasspath[PATH_MAX * 3];
    char bootclasspath_locations[1536];
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
    snprintf(android_i18n_root, sizeof(android_i18n_root), "%s/com.android.i18n", android_root);
    snprintf(android_tzdata_root, sizeof(android_tzdata_root), "%s/com.android.tzdata", android_root);
    snprintf(android_data, sizeof(android_data), "%s/android-data", output_root);
    snprintf(native_lib_root, sizeof(native_lib_root), "%s/dex/native-libs", android_data);
    snprintf(framework_jar, sizeof(framework_jar), "%s/out/framework/nova-framework-dex.jar", project_root);

    set_env_default("ANDROID_ROOT", android_root);
    set_env_default("ANDROID_ART_ROOT", android_art_root);
    set_env_default("ANDROID_I18N_ROOT", android_i18n_root);
    set_env_default("ANDROID_TZDATA_ROOT", android_tzdata_root);
    set_env_default("ANDROID_DATA", android_data);

    if (mkdir_p(getenv("ANDROID_DATA")) != 0) {
        fprintf(stderr, "Failed to create ANDROID_DATA at %s\n", getenv("ANDROID_DATA"));
        return -1;
    }
    if (mkdir_p(native_lib_root) != 0) {
        fprintf(stderr, "Failed to create native library dir at %s\n", native_lib_root);
        return -1;
    }
    if (prepend_env_path("LD_LIBRARY_PATH", native_lib_root) != 0) {
        fprintf(stderr, "Failed to update LD_LIBRARY_PATH with %s\n", native_lib_root);
        return -1;
    }
    if (ensure_host_runtime_compat_libraries(output_root, native_lib_root) != 0) {
        return -1;
    }
    if (ensure_host_gles_compat_library(native_lib_root) != 0) {
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
             "%s/apex/com.android.art/javalib/apache-xml.jar:"
             "%s/apex/com.android.i18n/javalib/core-icu4j.jar:"
             "%s/apex/com.android.conscrypt/javalib/conscrypt.jar",
             getenv("ANDROID_ROOT"),
             getenv("ANDROID_ROOT"),
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
             "/apex/com.android.art/javalib/apache-xml.jar:"
             "/apex/com.android.i18n/javalib/core-icu4j.jar:"
             "/apex/com.android.conscrypt/javalib/conscrypt.jar");

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
    printf("  ANDROID_I18N_ROOT=%s\n", getenv("ANDROID_I18N_ROOT"));
    printf("  ANDROID_TZDATA_ROOT=%s\n", getenv("ANDROID_TZDATA_ROOT"));
    printf("  ANDROID_DATA=%s\n", getenv("ANDROID_DATA"));
    printf("  LD_LIBRARY_PATH=%s\n", getenv("LD_LIBRARY_PATH"));

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

int nova_art_launch_apk(struct nova_state *state, const char *apk_path, const char *activity_class) {
    char project_root[PATH_MAX];
    char detected_activity[PATH_MAX];
    char detected_package[PATH_MAX];
    const char *resolved_activity = activity_class;
    const char *resolved_package = NULL;
    jclass launcher_class;
    jmethodID launch_method;
    jstring apk_string;
    jstring activity_string;
    jstring package_string;

    if (state == NULL || state->env == NULL || apk_path == NULL) {
        return -1;
    }

    build_project_root(project_root, sizeof(project_root));
    if (resolved_activity == NULL || resolved_activity[0] == '\0') {
        if (detect_launchable_activity(project_root, apk_path,
                                       detected_activity, sizeof(detected_activity)) != 0) {
            return -1;
        }
        resolved_activity = detected_activity;
    }
    if (detect_package_name(project_root, apk_path,
                            detected_package, sizeof(detected_package)) != 0) {
        return -1;
    }
    resolved_package = detected_package;

    printf("[NovaART] Launch probe for APK: %s\n", apk_path);
    printf("[NovaART] Launch target package: %s\n", resolved_package);
    printf("[NovaART] Launch target activity: %s\n", resolved_activity);

    launcher_class = (*state->env)->FindClass(state->env, "nova/internal/Launcher");
    if (launcher_class == NULL || jni_log_and_clear_exception(state->env, "FindClass(nova/internal/Launcher)") != 0) {
        return -1;
    }

    launch_method = (*state->env)->GetStaticMethodID(
        state->env, launcher_class, "launch",
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    if (launch_method == NULL || jni_log_and_clear_exception(state->env, "GetStaticMethodID(Launcher.launch)") != 0) {
        return -1;
    }

    apk_string = (*state->env)->NewStringUTF(state->env, apk_path);
    if (apk_string == NULL || jni_log_and_clear_exception(state->env, "NewStringUTF(apk_path)") != 0) {
        return -1;
    }

    activity_string = (*state->env)->NewStringUTF(state->env, resolved_activity);
    if (activity_string == NULL || jni_log_and_clear_exception(state->env, "NewStringUTF(activity_class)") != 0) {
        return -1;
    }
    package_string = (*state->env)->NewStringUTF(state->env, resolved_package);
    if (package_string == NULL || jni_log_and_clear_exception(state->env, "NewStringUTF(package_name)") != 0) {
        return -1;
    }

    (*state->env)->CallStaticVoidMethod(state->env, launcher_class, launch_method,
                                        apk_string, activity_string, package_string);
    if (jni_log_and_clear_exception(state->env, "Launcher.launch") != 0) {
        return -1;
    }

    printf("[NovaART] Launch probe completed\n");
    return 0;
}

int nova_art_init_render(struct nova_state *state, struct nova_window *win) {
    if (!state || !state->env || !win) {
        return -1;
    }

    jclass canvas_render_class = (*state->env)->FindClass(state->env, "nova/internal/CanvasRender");
    if (!canvas_render_class) {
        return -1;
    }

    jmethodID set_state = (*state->env)->GetStaticMethodID(state->env, canvas_render_class,
                                                           "setRenderState", "(J)V");
    jmethodID set_window = (*state->env)->GetStaticMethodID(state->env, canvas_render_class,
                                                            "setRenderWindow", "(J)V");

    if (!set_state || !set_window) {
        (*state->env)->DeleteLocalRef(state->env, canvas_render_class);
        return -1;
    }

    (*state->env)->CallStaticVoidMethod(state->env, canvas_render_class, set_state,
                                       (jlong)(intptr_t)state);
    (*state->env)->CallStaticVoidMethod(state->env, canvas_render_class, set_window,
                                       (jlong)(intptr_t)win);

    (*state->env)->DeleteLocalRef(state->env, canvas_render_class);
    return 0;
}
