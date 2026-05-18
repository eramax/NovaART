package android.opengl;

import android.graphics.Bitmap;
import android.util.Log;

public final class GLUtils {
    private static final String TAG = "NovaGLUtils";
    private GLUtils() {}

    public static void texImage2D(int target, int level, Bitmap bitmap, int border) {
        if (bitmap == null) return;
        nativeTexImage2D(target, level, bitmap.getNativeInstance(), border);
    }

    public static void texImage2D(int target, int level, int internalformat,
            Bitmap bitmap, int border) {
        if (bitmap == null) return;
        nativeTexImage2DFormat(target, level, internalformat, bitmap.getNativeInstance(), border);
    }

    public static void texSubImage2D(int target, int level, int xoffset, int yoffset,
            Bitmap bitmap) {
        if (bitmap == null) return;
        nativeTexSubImage2D(target, level, xoffset, yoffset, bitmap.getNativeInstance());
    }

    public static int getInternalFormat(Bitmap bitmap) {
        if (bitmap == null) return GLES20.GL_RGBA;
        Bitmap.Config config = bitmap.getConfig();
        return (config == Bitmap.Config.ARGB_8888) ? GLES20.GL_RGBA : GLES20.GL_RGB;
    }

    public static int getType(Bitmap bitmap) {
        return GLES20.GL_UNSIGNED_BYTE;
    }

    public static String getEGLErrorString(int error) {
        switch (error) {
            case 0x3000: return "EGL_SUCCESS";
            case 0x3001: return "EGL_NOT_INITIALIZED";
            case 0x3002: return "EGL_BAD_ACCESS";
            case 0x3003: return "EGL_BAD_ALLOC";
            case 0x3006: return "EGL_BAD_DISPLAY";
            case 0x3008: return "EGL_BAD_MATCH";
            case 0x300B: return "EGL_BAD_PARAMETER";
            case 0x300C: return "EGL_BAD_SURFACE";
            default: return "EGL_UNKNOWN(" + error + ")";
        }
    }

    private static native void nativeTexImage2D(int target, int level, long bitmapHandle, int border);
    private static native void nativeTexImage2DFormat(int target, int level, int internalformat,
            long bitmapHandle, int border);
    private static native void nativeTexSubImage2D(int target, int level, int xoffset, int yoffset,
            long bitmapHandle);
}
