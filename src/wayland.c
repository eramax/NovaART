#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>
#include <unistd.h>
#include <linux/input-event-codes.h>
#include "nova.h"
#include "xdg-shell-client-protocol.h"
#include "xdg-decoration-unstable-v1-client-protocol.h"

static void registry_global(void *data, struct wl_registry *registry, uint32_t name,
                            const char *interface, uint32_t version) {
    struct nova_state *state = data;

    if (strcmp(interface, wl_compositor_interface.name) == 0) {
        state->compositor = wl_registry_bind(registry, name, &wl_compositor_interface, 4);
    } else if (strcmp(interface, xdg_wm_base_interface.name) == 0) {
        state->wm_base = wl_registry_bind(registry, name, &xdg_wm_base_interface, 1);
    } else if (strcmp(interface, zxdg_decoration_manager_v1_interface.name) == 0) {
        state->decoration_manager = wl_registry_bind(
            registry, name, &zxdg_decoration_manager_v1_interface, 1);
    } else if (strcmp(interface, wl_subcompositor_interface.name) == 0) {
        state->subcompositor = wl_registry_bind(registry, name, &wl_subcompositor_interface, 1);
    } else if (strcmp(interface, wl_shm_interface.name) == 0) {
        state->shm = wl_registry_bind(registry, name, &wl_shm_interface, 1);
    } else if (strcmp(interface, wl_seat_interface.name) == 0) {
        state->seat = wl_registry_bind(registry, name, &wl_seat_interface, 1);
    }
}

static void registry_global_remove(void *data, struct wl_registry *registry, uint32_t name) {
    (void)data;
    (void)registry;
    (void)name;
}

static const struct wl_registry_listener kRegistryListener = {
    .global = registry_global,
    .global_remove = registry_global_remove,
};

static void wm_base_ping(void *data, struct xdg_wm_base *wm_base, uint32_t serial) {
    (void)data;
    xdg_wm_base_pong(wm_base, serial);
}

static const struct xdg_wm_base_listener kWmBaseListener = {
    .ping = wm_base_ping,
};

static void toplevel_decoration_configure(
        void *data,
        struct zxdg_toplevel_decoration_v1 *decoration,
        uint32_t mode) {
    struct nova_window *win = data;
    (void)decoration;
    (void)mode;
    (void)win;
}

static const struct zxdg_toplevel_decoration_v1_listener kToplevelDecorationListener = {
    .configure = toplevel_decoration_configure,
};

static void xdg_surface_configure(void *data, struct xdg_surface *xdg_surface, uint32_t serial) {
    struct nova_window *win = data;
    xdg_surface_ack_configure(xdg_surface, serial);
    win->configured = 1;
    wl_surface_commit(win->surface);
}

static const struct xdg_surface_listener kXdgSurfaceListener = {
    .configure = xdg_surface_configure,
};

static void xdg_toplevel_configure(void *data, struct xdg_toplevel *xdg_toplevel,
                                   int32_t width, int32_t height, struct wl_array *states) {
    struct nova_window *win = data;
    (void)xdg_toplevel;
    (void)states;
    if (width > 0) {
        win->width = width;
    }
    if (height > 0) {
        win->height = height;
    }
    nova_egl_resize_window(win->width, win->height);
}

static void xdg_toplevel_close(void *data, struct xdg_toplevel *xdg_toplevel) {
    struct nova_window *win = data;
    (void)xdg_toplevel;
    win->closed = 1;
}

static const struct xdg_toplevel_listener kXdgToplevelListener = {
    .configure = xdg_toplevel_configure,
    .close = xdg_toplevel_close,
    .configure_bounds = NULL,
    .wm_capabilities = NULL,
};

/* Input event handlers */
static void pointer_enter(void *data, struct wl_pointer *wl_pointer,
                         uint32_t serial, struct wl_surface *surface,
                         wl_fixed_t surface_x, wl_fixed_t surface_y) {
    (void)data;
    (void)wl_pointer;
    (void)serial;
    (void)surface;
    (void)surface_x;
    (void)surface_y;
}

static void pointer_leave(void *data, struct wl_pointer *wl_pointer,
                         uint32_t serial, struct wl_surface *surface) {
    (void)data;
    (void)wl_pointer;
    (void)serial;
    (void)surface;
}

static void pointer_motion(void *data, struct wl_pointer *wl_pointer,
                          uint32_t time, wl_fixed_t surface_x, wl_fixed_t surface_y) {
    (void)wl_pointer;
    struct nova_state *state = (struct nova_state *)data;
    if (!state->jvm || !state->env) return;

    double x = wl_fixed_to_double(surface_x);
    double y = wl_fixed_to_double(surface_y);

    jclass canvas_render = (*state->env)->FindClass(state->env, "nova/internal/CanvasRender");
    if (canvas_render) {
        jmethodID dispatch = (*state->env)->GetStaticMethodID(state->env, canvas_render,
                                                               "dispatchMotionEvent", "(JIFF)V");
        if (dispatch) {
            (*state->env)->CallStaticVoidMethod(state->env, canvas_render, dispatch,
                                               (jlong)time, (jint)2, (jfloat)x, (jfloat)y);
        }
        (*state->env)->DeleteLocalRef(state->env, canvas_render);
    }
}

static void pointer_button(void *data, struct wl_pointer *wl_pointer,
                          uint32_t serial, uint32_t time, uint32_t button, uint32_t state_enum) {
    (void)wl_pointer;
    (void)serial;
    struct nova_state *state = (struct nova_state *)data;
    if (!state->jvm || !state->env) return;

    int action = state_enum ? 0 : 1; /* 0=down, 1=up */
    if (button != BTN_LEFT) return;

    jclass canvas_render = (*state->env)->FindClass(state->env, "nova/internal/CanvasRender");
    if (canvas_render) {
        jmethodID dispatch = (*state->env)->GetStaticMethodID(state->env, canvas_render,
                                                               "dispatchMotionEvent", "(JIFF)V");
        if (dispatch) {
            (*state->env)->CallStaticVoidMethod(state->env, canvas_render, dispatch,
                                               (jlong)time, (jint)action, (jfloat)0, (jfloat)0);
        }
        (*state->env)->DeleteLocalRef(state->env, canvas_render);
    }
}

static void pointer_axis(void *data, struct wl_pointer *wl_pointer,
                        uint32_t time, uint32_t axis, wl_fixed_t value) {
    (void)data;
    (void)wl_pointer;
    (void)time;
    (void)axis;
    (void)value;
}

static void pointer_frame(void *data, struct wl_pointer *wl_pointer) {
    (void)data;
    (void)wl_pointer;
}

static const struct wl_pointer_listener pointer_listener = {
    .enter = pointer_enter,
    .leave = pointer_leave,
    .motion = pointer_motion,
    .button = pointer_button,
    .axis = pointer_axis,
    .frame = pointer_frame,
};

static void keyboard_keymap(void *data, struct wl_keyboard *wl_keyboard,
                           uint32_t format, int32_t fd, uint32_t size) {
    (void)data;
    (void)wl_keyboard;
    (void)format;
    (void)fd;
    (void)size;
    close(fd);
}

static void keyboard_enter(void *data, struct wl_keyboard *wl_keyboard,
                          uint32_t serial, struct wl_surface *surface,
                          struct wl_array *keys) {
    (void)data;
    (void)wl_keyboard;
    (void)serial;
    (void)surface;
    (void)keys;
}

static void keyboard_leave(void *data, struct wl_keyboard *wl_keyboard,
                          uint32_t serial, struct wl_surface *surface) {
    (void)data;
    (void)wl_keyboard;
    (void)serial;
    (void)surface;
}

static void keyboard_key(void *data, struct wl_keyboard *wl_keyboard,
                        uint32_t serial, uint32_t time, uint32_t key, uint32_t state_enum) {
    (void)wl_keyboard;
    (void)serial;
    struct nova_state *state = (struct nova_state *)data;
    if (!state->jvm || !state->env) return;

    int action = state_enum ? 0 : 1;
    int keycode = key + 8;

    jclass canvas_render = (*state->env)->FindClass(state->env, "nova/internal/CanvasRender");
    if (canvas_render) {
        jmethodID dispatch = (*state->env)->GetStaticMethodID(state->env, canvas_render,
                                                               "dispatchKeyEvent", "(IIJI)V");
        if (dispatch) {
            (*state->env)->CallStaticVoidMethod(state->env, canvas_render, dispatch,
                                               (jint)action, (jint)keycode, (jlong)time, (jint)0);
        }
        (*state->env)->DeleteLocalRef(state->env, canvas_render);
    }
}

static void keyboard_modifiers(void *data, struct wl_keyboard *wl_keyboard,
                              uint32_t serial, uint32_t mods_depressed, uint32_t mods_latched,
                              uint32_t mods_locked, uint32_t group) {
    (void)data;
    (void)wl_keyboard;
    (void)serial;
    (void)mods_depressed;
    (void)mods_latched;
    (void)mods_locked;
    (void)group;
}

static void keyboard_repeat_info(void *data, struct wl_keyboard *wl_keyboard,
                                int32_t rate, int32_t delay) {
    (void)data;
    (void)wl_keyboard;
    (void)rate;
    (void)delay;
}

static const struct wl_keyboard_listener keyboard_listener = {
    .keymap = keyboard_keymap,
    .enter = keyboard_enter,
    .leave = keyboard_leave,
    .key = keyboard_key,
    .modifiers = keyboard_modifiers,
    .repeat_info = keyboard_repeat_info,
};

static void seat_capabilities(void *data, struct wl_seat *wl_seat, uint32_t capabilities) {
    struct nova_state *state = (struct nova_state *)data;
    if (!state) return;

    if ((capabilities & WL_SEAT_CAPABILITY_POINTER) && !state->pointer) {
        state->pointer = wl_seat_get_pointer(wl_seat);
        wl_pointer_add_listener(state->pointer, &pointer_listener, state);
    }

    if ((capabilities & WL_SEAT_CAPABILITY_KEYBOARD) && !state->keyboard) {
        state->keyboard = wl_seat_get_keyboard(wl_seat);
        wl_keyboard_add_listener(state->keyboard, &keyboard_listener, state);
    }
}

static void seat_name(void *data, struct wl_seat *wl_seat, const char *name) {
    (void)data;
    (void)wl_seat;
    (void)name;
}

static const struct wl_seat_listener seat_listener = {
    .capabilities = seat_capabilities,
    .name = seat_name,
};

struct nova_state *nova_state_create(void) {
    struct nova_state *state = calloc(1, sizeof(struct nova_state));
    if (!state) return NULL;

    state->display = wl_display_connect(NULL);
    if (!state->display) {
        fprintf(stderr, "Failed to connect to Wayland display\n");
        free(state);
        return NULL;
    }

    state->registry = wl_display_get_registry(state->display);
    wl_registry_add_listener(state->registry, &kRegistryListener, state);
    wl_display_roundtrip(state->display);
    wl_display_roundtrip(state->display);

    if (!state->compositor || !state->wm_base) {
        fprintf(stderr, "Failed to bind required Wayland globals\n");
        nova_state_destroy(state);
        return NULL;
    }

    xdg_wm_base_add_listener(state->wm_base, &kWmBaseListener, state);

    if (state->seat) {
        wl_seat_add_listener(state->seat, &seat_listener, state);
    }

    return state;
}

void nova_state_destroy(struct nova_state *state) {
    if (!state) return;
    if (state->pointer)
        wl_pointer_destroy(state->pointer);
    if (state->keyboard)
        wl_keyboard_destroy(state->keyboard);
    if (state->seat)
        wl_seat_destroy(state->seat);
    if (state->shm)
        wl_shm_destroy(state->shm);
    if (state->subcompositor)
        wl_subcompositor_destroy(state->subcompositor);
    if (state->wm_base)
        xdg_wm_base_destroy(state->wm_base);
    if (state->decoration_manager)
        zxdg_decoration_manager_v1_destroy(state->decoration_manager);
    if (state->compositor)
        wl_compositor_destroy(state->compositor);
    if (state->registry)
        wl_registry_destroy(state->registry);
    if (state->display)
        wl_display_disconnect(state->display);
    free(state);
}

struct nova_window *nova_window_create(struct nova_state *state, int width, int height, const char *title) {
    struct nova_window *win = calloc(1, sizeof(struct nova_window));
    if (!win) return NULL;

    win->width = width;
    win->height = height;
    win->closed = 0;
    win->configured = 0;

    win->surface = wl_compositor_create_surface(state->compositor);
    if (!win->surface) {
        fprintf(stderr, "Failed to create wl_surface\n");
        free(win);
        return NULL;
    }

    win->xdg_surface = xdg_wm_base_get_xdg_surface(state->wm_base, win->surface);
    win->xdg_toplevel = xdg_surface_get_toplevel(win->xdg_surface);
    xdg_surface_add_listener(win->xdg_surface, &kXdgSurfaceListener, win);
    xdg_toplevel_add_listener(win->xdg_toplevel, &kXdgToplevelListener, win);
    if (state->decoration_manager) {
        win->xdg_decoration = zxdg_decoration_manager_v1_get_toplevel_decoration(
            state->decoration_manager, win->xdg_toplevel);
        if (win->xdg_decoration) {
            zxdg_toplevel_decoration_v1_add_listener(
                win->xdg_decoration, &kToplevelDecorationListener, win);
            zxdg_toplevel_decoration_v1_set_mode(
                win->xdg_decoration, ZXDG_TOPLEVEL_DECORATION_V1_MODE_SERVER_SIDE);
        }
    }
    xdg_toplevel_set_title(win->xdg_toplevel, title ? title : "NovaART");
    wl_surface_commit(win->surface);

    while (!win->configured && wl_display_dispatch(state->display) != -1) {
    }

    return win;
}

void nova_window_destroy(struct nova_window *win) {
    if (!win) return;
    if (win->xdg_decoration)
        zxdg_toplevel_decoration_v1_destroy(win->xdg_decoration);
    if (win->xdg_toplevel)
        xdg_toplevel_destroy(win->xdg_toplevel);
    if (win->xdg_surface)
        xdg_surface_destroy(win->xdg_surface);
    if (win->frame_callback)
        wl_callback_destroy(win->frame_callback);
    if (win->surface)
        wl_surface_destroy(win->surface);
    free(win);
}

void nova_window_set_title(struct nova_window *win, const char *title) {
    if (!win || !win->xdg_toplevel || !title) {
        return;
    }
    xdg_toplevel_set_title(win->xdg_toplevel, title);
    wl_surface_commit(win->surface);
}

int nova_dispatch(struct nova_state *state) {
    if (!state || !state->display) return -1;
    if (wl_display_dispatch_pending(state->display) < 0) {
        return -1;
    }
    if (wl_display_flush(state->display) < 0) {
        return -1;
    }
    return wl_display_dispatch(state->display);
}
