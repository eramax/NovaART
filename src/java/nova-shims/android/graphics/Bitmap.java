package android.graphics;

public class Bitmap {
    public enum Config {
        ARGB_8888(1);

        final int nativeInt;

        Config(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    private long mNativePtr;

    private Bitmap(long nativePtr) {
        mNativePtr = nativePtr;
    }

    static Bitmap createFromNative(long nativePtr) {
        if (nativePtr == 0) return null;
        return new Bitmap(nativePtr);
    }

    public static Bitmap createBitmap(int width, int height, Config config) {
        Config resolvedConfig = config != null ? config : Config.ARGB_8888;
        long nativePtr = nativeCreate(width, height, resolvedConfig == Config.ARGB_8888, true, 0L);
        if (nativePtr == 0L) {
            throw new OutOfMemoryError("Bitmap allocation failed");
        }
        return new Bitmap(nativePtr);
    }

    public long getNativeInstance() {
        return mNativePtr;
    }

    public void recycle() {
        if (mNativePtr != 0L) {
            nativeRecycle(mNativePtr);
            mNativePtr = 0L;
        }
    }

    public int getWidth() {
        return nativeGetWidth(mNativePtr);
    }

    public int getHeight() {
        return nativeGetHeight(mNativePtr);
    }

    public Config getConfig() {
        return nativeGetConfig(mNativePtr) == Config.ARGB_8888.nativeInt ? Config.ARGB_8888 : null;
    }

    public int[] getPixels() {
        int width = getWidth();
        int height = getHeight();
        int[] pixels = new int[width * height];
        nativeGetPixels(mNativePtr, pixels);
        return pixels;
    }

    public void getPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
        int bmpWidth  = getWidth();
        int bmpHeight = getHeight();
        int[] all = new int[bmpWidth * bmpHeight];
        nativeGetPixels(mNativePtr, all);
        for (int row = 0; row < height; row++) {
            int srcIdx = (y + row) * bmpWidth + x;
            int dstIdx = offset + row * stride;
            System.arraycopy(all, srcIdx, pixels, dstIdx, width);
        }
    }

    public int getPixel(int x, int y) {
        int[] px = new int[1];
        getPixels(px, 0, 1, x, y, 1, 1);
        return px[0];
    }

    public void setPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
        nativeSetPixels(mNativePtr, pixels, offset, stride, x, y, width, height);
    }

    public boolean isMutable() { return true; }
    public boolean isRecycled() { return mNativePtr == 0L; }
    public int getDensity() { return 0; }
    public void setDensity(int density) {}
    public void eraseColor(int color) { nativeEraseColor(mNativePtr, color); }

    public static Bitmap createBitmap(Bitmap src) {
        return createBitmap(src, 0, 0, src.getWidth(), src.getHeight());
    }

    public static Bitmap createBitmap(Bitmap src, int x, int y, int width, int height) {
        Bitmap dst = createBitmap(width, height, src.getConfig());
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, x, y, width, height);
        dst.setPixels(pixels, 0, width, 0, 0, width, height);
        return dst;
    }

    public static Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter) {
        if (src.getWidth() == dstWidth && src.getHeight() == dstHeight) return src;
        return createBitmap(dstWidth, dstHeight, src.getConfig());
    }

    public static native long nativeCreate(int width, int height, boolean config, boolean mutable, long density);
    private native void nativeRecycle(long bitmapHandle);
    private native int nativeGetWidth(long bitmapHandle);
    private native int nativeGetHeight(long bitmapHandle);
    private native int nativeGetConfig(long bitmapHandle);
    private native void nativeGetPixels(long bitmapHandle, int[] pixels);
    private native void nativeSetPixels(long bitmapHandle, int[] pixels, int offset, int stride, int x, int y, int width, int height);
    private native void nativeEraseColor(long bitmapHandle, int color);
    private static native long nativeGetNativeFinalizer();
}
