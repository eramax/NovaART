#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#include "softgfx.h"

static void fail(const char *message) {
    fprintf(stderr, "%s\n", message);
    exit(1);
}

static void expect_pixel_eq(struct nova_bitmap *bitmap, int x, int y, uint32_t expected) {
    uint32_t actual = nova_bitmap_pixels(bitmap)[y * nova_bitmap_width(bitmap) + x];
    if (actual != expected) {
        fprintf(stderr,
                "pixel mismatch at (%d,%d): expected 0x%08x got 0x%08x\n",
                x, y, expected, actual);
        exit(1);
    }
}

int main(void) {
    struct nova_bitmap *bitmap = nova_bitmap_create(4, 4, 0, 1);
    struct nova_paint *paint = nova_paint_create();
    struct nova_canvas *canvas;

    if (!bitmap || !paint) {
        fail("failed to allocate graphics objects");
    }

    canvas = nova_canvas_create(bitmap);
    if (!canvas) {
        fail("failed to create canvas");
    }

    if (nova_canvas_width(canvas) != 4 || nova_canvas_height(canvas) != 4) {
        fail("canvas dimensions do not match bitmap");
    }

    nova_paint_set_color(paint, 0xffff0000u);
    nova_canvas_draw_rect(canvas, 1.0f, 1.0f, 3.0f, 3.0f, paint);
    expect_pixel_eq(bitmap, 0, 0, 0x00000000u);
    expect_pixel_eq(bitmap, 1, 1, 0xffff0000u);
    expect_pixel_eq(bitmap, 2, 2, 0xffff0000u);
    expect_pixel_eq(bitmap, 3, 3, 0x00000000u);

    nova_bitmap_clear(bitmap, 0x00000000u);
    nova_paint_set_style(paint, NOVA_PAINT_STYLE_STROKE);
    nova_paint_set_stroke_width(paint, 1.0f);
    nova_paint_set_color(paint, 0xff00ff00u);
    nova_canvas_draw_rect(canvas, 0.0f, 0.0f, 4.0f, 4.0f, paint);
    expect_pixel_eq(bitmap, 0, 0, 0xff00ff00u);
    expect_pixel_eq(bitmap, 3, 0, 0xff00ff00u);
    expect_pixel_eq(bitmap, 0, 3, 0xff00ff00u);
    expect_pixel_eq(bitmap, 3, 3, 0xff00ff00u);
    expect_pixel_eq(bitmap, 1, 1, 0x00000000u);
    expect_pixel_eq(bitmap, 2, 2, 0x00000000u);

    nova_canvas_destroy(canvas);
    nova_paint_destroy(paint);
    nova_bitmap_destroy(bitmap);
    return 0;
}
