package android.graphics;

public class Paint {
    public enum Style {
        FILL(0),
        STROKE(1),
        FILL_AND_STROKE(2);

        final int nativeInt;

        Style(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    private long mNativePaint;

    public Paint() {
        mNativePaint = native_init();
    }

    public Paint(Paint paint) {
        mNativePaint = native_initWithPaint(paint != null ? paint.mNativePaint : 0L);
    }

    long getNativeInstance() {
        return mNativePaint;
    }

    public void setAntiAlias(boolean aa) {
        native_setAntiAlias(mNativePaint, aa);
    }

    public void setColor(int color) {
        native_setColor(mNativePaint, color);
    }

    public void setStrokeWidth(float width) {
        native_setStrokeWidth(mNativePaint, width);
    }

    public void setStyle(Style style) {
        native_setStyle(mNativePaint, style != null ? style.nativeInt : Style.FILL.nativeInt);
    }

    private native void native_setAntiAlias(long paintHandle, boolean aa);
    private native void native_setColor(long paintHandle, int color);
    private native void native_setStrokeWidth(long paintHandle, float width);
    private native void native_setStyle(long paintHandle, int style);
    private static native long native_getNativeFinalizer();
    private native long native_init();
    private native long native_initWithPaint(long paintHandle);
}
