package android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class SurfaceView extends View {
    private static final String TAG = "NovaSurfaceView";
    private final SurfaceHolder mHolder = new SimpleSurfaceHolder();

    public SurfaceView(Context context) {
        super(context);
    }

    public SurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SurfaceHolder getHolder() {
        return mHolder;
    }

    public void novaSimulateSurfaceLifecycle(int width, int height) {
        Log.d(TAG, "simulate lifecycle " + width + "x" + height + " for " + getClass().getName());
        if (mHolder instanceof SimpleSurfaceHolder) {
            SimpleSurfaceHolder holder = (SimpleSurfaceHolder) mHolder;
            holder.dispatchCreated();
            holder.dispatchChanged(0, width, height);
        }
    }

    private static final class SimpleSurfaceHolder implements SurfaceHolder {
        private final List<Callback> callbacks = new ArrayList<>();
        private final Surface surface = new Surface();
        private int mWidth = 960, mHeight = 540;

        @Override public void addCallback(Callback callback) { if (callback != null) callbacks.add(callback); }
        @Override public void removeCallback(Callback callback) { callbacks.remove(callback); }
        @Override public boolean isCreating() { return false; }
        @Override public void setType(int type) {}
        @Override public void setFixedSize(int width, int height) { mWidth = width; mHeight = height; }
        @Override public void setSizeFromLayout() {}
        @Override public void setFormat(int format) {}
        @Override public void setKeepScreenOn(boolean screenOn) {}
        @Override public android.graphics.Canvas lockCanvas() { return new android.graphics.Canvas(); }
        @Override public android.graphics.Canvas lockCanvas(android.graphics.Rect dirty) { return new android.graphics.Canvas(); }
        @Override public android.graphics.Canvas lockHardwareCanvas() { return new android.graphics.Canvas(); }
        @Override public void unlockCanvasAndPost(android.graphics.Canvas canvas) {}
        @Override public android.graphics.Rect getSurfaceFrame() { return new android.graphics.Rect(0, 0, mWidth, mHeight); }
        @Override public Surface getSurface() { return surface; }

        void dispatchCreated() {
            for (Callback cb : new ArrayList<>(callbacks)) cb.surfaceCreated(this);
        }

        void dispatchChanged(int format, int width, int height) {
            mWidth = width; mHeight = height;
            for (Callback cb : new ArrayList<>(callbacks)) cb.surfaceChanged(this, format, width, height);
        }
    }
}
