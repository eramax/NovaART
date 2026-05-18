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

    public void setSystemUiVisibility(int visibility) {}
    public int getSystemUiVisibility() { return 0; }
    public void setKeepScreenOn(boolean keepScreenOn) {}
    public void setLayerType(int layerType, android.graphics.Paint paint) {}
    public void setBackgroundColor(int color) {}
    public void setFocusable(boolean focusable) {}
    public void setFocusableInTouchMode(boolean focusableInTouchMode) {}
    public boolean requestFocus() { return true; }
    public void invalidate() {}
    public void postInvalidate() {}
    public void setVisibility(int visibility) {}
    public int getVisibility() { return 0; }
    public int getId() { return 0; }
    public void setId(int id) {}
    public android.graphics.drawable.Drawable getBackground() { return null; }
    public void setBackground(android.graphics.drawable.Drawable background) {}
    public void setBackgroundDrawable(android.graphics.drawable.Drawable d) {}
    public int getLeft() { return 0; }
    public int getTop() { return 0; }
    public int getRight() { return 960; }
    public int getBottom() { return 540; }
    public int getMeasuredWidth() { return 960; }
    public int getMeasuredHeight() { return 540; }
    public void measure(int widthMeasureSpec, int heightMeasureSpec) {}
    public void layout(int l, int t, int r, int b) {}
}
