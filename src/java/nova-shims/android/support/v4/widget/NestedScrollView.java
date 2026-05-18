package android.support.v4.widget;

public class NestedScrollView {
    public int access$000(android.support.v4.widget.NestedScrollView p0) { return 0; }
    public void addView(android.view.View p0) {}
    public void addView(android.view.View p0, int p1) {}
    public void addView(android.view.View p0, int p1, android.view.ViewGroup.LayoutParams p2) {}
    public boolean arrowScroll(int p0) { return false; }
    public boolean canScroll() { return false; }
    public int clamp(int p0, int p1, int p2) { return 0; }
    public int computeHorizontalScrollExtent() { return 0; }
    public int computeHorizontalScrollOffset() { return 0; }
    public int computeHorizontalScrollRange() { return 0; }
    public void computeScroll() {}
    public int computeScrollDeltaToGetChildRectOnScreen(android.graphics.Rect p0) { return 0; }
    public int computeVerticalScrollExtent() { return 0; }
    public int computeVerticalScrollOffset() { return 0; }
    public int computeVerticalScrollRange() { return 0; }
    public boolean dispatchKeyEvent(android.view.KeyEvent p0) { return false; }
    public boolean dispatchNestedFling(float p0, float p1, boolean p2) { return false; }
    public boolean dispatchNestedPreFling(float p0, float p1) { return false; }
    public boolean dispatchNestedPreScroll(int p0, int p1, int[] p2, int[] p3) { return false; }
    public boolean dispatchNestedScroll(int p0, int p1, int p2, int p3, int[] p4) { return false; }
    public void doScrollY(int p0) {}
    public void draw(android.graphics.Canvas p0) {}
    public void endDrag() {}
    public void ensureGlows() {}
    public boolean executeKeyEvent(android.view.KeyEvent p0) { return false; }
    public android.view.View findFocus() { return null; }
    public android.view.View findFocusableViewInBounds(boolean p0, int p1, int p2) { return null; }
    public void fling(int p0) {}
    public void flingWithNestedDispatch(int p0) {}
    public boolean fullScroll(int p0) { return false; }
    public float getBottomFadingEdgeStrength() { return 0f; }
    public android.view.View getChildAt(int p0) { return null; }
    public int getChildCount() { return 0; }
    public int getChildMeasureSpec(int p0, int p1, int p2) { return 0; }
    public android.content.Context getContext() { return null; }
    public int getDescendantFocusability() { return 0; }
    public java.util.ArrayList getFocusables(int p0) { return null; }
    public int getHeight() { return 0; }
    public int getMaxScrollAmount() { return 0; }
    public int getMeasuredHeight() { return 0; }
    public int getNestedScrollAxes() { return 0; }
    public int getPaddingBottom() { return 0; }
    public int getPaddingLeft() { return 0; }
    public int getPaddingRight() { return 0; }
    public int getPaddingTop() { return 0; }
    public android.view.ViewParent getParent() { return null; }
    public int getScrollRange() { return 0; }
    public int getScrollX() { return 0; }
    public int getScrollY() { return 0; }
    public float getTopFadingEdgeStrength() { return 0f; }
    public int getVerticalFadingEdgeLength() { return 0; }
    public float getVerticalScrollFactorCompat() { return 0f; }
    public int getWidth() { return 0; }
    public boolean hasNestedScrollingParent() { return false; }
    public boolean inChild(int p0, int p1) { return false; }
    public void initOrResetVelocityTracker() {}
    public void initScrollView() {}
    public void initVelocityTrackerIfNotExists() {}
    public boolean isEnabled() { return false; }
    public boolean isFillViewport() { return false; }
    public boolean isFocused() { return false; }
    public boolean isNestedScrollingEnabled() { return false; }
    public boolean isOffScreen(android.view.View p0) { return false; }
    public boolean isSmoothScrollingEnabled() { return false; }
    public boolean isViewDescendantOf(android.view.View p0, android.view.View p1) { return false; }
    public boolean isWithinDeltaOfScreen(android.view.View p0, int p1, int p2) { return false; }
    public void measureChild(android.view.View p0, int p1, int p2) {}
    public void measureChildWithMargins(android.view.View p0, int p1, int p2, int p3, int p4) {}
    public void offsetDescendantRectToMyCoords(android.view.View p0, android.graphics.Rect p1) {}
    public void onAttachedToWindow() {}
    public boolean onGenericMotionEvent(android.view.MotionEvent p0) { return false; }
    public boolean onInterceptTouchEvent(android.view.MotionEvent p0) { return false; }
    public void onLayout(boolean p0, int p1, int p2, int p3, int p4) {}
    public void onMeasure(int p0, int p1) {}
    public boolean onNestedFling(android.view.View p0, float p1, float p2, boolean p3) { return false; }
    public boolean onNestedPreFling(android.view.View p0, float p1, float p2) { return false; }
    public void onNestedPreScroll(android.view.View p0, int p1, int p2, int[] p3) {}
    public void onNestedScroll(android.view.View p0, int p1, int p2, int p3, int p4) {}
    public void onNestedScrollAccepted(android.view.View p0, android.view.View p1, int p2) {}
    public void onOverScrolled(int p0, int p1, boolean p2, boolean p3) {}
    public boolean onRequestFocusInDescendants(int p0, android.graphics.Rect p1) { return false; }
    public void onRestoreInstanceState(android.os.Parcelable p0) {}
    public android.os.Parcelable onSaveInstanceState() { return null; }
    public void onScrollChanged(int p0, int p1, int p2, int p3) {}
    public void onSecondaryPointerUp(android.view.MotionEvent p0) {}
    public void onSizeChanged(int p0, int p1, int p2, int p3) {}
    public boolean onStartNestedScroll(android.view.View p0, android.view.View p1, int p2) { return false; }
    public void onStopNestedScroll(android.view.View p0) {}
    public boolean onTouchEvent(android.view.MotionEvent p0) { return false; }
    public boolean overScrollByCompat(int p0, int p1, int p2, int p3, int p4, int p5, int p6, int p7, boolean p8) { return false; }
    public boolean pageScroll(int p0) { return false; }
    public void recycleVelocityTracker() {}
    public void requestChildFocus(android.view.View p0, android.view.View p1) {}
    public boolean requestChildRectangleOnScreen(android.view.View p0, android.graphics.Rect p1, boolean p2) { return false; }
    public void requestDisallowInterceptTouchEvent(boolean p0) {}
    public boolean requestFocus() { return false; }
    public void requestLayout() {}
    public boolean scrollAndFocus(int p0, int p1, int p2) { return false; }
    public void scrollBy(int p0, int p1) {}
    public void scrollTo(int p0, int p1) {}
    public void scrollToChild(android.view.View p0) {}
    public boolean scrollToChildRect(android.graphics.Rect p0, boolean p1) { return false; }
    public void setDescendantFocusability(int p0) {}
    public void setFillViewport(boolean p0) {}
    public void setFocusable(boolean p0) {}
    public void setNestedScrollingEnabled(boolean p0) {}
    public void setOnScrollChangeListener(android.support.v4.widget.NestedScrollView.OnScrollChangeListener p0) {}
    public void setSmoothScrollingEnabled(boolean p0) {}
    public void setWillNotDraw(boolean p0) {}
    public boolean shouldDelayChildPressedState() { return false; }
    public void smoothScrollBy(int p0, int p1) {}
    public void smoothScrollTo(int p0, int p1) {}
    public boolean startNestedScroll(int p0) { return false; }
    public void stopNestedScroll() {}

    public static class AccessibilityDelegate {
        public void onInitializeAccessibilityEvent(android.view.View p0, android.view.accessibility.AccessibilityEvent p1) {}
        public void onInitializeAccessibilityNodeInfo(android.view.View p0, android.support.v4.view.accessibility.AccessibilityNodeInfoCompat p1) {}
        public boolean performAccessibilityAction(android.view.View p0, int p1, android.os.Bundle p2) { return false; }
    }

    public interface OnScrollChangeListener {
        void onScrollChange(android.support.v4.widget.NestedScrollView p0, int p1, int p2, int p3, int p4);
    }

    public static class SavedState {
        public android.os.Parcelable getSuperState() { return null; }
        public java.lang.String toString() { return null; }
        public void writeToParcel(android.os.Parcel p0, int p1) {}
    }
}
