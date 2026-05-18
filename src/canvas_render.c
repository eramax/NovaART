#define _GNU_SOURCE

#include "nova.h"
#include "softgfx.h"

#include <sys/mman.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>

struct nova_canvas_render {
    struct nova_bitmap *backbuffer;
    struct nova_canvas *canvas;
    int width;
    int height;
    int display_width;
    int display_height;
    struct wl_buffer *wl_buffer;
    uint32_t *shm_data;
    size_t shm_size;
    int shm_fd;
};

static void shm_format(void *data, struct wl_shm *wl_shm, uint32_t format) {
    (void)data;
    (void)wl_shm;
    (void)format;
}

static const struct wl_shm_listener shm_listener = {
    shm_format,
};

static void buffer_release(void *data, struct wl_buffer *wl_buffer) {
    (void)data;
    (void)wl_buffer;
}

static const struct wl_buffer_listener buffer_listener = {
    buffer_release,
};

static int create_shm_buffer(struct nova_state *state, struct nova_canvas_render *render) {
    int width = render->width;
    int height = render->height;
    int stride = width * 4;
    size_t size = stride * height;

    render->shm_fd = memfd_create("novaart-canvas", 0);
    if (render->shm_fd < 0) {
        return -1;
    }

    if (ftruncate(render->shm_fd, (off_t)size) < 0) {
        close(render->shm_fd);
        return -1;
    }

    render->shm_data = mmap(NULL, size, PROT_READ | PROT_WRITE, MAP_SHARED, render->shm_fd, 0);
    if (render->shm_data == MAP_FAILED) {
        close(render->shm_fd);
        return -1;
    }

    render->shm_size = size;
    struct wl_shm_pool *pool = wl_shm_create_pool(state->shm, render->shm_fd, (int32_t)size);
    if (!pool) {
        munmap(render->shm_data, render->shm_size);
        close(render->shm_fd);
        return -1;
    }

    render->wl_buffer = wl_shm_pool_create_buffer(pool, 0, width, height, stride, WL_SHM_FORMAT_ARGB8888);
    wl_shm_pool_destroy(pool);

    if (!render->wl_buffer) {
        munmap(render->shm_data, render->shm_size);
        close(render->shm_fd);
        return -1;
    }

    wl_buffer_add_listener(render->wl_buffer, &buffer_listener, NULL);
    return 0;
}

struct nova_canvas_render *nova_canvas_render_create(struct nova_state *state, int width, int height) {
    struct nova_canvas_render *render = calloc(1, sizeof(*render));
    if (!render) return NULL;

    render->width = width;
    render->height = height;
    render->display_width = width;
    render->display_height = height;

    if (state->shm) {
        if (create_shm_buffer(state, render) < 0) {
            free(render);
            return NULL;
        }
    }

    render->backbuffer = nova_bitmap_create(width, height, 1, 1);
    if (!render->backbuffer) {
        if (render->wl_buffer) wl_buffer_destroy(render->wl_buffer);
        if (render->shm_data) munmap(render->shm_data, render->shm_size);
        if (render->shm_fd >= 0) close(render->shm_fd);
        free(render);
        return NULL;
    }

    render->canvas = nova_canvas_create(render->backbuffer);
    if (!render->canvas) {
        nova_bitmap_destroy(render->backbuffer);
        if (render->wl_buffer) wl_buffer_destroy(render->wl_buffer);
        if (render->shm_data) munmap(render->shm_data, render->shm_size);
        if (render->shm_fd >= 0) close(render->shm_fd);
        free(render);
        return NULL;
    }

    return render;
}

void nova_canvas_render_destroy(struct nova_canvas_render *render) {
    if (!render) return;
    if (render->canvas) nova_canvas_destroy(render->canvas);
    if (render->backbuffer) nova_bitmap_destroy(render->backbuffer);
    if (render->wl_buffer) wl_buffer_destroy(render->wl_buffer);
    if (render->shm_data) munmap(render->shm_data, render->shm_size);
    if (render->shm_fd >= 0) close(render->shm_fd);
    free(render);
}

void nova_canvas_render_clear(struct nova_canvas_render *render, uint32_t color) {
    if (!render || !render->backbuffer) return;
    nova_bitmap_clear(render->backbuffer, color);
}

int nova_canvas_render_submit(struct nova_canvas_render *render, struct nova_window *win) {
    if (!render || !win || !render->shm_data) return -1;

    uint32_t *pixels = nova_bitmap_pixels(render->backbuffer);
    if (!pixels) return -1;

    size_t pixel_count = (size_t)render->width * (size_t)render->height;
    memcpy(render->shm_data, pixels, pixel_count * 4);

    wl_surface_attach(win->surface, render->wl_buffer, 0, 0);
    wl_surface_damage_buffer(win->surface, 0, 0, render->width, render->height);
    wl_surface_commit(win->surface);

    return 0;
}

struct nova_canvas *nova_canvas_render_get_canvas(struct nova_canvas_render *render) {
    return render ? render->canvas : NULL;
}

struct nova_bitmap *nova_canvas_render_get_backbuffer(struct nova_canvas_render *render) {
    return render ? render->backbuffer : NULL;
}

int nova_canvas_render_get_width(struct nova_canvas_render *render) {
    return render ? render->width : 0;
}

int nova_canvas_render_get_height(struct nova_canvas_render *render) {
    return render ? render->height : 0;
}
