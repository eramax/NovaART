#ifndef NOVAART_SOFTGFX_H
#define NOVAART_SOFTGFX_H

#include <stdint.h>

enum nova_paint_style {
    NOVA_PAINT_STYLE_FILL = 0,
    NOVA_PAINT_STYLE_STROKE = 1,
    NOVA_PAINT_STYLE_FILL_AND_STROKE = 2,
};

struct nova_bitmap;
struct nova_paint;
struct nova_canvas;

struct nova_bitmap *nova_bitmap_create(int width, int height, int config, int is_mutable);
void nova_bitmap_destroy(struct nova_bitmap *bitmap);
void nova_bitmap_clear(struct nova_bitmap *bitmap, uint32_t color);
int nova_bitmap_width(const struct nova_bitmap *bitmap);
int nova_bitmap_height(const struct nova_bitmap *bitmap);
int nova_bitmap_config(const struct nova_bitmap *bitmap);
uint32_t *nova_bitmap_pixels(struct nova_bitmap *bitmap);

struct nova_paint *nova_paint_create(void);
struct nova_paint *nova_paint_clone(const struct nova_paint *paint);
void nova_paint_destroy(struct nova_paint *paint);
void nova_paint_set_anti_alias(struct nova_paint *paint, int enabled);
void nova_paint_set_color(struct nova_paint *paint, uint32_t color);
void nova_paint_set_stroke_width(struct nova_paint *paint, float width);
void nova_paint_set_style(struct nova_paint *paint, int style);

struct nova_canvas *nova_canvas_create(struct nova_bitmap *bitmap);
void nova_canvas_destroy(struct nova_canvas *canvas);
void nova_canvas_set_bitmap(struct nova_canvas *canvas, struct nova_bitmap *bitmap);
void nova_canvas_draw_rect(struct nova_canvas *canvas, float left, float top,
                           float right, float bottom, const struct nova_paint *paint);
void nova_canvas_draw_color(struct nova_canvas *canvas, uint32_t color);
int nova_canvas_save(struct nova_canvas *canvas, int save_flags);
void nova_canvas_restore(struct nova_canvas *canvas);
int nova_canvas_width(const struct nova_canvas *canvas);
int nova_canvas_height(const struct nova_canvas *canvas);

#endif
