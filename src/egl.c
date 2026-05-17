#include <stdio.h>
#include <stdlib.h>
#include <dlfcn.h>
#include "nova.h"

#include <EGL/egl.h>
#include <EGL/eglext.h>

struct nova_egl *nova_egl_create(struct nova_state *state, struct nova_window *win) {
    (void)state;
    (void)win;
    struct nova_egl *egl = calloc(1, sizeof(struct nova_egl));
    if (!egl) return NULL;

    /* TODO: get EGL display from wl_egl, choose config, create context + surface */
    /* For now: create a surfaceless EGL context for testing */

    EGLDisplay disp = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (disp == EGL_NO_DISPLAY) {
        fprintf(stderr, "eglGetDisplay failed\n");
        free(egl);
        return NULL;
    }

    EGLint major, minor;
    if (!eglInitialize(disp, &major, &minor)) {
        fprintf(stderr, "eglInitialize failed\n");
        free(egl);
        return NULL;
    }

    printf("EGL initialized: %d.%d\n", major, minor);

    egl->display = disp;
    return egl;
}

void nova_egl_destroy(struct nova_egl *egl) {
    if (!egl) return;
    /* TODO: destroy EGL surface, context */
    if (egl->display)
        eglTerminate(egl->display);
    free(egl);
}

void nova_egl_swap_buffers(struct nova_egl *egl) {
    if (!egl || !egl->display) return;
    if (egl->surface)
        eglSwapBuffers(egl->display, egl->surface);
}
