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

        @Override
        public void addCallback(Callback callback) {
            if (callback != null) {
                callbacks.add(callback);
            }
        }

        @Override
        public void setFormat(int format) {
        }

        @Override
        public Surface getSurface() {
            return surface;
        }

        void dispatchCreated() {
            Log.d(TAG, "dispatch surfaceCreated callbacks=" + callbacks.size());
            for (Callback callback : callbacks) {
                callback.surfaceCreated(this);
            }
        }

        void dispatchChanged(int format, int width, int height) {
            Log.d(TAG, "dispatch surfaceChanged callbacks=" + callbacks.size()
                    + " size=" + width + "x" + height);
            for (Callback callback : callbacks) {
                callback.surfaceChanged(this, format, width, height);
            }
        }
    }
}
