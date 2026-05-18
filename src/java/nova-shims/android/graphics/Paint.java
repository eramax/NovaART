package android.graphics;

public class Paint {
    private long mNativePaint;

    public Paint() {
        mNativePaint = native_init();
    }

    public Paint(Paint paint) {
        mNativePaint = native_initWithPaint(paint != null ? paint.mNativePaint : 0L);
    }

    private native void native_setAntiAlias(long paintHandle, boolean aa);
    private native void native_setColor(long paintHandle, int color);
    private native void native_setStrokeWidth(long paintHandle, float width);
    private native void native_setStyle(long paintHandle, int style);
    private static native long native_getNativeFinalizer();
    private native long native_init();
    private native long native_initWithPaint(long paintHandle);
}
