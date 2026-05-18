package nova.internal;

import android.graphics.Bitmap;

public final class CanvasRender {
    private CanvasRender() {}

    public static native void notifyVsync(long frameTimeNanos);

    public static native void dispatchMotionEvent(long eventTime, int action, float x, float y);

    public static native void dispatchKeyEvent(int action, int keyCode, long eventTime, int metaState);

    public static native void submitFrame(Bitmap bitmap);
}
