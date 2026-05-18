package android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public class View {
    private static final String TAG = "NovaView";
    private final Context mContext;
    private boolean mAttached;

    public View(Context context) {
        mContext = context;
    }

    public View(Context context, AttributeSet attrs) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public final void novaAttachToWindow() {
        if (mAttached) {
            return;
        }
        mAttached = true;
        Log.d(TAG, "attach " + getClass().getName());
        onAttachedToWindow();
    }

    public final void novaDetachFromWindow() {
        if (!mAttached) {
            return;
        }
        Log.d(TAG, "detach " + getClass().getName());
        onDetachedFromWindow();
        mAttached = false;
    }

    protected void onAttachedToWindow() {
    }

    protected void onDetachedFromWindow() {
    }
}
