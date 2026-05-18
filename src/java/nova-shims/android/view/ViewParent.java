package android.view;

public interface ViewParent {
    void requestLayout();
    boolean isLayoutRequested();
    void invalidateChild(View child, android.graphics.Rect dirty);
    ViewParent invalidateChildInParent(int[] location, android.graphics.Rect dirty);
    ViewParent getParent();
    void requestChildFocus(View child, View focused);
    void recomputeViewAttributes(View child);
    void clearChildFocus(View child);
    boolean getChildVisibleRect(View child, android.graphics.Rect r, android.graphics.Point offset);
    void childHasTransientStateChanged(View child, boolean hasTransientState);
    boolean requestChildRectangleOnScreen(View child, android.graphics.Rect rectangle, boolean immediate);
    void focusableViewAvailable(View v);
    boolean showContextMenuForChild(View originalView);
    boolean showContextMenuForChild(View originalView, float x, float y);
    void requestDisallowInterceptTouchEvent(boolean disallowIntercept);
    void childDrawableStateChanged(View child);
    void bringChildToFront(View child);
    void notifySubtreeAccessibilityStateChanged(View child, View source, int changeType);
    boolean onStartNestedScroll(View child, View target, int nestedScrollAxes);
    void onNestedScrollAccepted(View child, View target, int nestedScrollAxes);
    void onStopNestedScroll(View target);
    void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed);
    void onNestedPreScroll(View target, int dx, int dy, int[] consumed);
    boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed);
    boolean onNestedPreFling(View target, float velocityX, float velocityY);
    int getNestedScrollAxes();
}
