package android.graphics;

public class Canvas {
    public static final int ALL_SAVE_FLAG = 31;

    private long mNativeCanvasWrapper;

    public Canvas() {}

    public Canvas(Bitmap bitmap) {
        setBitmap(bitmap);
    }

    public void setBitmap(Bitmap bitmap) {
        long bitmapHandle = bitmap != null ? bitmap.getNativeInstance() : 0L;
        if (mNativeCanvasWrapper == 0L) {
            mNativeCanvasWrapper = initRaster(bitmapHandle);
        } else {
            native_setBitmap(mNativeCanvasWrapper, bitmapHandle);
        }
    }

    public void drawRect(float left, float top, float right, float bottom, Paint paint) {
        native_drawRect(mNativeCanvasWrapper, left, top, right, bottom,
                paint != null ? paint.getNativeInstance() : 0L);
    }

    public int save() {
        return native_save(mNativeCanvasWrapper, ALL_SAVE_FLAG);
    }

    public void restore() {
        native_restore(mNativeCanvasWrapper);
    }

    public int getWidth() {
        return native_getWidth(mNativeCanvasWrapper);
    }

    public int getHeight() {
        return native_getHeight(mNativeCanvasWrapper);
    }

    private native long initRaster(long bitmapHandle);
    private native void native_setBitmap(long canvasHandle, long bitmapHandle);
    private native void native_drawRect(long canvasHandle, float left, float top, float right, float bottom, long paintHandle);
    private native int native_save(long canvasHandle, int saveFlags);
    private native void native_restore(long canvasHandle);
    private native int native_getWidth(long canvasHandle);
    private native int native_getHeight(long canvasHandle);
}
