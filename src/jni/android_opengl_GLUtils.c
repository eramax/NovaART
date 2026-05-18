#include "core_jni_helpers.h"
#include "../softgfx.h"

#include <GLES2/gl2.h>
#include <stdint.h>
#include <stdlib.h>

/* Upload a nova_bitmap as a 2D texture at the given mip level. */
static void nativeTexImage2D(JNIEnv *env, jclass cls,
                              jint target, jint level, jlong bitmapHandle, jint border) {
    (void)cls;
    (void)env;
    struct nova_bitmap *bmp = (struct nova_bitmap *)(intptr_t)bitmapHandle;
    if (!bmp) return;

    int w = nova_bitmap_width(bmp);
    int h = nova_bitmap_height(bmp);
    uint32_t *pixels = nova_bitmap_pixels(bmp);

    /* nova_bitmap stores ARGB_8888 (0xAARRGGBB) — OpenGL wants RGBA8.
     * Convert in a stack-local copy to avoid mutating the bitmap.
     * For large textures, heap-allocate. */
    int count = w * h;
    uint32_t *rgba = (uint32_t *)malloc((size_t)count * 4);
    if (!rgba) return;

    for (int i = 0; i < count; i++) {
        uint32_t argb = pixels[i];
        uint8_t a = (argb >> 24) & 0xFF;
        uint8_t r = (argb >> 16) & 0xFF;
        uint8_t g = (argb >>  8) & 0xFF;
        uint8_t b = (argb      ) & 0xFF;
        rgba[i] = ((uint32_t)r)       |
                  ((uint32_t)g <<  8) |
                  ((uint32_t)b << 16) |
                  ((uint32_t)a << 24);
    }

    glTexImage2D((GLenum)target, level, GL_RGBA, w, h, border,
                 GL_RGBA, GL_UNSIGNED_BYTE, rgba);
    free(rgba);
}

static void nativeTexImage2DFormat(JNIEnv *env, jclass cls,
                                    jint target, jint level, jint internalformat,
                                    jlong bitmapHandle, jint border) {
    (void)cls;
    (void)env;
    struct nova_bitmap *bmp = (struct nova_bitmap *)(intptr_t)bitmapHandle;
    if (!bmp) return;

    int w = nova_bitmap_width(bmp);
    int h = nova_bitmap_height(bmp);
    uint32_t *pixels = nova_bitmap_pixels(bmp);
    int count = w * h;

    uint32_t *rgba = (uint32_t *)malloc((size_t)count * 4);
    if (!rgba) return;

    for (int i = 0; i < count; i++) {
        uint32_t argb = pixels[i];
        uint8_t a = (argb >> 24) & 0xFF;
        uint8_t r = (argb >> 16) & 0xFF;
        uint8_t g = (argb >>  8) & 0xFF;
        uint8_t b = (argb      ) & 0xFF;
        rgba[i] = ((uint32_t)r)       |
                  ((uint32_t)g <<  8) |
                  ((uint32_t)b << 16) |
                  ((uint32_t)a << 24);
    }

    GLenum fmt = (internalformat == 0x1907 /* GL_RGB */) ? GL_RGB : GL_RGBA;
    glTexImage2D((GLenum)target, level, (GLint)internalformat, w, h, border,
                 fmt, GL_UNSIGNED_BYTE, rgba);
    free(rgba);
}

static void nativeTexSubImage2D(JNIEnv *env, jclass cls,
                                 jint target, jint level,
                                 jint xoffset, jint yoffset,
                                 jlong bitmapHandle) {
    (void)cls;
    (void)env;
    struct nova_bitmap *bmp = (struct nova_bitmap *)(intptr_t)bitmapHandle;
    if (!bmp) return;

    int w = nova_bitmap_width(bmp);
    int h = nova_bitmap_height(bmp);
    uint32_t *pixels = nova_bitmap_pixels(bmp);
    int count = w * h;

    uint32_t *rgba = (uint32_t *)malloc((size_t)count * 4);
    if (!rgba) return;

    for (int i = 0; i < count; i++) {
        uint32_t argb = pixels[i];
        uint8_t a = (argb >> 24) & 0xFF;
        uint8_t r = (argb >> 16) & 0xFF;
        uint8_t g = (argb >>  8) & 0xFF;
        uint8_t b = (argb      ) & 0xFF;
        rgba[i] = ((uint32_t)r)       |
                  ((uint32_t)g <<  8) |
                  ((uint32_t)b << 16) |
                  ((uint32_t)a << 24);
    }

    glTexSubImage2D((GLenum)target, level, xoffset, yoffset, w, h,
                    GL_RGBA, GL_UNSIGNED_BYTE, rgba);
    free(rgba);
}

static const JNINativeMethod gMethods[] = {
    { "nativeTexImage2D",       "(IIJI)V",   (void *)nativeTexImage2D },
    { "nativeTexImage2DFormat", "(IIIJI)V",  (void *)nativeTexImage2DFormat },
    { "nativeTexSubImage2D",    "(IIIIJ)V",  (void *)nativeTexSubImage2D },
};

int register_android_opengl_GLUtils(JNIEnv *env) {
    return RegisterMethodsOrDie(env, "android/opengl/GLUtils",
                                gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}
