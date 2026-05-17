#ifndef NOVAART_H
#define NOVAART_H

#include <wayland-client.h>
#include <wayland-egl.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <jni.h>

/* Opaque forward decls (actual types in generated xdg-shell-client-protocol.h) */
struct xdg_wm_base;
struct xdg_surface;
struct xdg_toplevel;

/* Wayland globals */
struct nova_state {
    struct wl_display *display;
    struct wl_registry *registry;
    struct wl_compositor *compositor;
    struct wl_subcompositor *subcompositor;
    struct xdg_wm_base *wm_base;
    struct wl_seat *seat;
    struct wl_shm *shm;
    struct wl_keyboard *keyboard;
    struct wl_pointer *pointer;

    /* ART runtime */
    JavaVM *jvm;
    JNIEnv *env;
    void *libart_handle;
};

/* Window */
struct nova_window {
    struct wl_surface *surface;
    struct xdg_surface *xdg_surface;
    struct xdg_toplevel *xdg_toplevel;
    struct wl_callback *frame_callback;
    int width;
    int height;
    int closed;
    void *user_data;
};

/* EGL context */
struct nova_egl {
    struct wl_egl_window *wl_window;
    EGLDisplay display;
    EGLConfig config;
    EGLContext context;
    EGLSurface surface;
};

/* wayland.c */
struct nova_state *nova_state_create(void);
void nova_state_destroy(struct nova_state *state);
struct nova_window *nova_window_create(struct nova_state *state, int width, int height, const char *title);
void nova_window_destroy(struct nova_window *win);
void nova_window_set_title(struct nova_window *win, const char *title);
int nova_dispatch(struct nova_state *state);

/* egl.c */
struct nova_egl *nova_egl_create(struct nova_state *state, struct nova_window *win);
void nova_egl_destroy(struct nova_egl *egl);
void nova_egl_swap_buffers(struct nova_egl *egl);

/* art.c */
int nova_art_init(struct nova_state *state, int argc, char *argv[]);
void nova_art_shutdown(struct nova_state *state);
jclass nova_art_find_class(struct nova_state *state, const char *name);
jmethodID nova_art_get_static_method(struct nova_state *state, jclass cls, const char *name, const char *sig);
jmethodID nova_art_get_method(struct nova_state *state, jclass cls, const char *name, const char *sig);

/* JNI registration */
int register_all_jni_stubs(JNIEnv *env);

#endif /* NOVAART_H */
