package android.view;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public final class Choreographer {
    private static final String TAG = "Choreographer";
    private static final Choreographer sInstance = new Choreographer();
    private final List<FrameCallback> mFrameCallbacks = new ArrayList<>();
    private final List<Runnable> mCommitCallbacks = new ArrayList<>();

    public interface FrameCallback {
        void doFrame(long frameTimeNanos);
    }

    public static Choreographer getInstance() {
        return sInstance;
    }

    public void postFrameCallback(FrameCallback callback) {
        if (callback != null) {
            synchronized (mFrameCallbacks) {
                mFrameCallbacks.add(callback);
            }
        }
    }

    public void postFrameCallbackDelayed(FrameCallback callback, long delayMillis) {
        if (callback != null) {
            synchronized (mFrameCallbacks) {
                mFrameCallbacks.add(callback);
            }
        }
    }

    public void postRunnable(Runnable action) {
        if (action != null) {
            synchronized (mCommitCallbacks) {
                mCommitCallbacks.add(action);
            }
        }
    }

    public void removeFrameCallback(FrameCallback callback) {
        if (callback != null) {
            synchronized (mFrameCallbacks) {
                mFrameCallbacks.remove(callback);
            }
        }
    }

    // Called from JNI when a vsync happens
    public static void notifyFrameTime(long frameTimeNanos) {
        Choreographer instance = getInstance();
        List<FrameCallback> callbacks;
        synchronized (instance.mFrameCallbacks) {
            callbacks = new ArrayList<>(instance.mFrameCallbacks);
            instance.mFrameCallbacks.clear();
        }
        for (FrameCallback cb : callbacks) {
            try {
                cb.doFrame(frameTimeNanos);
            } catch (Exception e) {
                Log.e(TAG, "Error in frame callback", e);
            }
        }

        List<Runnable> runnables;
        synchronized (instance.mCommitCallbacks) {
            runnables = new ArrayList<>(instance.mCommitCallbacks);
            instance.mCommitCallbacks.clear();
        }
        for (Runnable r : runnables) {
            try {
                r.run();
            } catch (Exception e) {
                Log.e(TAG, "Error in runnable", e);
            }
        }
    }
}
