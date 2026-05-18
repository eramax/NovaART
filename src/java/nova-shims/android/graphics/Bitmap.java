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

    public static Bitmap createBitmap(int width, int height, Config config) {
        Config resolvedConfig = config != null ? config : Config.ARGB_8888;
        long nativePtr = nativeCreate(width, height, resolvedConfig == Config.ARGB_8888, true, 0L);
        if (nativePtr == 0L) {
            throw new OutOfMemoryError("Bitmap allocation failed");
        }
        return new Bitmap(nativePtr);
    }

    long getNativeInstance() {
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

    public static native long nativeCreate(int width, int height, boolean config, boolean mutable, long density);
    private native void nativeRecycle(long bitmapHandle);
    private native int nativeGetWidth(long bitmapHandle);
    private native int nativeGetHeight(long bitmapHandle);
    private native int nativeGetConfig(long bitmapHandle);
    private static native long nativeGetNativeFinalizer();
}
