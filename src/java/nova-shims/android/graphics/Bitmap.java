package android.graphics;

public class Bitmap {
    private long mNativePtr;

    private Bitmap(long nativePtr) {
        mNativePtr = nativePtr;
    }

    public static native long nativeCreate(int width, int height, boolean config, boolean mutable, long density);
    private native void nativeRecycle(long bitmapHandle);
    private native int nativeGetWidth(long bitmapHandle);
    private native int nativeGetHeight(long bitmapHandle);
    private native int nativeGetConfig(long bitmapHandle);
    private static native long nativeGetNativeFinalizer();
}
