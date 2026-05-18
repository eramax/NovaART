#include "softgfx.h"

#include <stdlib.h>
#include <string.h>

struct nova_bitmap {
    int width;
    int height;
    int config;
    int is_mutable;
    uint32_t *pixels;
};

struct nova_paint {
    uint32_t color;
    float stroke_width;
    int style;
    int anti_alias;
};

struct nova_canvas {
    struct nova_bitmap *bitmap;
    int save_depth;
};

static int clamp_floor(float value, int min_value, int max_value) {
    int result = (int)value;

    if (value < (float)result) {
        result -= 1;
    }
    if (result < min_value) {
        return min_value;
    }
    if (result > max_value) {
        return max_value;
    }
    return result;
}

static int clamp_ceil(float value, int min_value, int max_value) {
    int result = (int)value;

    if (value > (float)result) {
        result += 1;
    }
    if (result < min_value) {
        return min_value;
    }
    if (result > max_value) {
        return max_value;
    }
    return result;
}

static void fill_span(struct nova_bitmap *bitmap, int x0, int y0, int x1, int y1, uint32_t color) {
    int y;

    if (!bitmap || !bitmap->pixels) {
        return;
    }
    if (x0 < 0) {
        x0 = 0;
    }
    if (y0 < 0) {
        y0 = 0;
    }
    if (x1 > bitmap->width) {
        x1 = bitmap->width;
    }
    if (y1 > bitmap->height) {
        y1 = bitmap->height;
    }
    if (x0 >= x1 || y0 >= y1) {
        return;
    }

    for (y = y0; y < y1; y++) {
        int x;
        uint32_t *row = bitmap->pixels + (y * bitmap->width);
        for (x = x0; x < x1; x++) {
            row[x] = color;
        }
    }
}

struct nova_bitmap *nova_bitmap_create(int width, int height, int config, int is_mutable) {
    struct nova_bitmap *bitmap;
    size_t pixel_count;

    if (width <= 0 || height <= 0) {
        return NULL;
    }

    bitmap = calloc(1, sizeof(*bitmap));
    if (!bitmap) {
        return NULL;
    }

    pixel_count = (size_t)width * (size_t)height;
    bitmap->pixels = calloc(pixel_count, sizeof(*bitmap->pixels));
    if (!bitmap->pixels) {
        free(bitmap);
        return NULL;
    }

    bitmap->width = width;
    bitmap->height = height;
    bitmap->config = config;
    bitmap->is_mutable = is_mutable;
    return bitmap;
}

void nova_bitmap_destroy(struct nova_bitmap *bitmap) {
    if (!bitmap) {
        return;
    }
    free(bitmap->pixels);
    free(bitmap);
}

void nova_bitmap_clear(struct nova_bitmap *bitmap, uint32_t color) {
    int x;
    int y;

    if (!bitmap || !bitmap->pixels) {
        return;
    }

    for (y = 0; y < bitmap->height; y++) {
        uint32_t *row = bitmap->pixels + (y * bitmap->width);
        for (x = 0; x < bitmap->width; x++) {
            row[x] = color;
        }
    }
}

int nova_bitmap_width(const struct nova_bitmap *bitmap) {
    return bitmap ? bitmap->width : 0;
}

int nova_bitmap_height(const struct nova_bitmap *bitmap) {
    return bitmap ? bitmap->height : 0;
}

int nova_bitmap_config(const struct nova_bitmap *bitmap) {
    return bitmap ? bitmap->config : 0;
}

uint32_t *nova_bitmap_pixels(struct nova_bitmap *bitmap) {
    return bitmap ? bitmap->pixels : NULL;
}

struct nova_paint *nova_paint_create(void) {
    struct nova_paint *paint = calloc(1, sizeof(*paint));

    if (!paint) {
        return NULL;
    }

    paint->color = 0xff000000u;
    paint->stroke_width = 1.0f;
    paint->style = NOVA_PAINT_STYLE_FILL;
    return paint;
}

struct nova_paint *nova_paint_clone(const struct nova_paint *paint) {
    struct nova_paint *copy;

    if (!paint) {
        return nova_paint_create();
    }

    copy = malloc(sizeof(*copy));
    if (!copy) {
        return NULL;
    }
    memcpy(copy, paint, sizeof(*copy));
    return copy;
}

void nova_paint_destroy(struct nova_paint *paint) {
    free(paint);
}

void nova_paint_set_anti_alias(struct nova_paint *paint, int enabled) {
    if (paint) {
        paint->anti_alias = enabled ? 1 : 0;
    }
}

void nova_paint_set_color(struct nova_paint *paint, uint32_t color) {
    if (paint) {
        paint->color = color;
    }
}

void nova_paint_set_stroke_width(struct nova_paint *paint, float width) {
    if (paint) {
        paint->stroke_width = width > 0.0f ? width : 1.0f;
    }
}

void nova_paint_set_style(struct nova_paint *paint, int style) {
    if (!paint) {
        return;
    }
    if (style < NOVA_PAINT_STYLE_FILL || style > NOVA_PAINT_STYLE_FILL_AND_STROKE) {
        style = NOVA_PAINT_STYLE_FILL;
    }
    paint->style = style;
}

struct nova_canvas *nova_canvas_create(struct nova_bitmap *bitmap) {
    struct nova_canvas *canvas = calloc(1, sizeof(*canvas));

    if (!canvas) {
        return NULL;
    }

    canvas->bitmap = bitmap;
    canvas->save_depth = 1;
    return canvas;
}

void nova_canvas_destroy(struct nova_canvas *canvas) {
    free(canvas);
}

void nova_canvas_set_bitmap(struct nova_canvas *canvas, struct nova_bitmap *bitmap) {
    if (canvas) {
        canvas->bitmap = bitmap;
    }
}

void nova_canvas_draw_rect(struct nova_canvas *canvas, float left, float top,
                           float right, float bottom, const struct nova_paint *paint) {
    int x0;
    int y0;
    int x1;
    int y1;
    int stroke;

    if (!canvas || !canvas->bitmap || !paint) {
        return;
    }

    if (right < left) {
        float swap = left;
        left = right;
        right = swap;
    }
    if (bottom < top) {
        float swap = top;
        top = bottom;
        bottom = swap;
    }

    x0 = clamp_floor(left, 0, canvas->bitmap->width);
    y0 = clamp_floor(top, 0, canvas->bitmap->height);
    x1 = clamp_ceil(right, 0, canvas->bitmap->width);
    y1 = clamp_ceil(bottom, 0, canvas->bitmap->height);

    if (paint->style == NOVA_PAINT_STYLE_FILL || paint->style == NOVA_PAINT_STYLE_FILL_AND_STROKE) {
        fill_span(canvas->bitmap, x0, y0, x1, y1, paint->color);
    }

    if (paint->style == NOVA_PAINT_STYLE_STROKE || paint->style == NOVA_PAINT_STYLE_FILL_AND_STROKE) {
        stroke = (int)paint->stroke_width;
        if (stroke < 1) {
            stroke = 1;
        }

        fill_span(canvas->bitmap, x0, y0, x1, y0 + stroke, paint->color);
        fill_span(canvas->bitmap, x0, y1 - stroke, x1, y1, paint->color);
        fill_span(canvas->bitmap, x0, y0 + stroke, x0 + stroke, y1 - stroke, paint->color);
        fill_span(canvas->bitmap, x1 - stroke, y0 + stroke, x1, y1 - stroke, paint->color);
    }
}

int nova_canvas_save(struct nova_canvas *canvas, int save_flags) {
    (void)save_flags;
    if (!canvas) {
        return 0;
    }
    canvas->save_depth += 1;
    return canvas->save_depth;
}

void nova_canvas_restore(struct nova_canvas *canvas) {
    if (canvas && canvas->save_depth > 1) {
        canvas->save_depth -= 1;
    }
}

int nova_canvas_width(const struct nova_canvas *canvas) {
    return (canvas && canvas->bitmap) ? canvas->bitmap->width : 0;
}

int nova_canvas_height(const struct nova_canvas *canvas) {
    return (canvas && canvas->bitmap) ? canvas->bitmap->height : 0;
}
