#ifndef NOVAART_CANVAS_RENDER_H
#define NOVAART_CANVAS_RENDER_H

#include "nova.h"

struct nova_canvas_render;
struct nova_canvas;
struct nova_window;

struct nova_canvas_render *nova_canvas_render_create(struct nova_state *state, int width, int height);
void nova_canvas_render_destroy(struct nova_canvas_render *render);
void nova_canvas_render_clear(struct nova_canvas_render *render, uint32_t color);
int nova_canvas_render_submit(struct nova_canvas_render *render, struct nova_window *win);
struct nova_canvas *nova_canvas_render_get_canvas(struct nova_canvas_render *render);
struct nova_bitmap *nova_canvas_render_get_backbuffer(struct nova_canvas_render *render);
int nova_canvas_render_get_width(struct nova_canvas_render *render);
int nova_canvas_render_get_height(struct nova_canvas_render *render);

#endif
