#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>
#include "nova.h"

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
    /* TODO: bind globals via registry listener */

    return state;
}

void nova_state_destroy(struct nova_state *state) {
    if (!state) return;
    if (state->display)
        wl_display_disconnect(state->display);
    free(state);
}

struct nova_window *nova_window_create(struct nova_state *state, int width, int height, const char *title) {
    (void)state;
    (void)title;
    struct nova_window *win = calloc(1, sizeof(struct nova_window));
    if (!win) return NULL;

    win->width = width;
    win->height = height;
    win->closed = 0;
    /* TODO: create wl_surface + xdg_toplevel after registry globals are bound */

    return win;
}

void nova_window_destroy(struct nova_window *win) {
    if (!win) return;
    /* TODO: destroy wayland objects */
    free(win);
}

void nova_window_set_title(struct nova_window *win, const char *title) {
    (void)win;
    (void)title;
    /* TODO: xdg_toplevel_set_title */
}

int nova_dispatch(struct nova_state *state) {
    if (!state || !state->display) return -1;
    /* TODO: non-blocking dispatch with polling */
    return wl_display_dispatch(state->display);
}
