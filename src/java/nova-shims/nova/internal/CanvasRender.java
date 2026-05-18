package nova.internal;

import android.graphics.Bitmap;

public final class CanvasRender {
    private CanvasRender() {}

    public static native void notifyVsync(long frameTimeNanos);

    public static native void dispatchMotionEvent(long eventTime, int action, float x, float y);

    public static native void dispatchKeyEvent(int action, int keyCode, long eventTime, int metaState);

    public static native void submitFrame(Bitmap bitmap);

    public static native void initRender(long state, long window);

    public static native long getRenderState();

    public static native long getRenderWindow();

    public static native void setRenderState(long state);

    public static native void setRenderWindow(long window);

    public static native void cleanupRender();
}
