package android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.Canvas;

public class View {
    private static final String TAG = "NovaView";
    private final Context mContext;
    private final ViewTreeObserver mViewTreeObserver = new ViewTreeObserver();
    private boolean mAttached;

    public interface OnTouchListener {
        boolean onTouch(View v, MotionEvent event);
    }

    private OnTouchListener mOnTouchListener;

    public View(Context context) {
        mContext = context;
    }

    public View(Context context, AttributeSet attrs) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public ViewTreeObserver getViewTreeObserver() {
        return mViewTreeObserver;
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

    public boolean dispatchMotionEvent(MotionEvent event) {
        return onMotionEvent(event);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return onKeyEvent(event);
    }

    protected boolean onMotionEvent(MotionEvent event) {
        return false;
    }

    protected boolean onKeyEvent(KeyEvent event) {
        return false;
    }

    public final void draw(Canvas canvas) {
        onDraw(canvas);
    }

    protected void onDraw(Canvas canvas) {
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mOnTouchListener = listener;
    }
}
