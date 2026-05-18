package nova.internal;

import android.view.View;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.util.Log;

public final class ViewDispatcher {
    private static final String TAG = "ViewDispatcher";
    private static View sRootView;

    private ViewDispatcher() {}

    public static void setRootView(View view) {
        sRootView = view;
        Log.d(TAG, "Root view set: " + (view != null ? view.getClass().getName() : "null"));
    }

    public static void dispatchMotionEvent(long eventTime, int action, float x, float y) {
        if (sRootView == null) {
            Log.w(TAG, "dispatchMotionEvent called but no root view set");
            return;
        }
        MotionEvent event = MotionEvent.obtain(eventTime, action, x, y);
        try {
            sRootView.dispatchMotionEvent(event);
        } finally {
            event.recycle();
        }
    }

    public static void dispatchKeyEvent(int action, int keyCode, long eventTime, int metaState) {
        if (sRootView == null) {
            Log.w(TAG, "dispatchKeyEvent called but no root view set");
            return;
        }
        KeyEvent event = KeyEvent.obtain(action, keyCode, eventTime, metaState);
        try {
            sRootView.dispatchKeyEvent(event);
        } finally {
        }
    }
}
