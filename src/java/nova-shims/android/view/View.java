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

    public interface OnClickListener { void onClick(View v); }
    public interface OnLongClickListener { boolean onLongClick(View v); }
    public interface OnCreateContextMenuListener { void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo); }
    public interface OnKeyListener { boolean onKey(View v, int keyCode, KeyEvent event); }
    public interface OnFocusChangeListener { void onFocusChange(View v, boolean hasFocus); }
    public interface OnScrollChangeListener { void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY); }
    public interface OnLayoutChangeListener { void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom); }
    public interface OnAttachStateChangeListener {
        void onViewAttachedToWindow(View v);
        void onViewDetachedFromWindow(View v);
    }

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

    public void setOnClickListener(OnClickListener listener) {}
    public void setOnLongClickListener(OnLongClickListener listener) {}
    public void setOnCreateContextMenuListener(OnCreateContextMenuListener listener) {}
    public void setOnKeyListener(OnKeyListener listener) {}
    public void setOnFocusChangeListener(OnFocusChangeListener listener) {}
    public void addOnAttachStateChangeListener(OnAttachStateChangeListener listener) {}
    public void removeOnAttachStateChangeListener(OnAttachStateChangeListener listener) {}
    public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {}
    public void removeOnLayoutChangeListener(OnLayoutChangeListener listener) {}

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
    public ViewPropertyAnimator animate() { return new ViewPropertyAnimator(this); }
    public float getAlpha() { return 1f; }
    public void setAlpha(float alpha) {}
    public float getTranslationX() { return 0f; }
    public float getTranslationY() { return 0f; }
    public void setTranslationX(float x) {}
    public void setTranslationY(float y) {}
    public float getScaleX() { return 1f; }
    public float getScaleY() { return 1f; }
    public void setScaleX(float x) {}
    public void setScaleY(float y) {}
    public float getRotation() { return 0f; }
    public void setRotation(float r) {}
    public float getX() { return 0f; }
    public float getY() { return 0f; }
    public void setX(float x) {}
    public void setY(float y) {}
    public int getWidth() { return getMeasuredWidth(); }
    public int getHeight() { return getMeasuredHeight(); }
    public android.animation.StateListAnimator getStateListAnimator() { return null; }
    public void setStateListAnimator(android.animation.StateListAnimator animator) {}
    public void setPadding(int left, int top, int right, int bottom) {}
    public int getPaddingLeft() { return 0; }
    public int getPaddingTop() { return 0; }
    public int getPaddingRight() { return 0; }
    public int getPaddingBottom() { return 0; }
    public void setElevation(float elevation) {}
    public float getElevation() { return 0f; }
    public void setTag(Object tag) {}
    public Object getTag() { return null; }
    public void setTag(int key, Object tag) {}
    public Object getTag(int key) { return null; }
    public boolean isAttachedToWindow() { return mAttached; }
    public android.view.ViewParent getParent() { return null; }
    public android.content.res.Resources getResources() {
        return mContext != null ? mContext.getResources() : null;
    }
    public void requestLayout() {}
    public void forceLayout() {}
    public boolean isInLayout() { return false; }
    public void setMinimumWidth(int minWidth) {}
    public void setMinimumHeight(int minHeight) {}
    public void setClickable(boolean clickable) {}
    public boolean isClickable() { return false; }
    public void setLongClickable(boolean longClickable) {}
    public void setSelected(boolean selected) {}
    public boolean isSelected() { return false; }
    public void setEnabled(boolean enabled) {}
    public boolean isEnabled() { return true; }
    public boolean isFocused() { return false; }
    public void bringToFront() {}
    public int[] getDrawableState() { return new int[0]; }
    public void refreshDrawableState() {}
    public void scheduleDrawable(android.graphics.drawable.Drawable who, Runnable what, long when) {}
    public void unscheduleDrawable(android.graphics.drawable.Drawable who, Runnable what) {}
    public void unscheduleDrawable(android.graphics.drawable.Drawable who) {}
    public boolean verifyDrawable(android.graphics.drawable.Drawable who) { return false; }
    public void drawableStateChanged() {}
    public void jumpDrawablesToCurrentState() {}
    public void post(Runnable action) {
        new android.os.Handler().post(action);
    }
    public void postDelayed(Runnable action, long delayMillis) {
        new android.os.Handler().postDelayed(action, delayMillis);
    }
    public void removeCallbacks(Runnable action) {}
}
