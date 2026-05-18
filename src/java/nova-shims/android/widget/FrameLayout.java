package android.widget;

public class FrameLayout {
    public void addFocusables(java.util.ArrayList p0, int p1, int p2) {}
    public void addOnLayoutChangeListener(android.view.View.OnLayoutChangeListener p0) {}
    public void addView(android.view.View p0) {}
    public void addView(android.view.View p0, int p1) {}
    public void addView(android.view.View p0, int p1, android.view.ViewGroup.LayoutParams p2) {}
    public boolean addViewInLayout(android.view.View p0, int p1, android.view.ViewGroup.LayoutParams p2, boolean p3) { return false; }
    public android.view.ViewPropertyAnimator animate() { return null; }
    public void announceForAccessibility(java.lang.CharSequence p0) {}
    public void bringChildToFront(android.view.View p0) {}
    public void bringToFront() {}
    public void clearAnimation() {}
    public int computeHorizontalScrollExtent() { return 0; }
    public int computeHorizontalScrollOffset() { return 0; }
    public int computeHorizontalScrollRange() { return 0; }
    public int computeVerticalScrollExtent() { return 0; }
    public int computeVerticalScrollOffset() { return 0; }
    public void dispatchDraw(android.graphics.Canvas p0) {}
    public boolean dispatchKeyEvent(android.view.KeyEvent p0) { return false; }
    public boolean dispatchTouchEvent(android.view.MotionEvent p0) { return false; }
    public void draw(android.graphics.Canvas p0) {}
    public boolean drawChild(android.graphics.Canvas p0, android.view.View p1, long p2) { return false; }
    public void drawableStateChanged() {}
    public void endViewTransition(android.view.View p0) {}
    public android.view.View findFocus() { return null; }
    public android.view.View findViewById(int p0) { return null; }
    public boolean fitSystemWindows(android.graphics.Rect p0) { return false; }
    public android.view.View focusSearch(android.view.View p0, int p1) { return null; }
    public android.widget.FrameLayout.LayoutParams generateDefaultLayoutParams() { return null; }
    public android.view.animation.Animation getAnimation() { return null; }
    public android.view.autofill.AutofillId getAutofillId() { return null; }
    public android.graphics.drawable.Drawable getBackground() { return null; }
    public int getBottom() { return 0; }
    public android.view.View getChildAt(int p0) { return null; }
    public int getChildCount() { return 0; }
    public int getChildMeasureSpec(int p0, int p1, int p2) { return 0; }
    public boolean getClipToPadding() { return false; }
    public android.content.Context getContext() { return null; }
    public int getDescendantFocusability() { return 0; }
    public int[] getDrawableState() { return null; }
    public long getDrawingTime() { return 0; }
    public java.util.ArrayList getFocusables(int p0) { return null; }
    public android.graphics.drawable.Drawable getForeground() { return null; }
    public int getHeight() { return 0; }
    public void getHitRect(android.graphics.Rect p0) {}
    public int getId() { return 0; }
    public int getImportantForAutofill() { return 0; }
    public int getLayoutDirection() { return 0; }
    public android.view.ViewGroup.LayoutParams getLayoutParams() { return null; }
    public void getLocationInWindow(int[] p0) {}
    public void getLocationOnScreen(int[] p0) {}
    public boolean getMeasureAllChildren() { return false; }
    public int getMeasuredHeight() { return 0; }
    public int getMeasuredState() { return 0; }
    public int getMeasuredWidth() { return 0; }
    public int getOverScrollMode() { return 0; }
    public int getPaddingBottom() { return 0; }
    public int getPaddingLeft() { return 0; }
    public int getPaddingRight() { return 0; }
    public int getPaddingTop() { return 0; }
    public android.view.ViewParent getParent() { return null; }
    public android.content.res.Resources getResources() { return null; }
    public android.view.AttachedSurfaceControl getRootSurfaceControl() { return null; }
    public android.view.WindowInsets getRootWindowInsets() { return null; }
    public int getScrollX() { return 0; }
    public int getScrollY() { return 0; }
    public java.lang.Object getTag() { return null; }
    public int getTop() { return 0; }
    public float getTranslationY() { return 0f; }
    public int getVerticalFadingEdgeLength() { return 0; }
    public android.view.ViewTreeObserver getViewTreeObserver() { return null; }
    public int getVisibility() { return 0; }
    public int getWidth() { return 0; }
    public android.view.View inflate(android.content.Context p0, int p1, android.view.ViewGroup p2) { return null; }
    public void invalidate() {}
    public void invalidate(android.graphics.Rect p0) {}
    public void invalidateOutline() {}
    public boolean isClickable() { return false; }
    public boolean isEnabled() { return false; }
    public boolean isFocused() { return false; }
    public boolean isInEditMode() { return false; }
    public boolean isLaidOut() { return false; }
    public boolean isOpaque() { return false; }
    public boolean isSelected() { return false; }
    public void jumpDrawablesToCurrentState() {}
    public void layout(int p0, int p1, int p2, int p3) {}
    public void measure(int p0, int p1) {}
    public int[] mergeDrawableStates(int[] p0, int[] p1) { return null; }
    public void offsetDescendantRectToMyCoords(android.view.View p0, android.graphics.Rect p1) {}
    public void onAttachedToWindow() {}
    public void onConfigurationChanged(android.content.res.Configuration p0) {}
    public int[] onCreateDrawableState(int p0) { return null; }
    public void onDetachedFromWindow() {}
    public void onDraw(android.graphics.Canvas p0) {}
    public void onFinishInflate() {}
    public void onFocusChanged(boolean p0, int p1, android.graphics.Rect p2) {}
    public boolean onGenericMotionEvent(android.view.MotionEvent p0) { return false; }
    public boolean onHoverEvent(android.view.MotionEvent p0) { return false; }
    public void onInitializeAccessibilityEvent(android.view.accessibility.AccessibilityEvent p0) {}
    public void onInitializeAccessibilityNodeInfo(android.view.accessibility.AccessibilityNodeInfo p0) {}
    public boolean onInterceptTouchEvent(android.view.MotionEvent p0) { return false; }
    public boolean onKeyDown(int p0, android.view.KeyEvent p1) { return false; }
    public boolean onKeyLongPress(int p0, android.view.KeyEvent p1) { return false; }
    public boolean onKeyUp(int p0, android.view.KeyEvent p1) { return false; }
    public void onLayout(boolean p0, int p1, int p2, int p3, int p4) {}
    public void onMeasure(int p0, int p1) {}
    public void onProvideAutofillStructure(android.view.ViewStructure p0, int p1) {}
    public boolean onRequestFocusInDescendants(int p0, android.graphics.Rect p1) { return false; }
    public void onRestoreInstanceState(android.os.Parcelable p0) {}
    public android.os.Parcelable onSaveInstanceState() { return null; }
    public void onScrollChanged(int p0, int p1, int p2, int p3) {}
    public void onSizeChanged(int p0, int p1, int p2, int p3) {}
    public boolean onTouchEvent(android.view.MotionEvent p0) { return false; }
    public boolean onTrackballEvent(android.view.MotionEvent p0) { return false; }
    public void onViewAdded(android.view.View p0) {}
    public void onViewRemoved(android.view.View p0) {}
    public void onVisibilityChanged(android.view.View p0, int p1) {}
    public void onWindowFocusChanged(boolean p0) {}
    public void onWindowVisibilityChanged(int p0) {}
    public boolean performClick() { return false; }
    public boolean post(java.lang.Runnable p0) { return false; }
    public void refreshDrawableState() {}
    public void removeAllViews() {}
    public void removeAllViewsInLayout() {}
    public boolean removeCallbacks(java.lang.Runnable p0) { return false; }
    public void removeDetachedView(android.view.View p0, boolean p1) {}
    public void removeOnLayoutChangeListener(android.view.View.OnLayoutChangeListener p0) {}
    public void removeView(android.view.View p0) {}
    public void removeViewAt(int p0) {}
    public void removeViewInLayout(android.view.View p0) {}
    public void removeViews(int p0, int p1) {}
    public void removeViewsInLayout(int p0, int p1) {}
    public void requestChildFocus(android.view.View p0, android.view.View p1) {}
    public void requestDisallowInterceptTouchEvent(boolean p0) {}
    public boolean requestFocus() { return false; }
    public boolean requestFocus(int p0, android.graphics.Rect p1) { return false; }
    public void requestLayout() {}
    public int resolveSizeAndState(int p0, int p1, int p2) { return 0; }
    public void scrollBy(int p0, int p1) {}
    public void scrollTo(int p0, int p1) {}
    public void setAccessibilityDelegate(android.view.View.AccessibilityDelegate p0) {}
    public void setActivated(boolean p0) {}
    public void setAddStatesFromChildren(boolean p0) {}
    public void setAlpha(float p0) {}
    public void setBackground(android.graphics.drawable.Drawable p0) {}
    public void setBackgroundColor(int p0) {}
    public void setBackgroundDrawable(android.graphics.drawable.Drawable p0) {}
    public void setBackgroundResource(int p0) {}
    public void setClickable(boolean p0) {}
    public void setClipChildren(boolean p0) {}
    public void setClipToOutline(boolean p0) {}
    public void setClipToPadding(boolean p0) {}
    public void setContentDescription(java.lang.CharSequence p0) {}
    public void setDefaultFocusHighlightEnabled(boolean p0) {}
    public void setDescendantFocusability(int p0) {}
    public void setElevation(float p0) {}
    public void setEnabled(boolean p0) {}
    public void setFitsSystemWindows(boolean p0) {}
    public void setFocusable(boolean p0) {}
    public void setFocusableInTouchMode(boolean p0) {}
    public void setForeground(android.graphics.drawable.Drawable p0) {}
    public void setForegroundGravity(int p0) {}
    public void setId(int p0) {}
    public void setImportantForAutofill(int p0) {}
    public void setLayoutParams(android.view.ViewGroup.LayoutParams p0) {}
    public void setLayoutTransition(android.animation.LayoutTransition p0) {}
    public void setLongClickable(boolean p0) {}
    public void setMeasuredDimension(int p0, int p1) {}
    public void setMinimumHeight(int p0) {}
    public void setMinimumWidth(int p0) {}
    public void setOnClickListener(android.view.View.OnClickListener p0) {}
    public void setOnLongClickListener(android.view.View.OnLongClickListener p0) {}
    public void setOnTouchListener(android.view.View.OnTouchListener p0) {}
    public void setOutlineAmbientShadowColor(int p0) {}
    public void setOutlineProvider(android.view.ViewOutlineProvider p0) {}
    public void setOutlineSpotShadowColor(int p0) {}
    public void setOverScrollMode(int p0) {}
    public void setPadding(int p0, int p1, int p2, int p3) {}
    public void setSaveEnabled(boolean p0) {}
    public void setScaleX(float p0) {}
    public void setScaleY(float p0) {}
    public void setSelected(boolean p0) {}
    public void setTag(java.lang.Object p0) {}
    public void setTranslationX(float p0) {}
    public void setTranslationY(float p0) {}
    public void setVisibility(int p0) {}
    public void setWillNotDraw(boolean p0) {}
    public android.view.ActionMode startActionModeForChild(android.view.View p0, android.view.ActionMode.Callback p1, int p2) { return null; }
    public void startAnimation(android.view.animation.Animation p0) {}
    public void startViewTransition(android.view.View p0) {}
    public java.lang.String toString() { return null; }
    public void unscheduleDrawable(android.graphics.drawable.Drawable p0) {}
    public boolean verifyDrawable(android.graphics.drawable.Drawable p0) { return false; }

    public static class LayoutParams {
        public void setMargins(int p0, int p1, int p2, int p3) {}
    }
}
