package android.view;

import android.content.Context;
import android.util.AttributeSet;
import java.util.ArrayList;
import java.util.List;

public class SurfaceView extends View {
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
            for (Callback callback : callbacks) {
                callback.surfaceCreated(this);
            }
        }

        void dispatchChanged(int format, int width, int height) {
            for (Callback callback : callbacks) {
                callback.surfaceChanged(this, format, width, height);
            }
        }
    }
}
