#include <stdio.h>
#include <stdlib.h>
#include <dlfcn.h>
#include "nova.h"

#include <EGL/egl.h>
#include <EGL/eglext.h>

static struct nova_egl *g_active_egl;

struct nova_egl *nova_egl_get_active(void) {
    return g_active_egl;
}

void nova_egl_resize_window(int width, int height) {
    if (!g_active_egl || !g_active_egl->wl_window) {
        return;
    }
    wl_egl_window_resize(g_active_egl->wl_window, width, height, 0, 0);
}

struct nova_egl *nova_egl_create(struct nova_state *state, struct nova_window *win) {
    struct nova_egl *egl = calloc(1, sizeof(struct nova_egl));
    if (!egl) return NULL;

    egl->wl_window = wl_egl_window_create(win->surface, win->width, win->height);
    if (!egl->wl_window) {
        fprintf(stderr, "wl_egl_window_create failed\n");
        free(egl);
        return NULL;
    }

    EGLDisplay disp = eglGetDisplay((EGLNativeDisplayType)state->display);
    if (disp == EGL_NO_DISPLAY) {
        fprintf(stderr, "eglGetDisplay failed\n");
        wl_egl_window_destroy(egl->wl_window);
        free(egl);
        return NULL;
    }

    EGLint major, minor;
    if (!eglInitialize(disp, &major, &minor)) {
        fprintf(stderr, "eglInitialize failed\n");
        wl_egl_window_destroy(egl->wl_window);
        free(egl);
        return NULL;
    }

    EGLint config_attribs[] = {
        EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
        EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT | EGL_OPENGL_ES3_BIT_KHR,
        EGL_RED_SIZE, 8,
        EGL_GREEN_SIZE, 8,
        EGL_BLUE_SIZE, 8,
        EGL_ALPHA_SIZE, 0,
        EGL_DEPTH_SIZE, 16,
        EGL_STENCIL_SIZE, 0,
        EGL_NONE
    };
    EGLint num_configs = 0;
    if (!eglChooseConfig(disp, config_attribs, &egl->config, 1, &num_configs) || num_configs < 1) {
        fprintf(stderr, "eglChooseConfig failed\n");
        eglTerminate(disp);
        wl_egl_window_destroy(egl->wl_window);
        free(egl);
        return NULL;
    }

    printf("EGL initialized: %d.%d\n", major, minor);

    egl->display = disp;
    g_active_egl = egl;
    return egl;
}

void nova_egl_destroy(struct nova_egl *egl) {
    if (!egl) return;
    if (g_active_egl == egl) {
        g_active_egl = NULL;
    }
    if (egl->surface)
        eglDestroySurface(egl->display, egl->surface);
    if (egl->context)
        eglDestroyContext(egl->display, egl->context);
    if (egl->display)
        eglTerminate(egl->display);
    if (egl->wl_window)
        wl_egl_window_destroy(egl->wl_window);
    free(egl);
}

void nova_egl_swap_buffers(struct nova_egl *egl) {
    if (!egl || !egl->display) return;
    if (egl->surface)
        eglSwapBuffers(egl->display, egl->surface);
}
