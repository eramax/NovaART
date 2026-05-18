package android.view;

public interface SurfaceHolder {
    void addCallback(Callback callback);
    void setFormat(int format);
    Surface getSurface();

    interface Callback {
        void surfaceCreated(SurfaceHolder holder);
        void surfaceDestroyed(SurfaceHolder holder);
        void surfaceChanged(SurfaceHolder holder, int format, int width, int height);
    }

    interface Callback2 extends Callback {
        void surfaceRedrawNeeded(SurfaceHolder holder);
        void surfaceRedrawNeededAsync(SurfaceHolder holder, Runnable finishDrawing);
    }
}
