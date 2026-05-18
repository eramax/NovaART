/*
 * dlopen wrapper that adds RTLD_DEEPBIND for APK native libraries.
 *
 * APK native libraries (e.g. libgles3jni.so) define writable global function
 * pointer variables (glReadBuffer, etc.) that they fill at runtime via
 * dlsym(gl3stubInit). Without RTLD_DEEPBIND, the dynamic linker resolves
 * these symbols to the host system's libGLESv2.so.2, whose text segment is
 * read-only — causing SIGSEGV when the APK tries to write its own pointers.
 *
 * RTLD_DEEPBIND makes the loaded library prefer its own symbols over
 * already-loaded global ones, which is the correct Android behavior.
 *
 * Only applies to libraries loaded from the APK native-libs directory.
 */
#define _GNU_SOURCE
#include <dlfcn.h>
#include <string.h>
#include <stdio.h>

typedef void* (*dlopen_fn)(const char*, int);

static dlopen_fn get_real_dlopen(void) {
    static dlopen_fn real = NULL;
    if (!real) {
        real = (dlopen_fn)dlvsym(RTLD_NEXT, "dlopen", "GLIBC_2.34");
        if (!real) {
            real = (dlopen_fn)dlsym(RTLD_NEXT, "dlopen");
        }
    }
    return real;
}

void* dlopen(const char* filename, int flags) {
    dlopen_fn real = get_real_dlopen();

    if (filename && strstr(filename, "dex/native-libs/")) {
        const char* base = strrchr(filename, '/');
        base = base ? base + 1 : filename;
        fprintf(stderr, "[deepbind] %s RTLD_DEEPBIND\n", base);
        return real(filename, flags | RTLD_DEEPBIND);
    }

    return real(filename, flags);
}
