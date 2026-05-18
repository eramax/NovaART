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

    public void drawColor(int color) {
        native_drawColor(mNativeCanvasWrapper, color);
    }

    public void drawText(String text, float x, float y, Paint paint) {
        if (text != null) {
            native_drawText(mNativeCanvasWrapper, text, x, y,
                    paint != null ? paint.getNativeInstance() : 0L);
        }
    }

    public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
        if (bitmap != null) {
            native_drawBitmap(mNativeCanvasWrapper, bitmap.getNativeInstance(), left, top,
                    paint != null ? paint.getNativeInstance() : 0L);
        }
    }

    public void drawCircle(float cx, float cy, float radius, Paint paint) {
        native_drawCircle(mNativeCanvasWrapper, cx, cy, radius,
                paint != null ? paint.getNativeInstance() : 0L);
    }

    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
        native_drawLine(mNativeCanvasWrapper, startX, startY, stopX, stopY,
                paint != null ? paint.getNativeInstance() : 0L);
    }

    private native long initRaster(long bitmapHandle);
    private native void native_setBitmap(long canvasHandle, long bitmapHandle);
    private native void native_drawRect(long canvasHandle, float left, float top, float right, float bottom, long paintHandle);
    private native void native_drawColor(long canvasHandle, int color);
    private native void native_drawText(long canvasHandle, String text, float x, float y, long paintHandle);
    private native void native_drawBitmap(long canvasHandle, long bitmapHandle, float left, float top, long paintHandle);
    private native void native_drawCircle(long canvasHandle, float cx, float cy, float radius, long paintHandle);
    private native void native_drawLine(long canvasHandle, float startX, float startY, float stopX, float stopY, long paintHandle);
    private native int native_save(long canvasHandle, int saveFlags);
    private native void native_restore(long canvasHandle);
    private native int native_getWidth(long canvasHandle);
    private native int native_getHeight(long canvasHandle);
}
