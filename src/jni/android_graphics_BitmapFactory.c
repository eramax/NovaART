#include "core_jni_helpers.h"
#include "../softgfx.h"

#include <png.h>
#include <string.h>
#include <stdlib.h>
#include <setjmp.h>

/* PNG memory-read state */
struct png_mem_read {
    const unsigned char *data;
    size_t pos;
    size_t len;
};

static void png_mem_read_fn(png_structp png_ptr, png_bytep out, png_size_t length) {
    struct png_mem_read *r = (struct png_mem_read *)png_get_io_ptr(png_ptr);
    if (r->pos + length > r->len) {
        png_error(png_ptr, "PNG read beyond buffer");
        return;
    }
    memcpy(out, r->data + r->pos, length);
    r->pos += length;
}

static struct nova_bitmap *decode_png(const unsigned char *data, size_t len) {
    if (len < 8 || png_sig_cmp((png_const_bytep)data, 0, 8)) return NULL;

    png_structp png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
    if (!png_ptr) return NULL;

    png_infop info_ptr = png_create_info_struct(png_ptr);
    if (!info_ptr) { png_destroy_read_struct(&png_ptr, NULL, NULL); return NULL; }

    if (setjmp(png_jmpbuf(png_ptr))) {
        png_destroy_read_struct(&png_ptr, &info_ptr, NULL);
        return NULL;
    }

    struct png_mem_read mem = { data, 0, len };
    png_set_read_fn(png_ptr, &mem, png_mem_read_fn);
    png_read_info(png_ptr, info_ptr);

    int width      = (int)png_get_image_width(png_ptr, info_ptr);
    int height     = (int)png_get_image_height(png_ptr, info_ptr);
    int color_type = png_get_color_type(png_ptr, info_ptr);
    int bit_depth  = png_get_bit_depth(png_ptr, info_ptr);

    /* Normalise to RGBA8 */
    if (bit_depth == 16) png_set_strip_16(png_ptr);
    if (color_type == PNG_COLOR_TYPE_PALETTE) png_set_palette_to_rgb(png_ptr);
    if (color_type == PNG_COLOR_TYPE_GRAY && bit_depth < 8) png_set_expand_gray_1_2_4_to_8(png_ptr);
    if (png_get_valid(png_ptr, info_ptr, PNG_INFO_tRNS)) png_set_tRNS_to_alpha(png_ptr);
    if (color_type == PNG_COLOR_TYPE_RGB || color_type == PNG_COLOR_TYPE_GRAY
            || color_type == PNG_COLOR_TYPE_PALETTE) {
        png_set_filler(png_ptr, 0xFF, PNG_FILLER_AFTER);
    }
    if (color_type == PNG_COLOR_TYPE_GRAY || color_type == PNG_COLOR_TYPE_GRAY_ALPHA) {
        png_set_gray_to_rgb(png_ptr);
    }
    png_read_update_info(png_ptr, info_ptr);

    struct nova_bitmap *bmp = nova_bitmap_create(width, height, 1, 1);
    if (!bmp) { png_destroy_read_struct(&png_ptr, &info_ptr, NULL); return NULL; }

    uint32_t *pixels = nova_bitmap_pixels(bmp);
    if (!pixels) { nova_bitmap_destroy(bmp); png_destroy_read_struct(&png_ptr, &info_ptr, NULL); return NULL; }

    png_bytep *rows = (png_bytep *)malloc(height * sizeof(png_bytep));
    if (!rows) { nova_bitmap_destroy(bmp); png_destroy_read_struct(&png_ptr, &info_ptr, NULL); return NULL; }
    for (int y = 0; y < height; y++) rows[y] = (png_bytep)(pixels + y * width);

    png_read_image(png_ptr, rows);
    free(rows);

    /* PNG is RGBA; Android Bitmap is ARGB_8888 = stored as RGBA in memory on little-endian */
    /* libpng gives us RGBA bytes; we want 0xAARRGGBB in int.                               */
    /* On little-endian: int stored as [B][G][R][A] in memory, so pixel[i] = AABBGGRR.      */
    /* Convert RGBA bytes → ARGB int.                                                        */
    for (int i = 0; i < width * height; i++) {
        unsigned char *p = (unsigned char *)&pixels[i];
        unsigned char r = p[0], g = p[1], b = p[2], a = p[3];
        pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
    }

    png_destroy_read_struct(&png_ptr, &info_ptr, NULL);
    return bmp;
}

/* Very minimal JPEG decoder fallback — skip for now; return a 1x1 dummy */
static struct nova_bitmap *decode_jpeg(const unsigned char *data, size_t len) {
    (void)data; (void)len;
    /* JPEG support requires libjpeg - provide 1x1 grey placeholder */
    struct nova_bitmap *bmp = nova_bitmap_create(1, 1, 1, 1);
    if (bmp) {
        uint32_t *pixels = nova_bitmap_pixels(bmp);
        if (pixels) pixels[0] = 0xFF808080;
    }
    return bmp;
}

static jlong nativeDecodeBytes(JNIEnv *env, jclass cls,
                                jbyteArray data, jint offset, jint length, jboolean justBounds) {
    (void)cls;
    if (!data || length <= 0) return 0L;

    jbyte *bytes = (*env)->GetByteArrayElements(env, data, NULL);
    if (!bytes) return 0L;

    const unsigned char *buf = (const unsigned char *)bytes + offset;
    size_t len = (size_t)length;

    struct nova_bitmap *bmp = NULL;

    /* Check PNG signature */
    if (len >= 8 && buf[0] == 0x89 && buf[1] == 'P' && buf[2] == 'N' && buf[3] == 'G') {
        bmp = decode_png(buf, len);
    }
    /* Check JPEG signature */
    else if (len >= 3 && buf[0] == 0xFF && buf[1] == 0xD8 && buf[2] == 0xFF) {
        bmp = decode_jpeg(buf, len);
    }
    /* Check WebP / other — produce placeholder */
    else {
        bmp = nova_bitmap_create(1, 1, 1, 1);
        if (bmp) {
            uint32_t *pixels = nova_bitmap_pixels(bmp);
            if (pixels) pixels[0] = 0xFF404040;
        }
    }

    (*env)->ReleaseByteArrayElements(env, data, bytes, JNI_ABORT);

    if (justBounds && bmp) {
        /* Caller only wants dimensions — but we decoded anyway; return the bitmap */
        /* so the Java side can read outWidth/outHeight from it */
    }

    return (jlong)(intptr_t)bmp;
}

static const JNINativeMethod gMethods[] = {
    { "nativeDecodeBytes", "([BIIZ)J", (void *)nativeDecodeBytes },
};

int register_android_graphics_BitmapFactory(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/graphics/BitmapFactory",
                                gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
