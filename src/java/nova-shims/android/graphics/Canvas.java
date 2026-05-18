package android.graphics;

public class Canvas {
    private long mNativeCanvasWrapper;

    public Canvas() {}

    private native long initRaster(long bitmapHandle);
    private native void native_setBitmap(long canvasHandle, long bitmapHandle);
    private native void native_drawRect(long canvasHandle, float left, float top, float right, float bottom, long paintHandle);
    private native int native_save(long canvasHandle, int saveFlags);
    private native void native_restore(long canvasHandle);
    private native int native_getWidth(long canvasHandle);
    private native int native_getHeight(long canvasHandle);
}
