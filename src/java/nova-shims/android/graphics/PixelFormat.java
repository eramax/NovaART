package android.graphics;

public class PixelFormat {

    public static final int UNKNOWN      = 0;
    public static final int TRANSLUCENT  = -3;
    public static final int TRANSPARENT  = -2;
    public static final int OPAQUE       = -1;
    public static final int RGBA_8888    = 1;
    public static final int RGBX_8888    = 2;
    public static final int RGB_888      = 3;
    public static final int RGB_565      = 4;
    public static final int RGBA_5551    = 6;
    public static final int RGBA_4444    = 7;
    public static final int A_8          = 8;
    public static final int L_8          = 9;
    public static final int LA_88        = 10;
    public static final int RGB_332      = 11;
    public static final int YCbCr_422_SP = 16;
    public static final int YCbCr_420_SP = 17;
    public static final int YCbCr_422_I  = 18;
    public static final int RGBA_FP16    = 22;
    public static final int RGBA_1010102 = 43;
    public static final int JPEG         = 256;

    public int bytesPerPixel;
    public int bitsPerPixel;

    public static boolean formatHasAlpha(int format) {
        return format == TRANSLUCENT || format == TRANSPARENT || format == RGBA_8888
                || format == RGBA_5551 || format == RGBA_4444 || format == RGBA_FP16
                || format == A_8 || format == LA_88;
    }

    public static void getPixelFormatInfo(int format, PixelFormat info) {
        switch (format) {
            case RGBA_8888: info.bitsPerPixel = 32; info.bytesPerPixel = 4; break;
            case RGBX_8888: info.bitsPerPixel = 32; info.bytesPerPixel = 4; break;
            case RGB_888:   info.bitsPerPixel = 24; info.bytesPerPixel = 3; break;
            case RGB_565:   info.bitsPerPixel = 16; info.bytesPerPixel = 2; break;
            case A_8:       info.bitsPerPixel = 8;  info.bytesPerPixel = 1; break;
            default:        info.bitsPerPixel = 32; info.bytesPerPixel = 4; break;
        }
    }

    public static boolean isPublicFormat(int format) {
        return format == RGBA_8888 || format == RGBX_8888 || format == RGB_565 || format == RGB_888;
    }
}
