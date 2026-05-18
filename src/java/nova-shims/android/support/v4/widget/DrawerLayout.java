package android.support.v4.widget;

public class DrawerLayout {
    public int[] access$400() { return null; }
    public boolean access$500() { return false; }
    public android.view.View access$600(android.support.v4.widget.DrawerLayout p0) { return null; }
    public boolean access$700(android.view.View p0) { return false; }
    public void addDrawerListener(android.support.v4.widget.DrawerLayout.DrawerListener p0) {}
    public void addFocusables(java.util.ArrayList p0, int p1, int p2) {}
    public void addView(android.view.View p0, int p1, android.view.ViewGroup.LayoutParams p2) {}
    public void cancelChildViewTouch() {}
    public boolean checkDrawerViewAbsoluteGravity(android.view.View p0, int p1) { return false; }
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p0) { return false; }
    public void closeDrawer(int p0) {}
    public void closeDrawers() {}
    public void closeDrawers(boolean p0) {}
    public void computeScroll() {}
    public void dispatchOnDrawerClosed(android.view.View p0) {}
    public void dispatchOnDrawerOpened(android.view.View p0) {}
    public void dispatchOnDrawerSlide(android.view.View p0, float p1) {}
    public boolean drawChild(android.graphics.Canvas p0, android.view.View p1, long p2) { return false; }
    public android.view.View findDrawerWithGravity(int p0) { return null; }
    public android.view.View findOpenDrawer() { return null; }
    public android.view.View findVisibleDrawer() { return null; }
    public android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() { return null; }
    public android.view.ViewGroup.LayoutParams generateLayoutParams(android.util.AttributeSet p0) { return null; }
    public android.graphics.drawable.Drawable getBackground() { return null; }
    public android.view.View getChildAt(int p0) { return null; }
    public int getChildCount() { return 0; }
    public int getChildMeasureSpec(int p0, int p1, int p2) { return 0; }
    public android.content.Context getContext() { return null; }
    public int getDescendantFocusability() { return 0; }
    public float getDrawerElevation() { return 0f; }
    public int getDrawerLockMode(int p0) { return 0; }
    public java.lang.CharSequence getDrawerTitle(int p0) { return null; }
    public int getDrawerViewAbsoluteGravity(android.view.View p0) { return 0; }
    public float getDrawerViewOffset(android.view.View p0) { return 0f; }
    public int getHeight() { return 0; }
    public android.content.res.Resources getResources() { return null; }
    public android.view.View getRootView() { return null; }
    public android.graphics.drawable.Drawable getStatusBarBackgroundDrawable() { return null; }
    public int getWidth() { return 0; }
    public java.lang.String gravityToString(int p0) { return null; }
    public boolean hasOpaqueBackground(android.view.View p0) { return false; }
    public boolean hasPeekingDrawer() { return false; }
    public boolean hasVisibleDrawer() { return false; }
    public boolean hasWindowFocus() { return false; }
    public boolean includeChildForAccessibility(android.view.View p0) { return false; }
    public void invalidate() {}
    public boolean isContentView(android.view.View p0) { return false; }
    public boolean isDrawerOpen(int p0) { return false; }
    public boolean isDrawerView(android.view.View p0) { return false; }
    public boolean isDrawerVisible(int p0) { return false; }
    public boolean isInEditMode() { return false; }
    public boolean mirror(android.graphics.drawable.Drawable p0, int p1) { return false; }
    public void moveDrawerToOffset(android.view.View p0, float p1) {}
    public void onAttachedToWindow() {}
    public void onDetachedFromWindow() {}
    public void onDraw(android.graphics.Canvas p0) {}
    public boolean onInterceptTouchEvent(android.view.MotionEvent p0) { return false; }
    public boolean onKeyDown(int p0, android.view.KeyEvent p1) { return false; }
    public boolean onKeyUp(int p0, android.view.KeyEvent p1) { return false; }
    public void onLayout(boolean p0, int p1, int p2, int p3, int p4) {}
    public void onMeasure(int p0, int p1) {}
    public void onRestoreInstanceState(android.os.Parcelable p0) {}
    public void onRtlPropertiesChanged(int p0) {}
    public android.os.Parcelable onSaveInstanceState() { return null; }
    public boolean onTouchEvent(android.view.MotionEvent p0) { return false; }
    public void openDrawer(int p0) {}
    public boolean postDelayed(java.lang.Runnable p0, long p1) { return false; }
    public boolean removeCallbacks(java.lang.Runnable p0) { return false; }
    public void removeDrawerListener(android.support.v4.widget.DrawerLayout.DrawerListener p0) {}
    public void requestDisallowInterceptTouchEvent(boolean p0) {}
    public void requestLayout() {}
    public android.graphics.drawable.Drawable resolveLeftShadow() { return null; }
    public android.graphics.drawable.Drawable resolveRightShadow() { return null; }
    public void resolveShadowDrawables() {}
    public void sendAccessibilityEvent(int p0) {}
    public void setChildInsets(java.lang.Object p0, boolean p1) {}
    public void setDescendantFocusability(int p0) {}
    public void setDrawerElevation(float p0) {}
    public void setDrawerListener(android.support.v4.widget.DrawerLayout.DrawerListener p0) {}
    public void setDrawerLockMode(int p0) {}
    public void setDrawerLockMode(int p0, int p1) {}
    public void setDrawerShadow(int p0, int p1) {}
    public void setDrawerTitle(int p0, java.lang.CharSequence p1) {}
    public void setDrawerViewOffset(android.view.View p0, float p1) {}
    public void setFocusableInTouchMode(boolean p0) {}
    public void setMeasuredDimension(int p0, int p1) {}
    public void setScrimColor(int p0) {}
    public void setStatusBarBackground(int p0) {}
    public void setStatusBarBackgroundColor(int p0) {}
    public void setWillNotDraw(boolean p0) {}
    public void updateChildrenImportantForAccessibility(android.view.View p0, boolean p1) {}
    public void updateDrawerState(int p0, int p1, android.view.View p2) {}

    public static class AccessibilityDelegate {
        public void addChildrenForAccessibility(android.support.v4.view.accessibility.AccessibilityNodeInfoCompat p0, android.view.ViewGroup p1) {}
        public void copyNodeInfoNoChildren(android.support.v4.view.accessibility.AccessibilityNodeInfoCompat p0, android.support.v4.view.accessibility.AccessibilityNodeInfoCompat p1) {}
        public boolean dispatchPopulateAccessibilityEvent(android.view.View p0, android.view.accessibility.AccessibilityEvent p1) { return false; }
        public void onInitializeAccessibilityEvent(android.view.View p0, android.view.accessibility.AccessibilityEvent p1) {}
        public void onInitializeAccessibilityNodeInfo(android.view.View p0, android.support.v4.view.accessibility.AccessibilityNodeInfoCompat p1) {}
        public boolean onRequestSendAccessibilityEvent(android.view.ViewGroup p0, android.view.View p1, android.view.accessibility.AccessibilityEvent p2) { return false; }
    }

    public static class ChildAccessibilityDelegate {
        public void onInitializeAccessibilityNodeInfo(android.view.View p0, android.support.v4.view.accessibility.AccessibilityNodeInfoCompat p1) {}
    }

    public static class DrawerLayoutCompatImpl {
        public void applyMarginInsets(android.view.ViewGroup.MarginLayoutParams p0, java.lang.Object p1, int p2) {}
        public void configureApplyInsets(android.view.View p0) {}
        public void dispatchChildInsets(android.view.View p0, java.lang.Object p1, int p2) {}
        public android.graphics.drawable.Drawable getDefaultStatusBarBackground(android.content.Context p0) { return null; }
        public int getTopInset(java.lang.Object p0) { return 0; }
    }

    public static class DrawerLayoutCompatImplApi21 {
        public void applyMarginInsets(android.view.ViewGroup.MarginLayoutParams p0, java.lang.Object p1, int p2) {}
        public void configureApplyInsets(android.view.View p0) {}
        public void dispatchChildInsets(android.view.View p0, java.lang.Object p1, int p2) {}
        public android.graphics.drawable.Drawable getDefaultStatusBarBackground(android.content.Context p0) { return null; }
        public int getTopInset(java.lang.Object p0) { return 0; }
    }

    public static class DrawerLayoutCompatImplBase {
        public void applyMarginInsets(android.view.ViewGroup.MarginLayoutParams p0, java.lang.Object p1, int p2) {}
        public void configureApplyInsets(android.view.View p0) {}
        public void dispatchChildInsets(android.view.View p0, java.lang.Object p1, int p2) {}
        public android.graphics.drawable.Drawable getDefaultStatusBarBackground(android.content.Context p0) { return null; }
        public int getTopInset(java.lang.Object p0) { return 0; }
    }

    public interface DrawerListener {
        void onDrawerClosed(android.view.View p0);
        void onDrawerOpened(android.view.View p0);
        void onDrawerSlide(android.view.View p0, float p1);
        void onDrawerStateChanged(int p0);
    }

    public static class LayoutParams {
        public float access$000(android.support.v4.widget.DrawerLayout.LayoutParams p0) { return 0f; }
        public float access$002(android.support.v4.widget.DrawerLayout.LayoutParams p0, float p1) { return 0f; }
        public int access$100(android.support.v4.widget.DrawerLayout.LayoutParams p0) { return 0; }
        public int access$102(android.support.v4.widget.DrawerLayout.LayoutParams p0, int p1) { return 0; }
        public int access$176(android.support.v4.widget.DrawerLayout.LayoutParams p0, int p1) { return 0; }
        public boolean access$200(android.support.v4.widget.DrawerLayout.LayoutParams p0) { return false; }
        public boolean access$202(android.support.v4.widget.DrawerLayout.LayoutParams p0, boolean p1) { return false; }
    }

    public static class SavedState {
        public android.os.Parcelable getSuperState() { return null; }
        public void writeToParcel(android.os.Parcel p0, int p1) {}
    }

    public interface SimpleDrawerListener {
        void onDrawerClosed(android.view.View p0);
        void onDrawerOpened(android.view.View p0);
        void onDrawerSlide(android.view.View p0, float p1);
        void onDrawerStateChanged(int p0);
    }

    public interface ViewDragCallback {
        void access$300(android.support.v4.widget.DrawerLayout.ViewDragCallback p0);
        int clampViewPositionHorizontal(android.view.View p0, int p1, int p2);
        int clampViewPositionVertical(android.view.View p0, int p1, int p2);
        void closeOtherDrawer();
        int getViewHorizontalDragRange(android.view.View p0);
        void onEdgeDragStarted(int p0, int p1);
        boolean onEdgeLock(int p0);
        void onEdgeTouched(int p0, int p1);
        void onViewCaptured(android.view.View p0, int p1);
        void onViewDragStateChanged(int p0);
        void onViewPositionChanged(android.view.View p0, int p1, int p2, int p3, int p4);
        void onViewReleased(android.view.View p0, float p1, float p2);
        void peekDrawer();
        void removeCallbacks();
        void setDragger(android.support.v4.widget.ViewDragHelper p0);
        boolean tryCaptureView(android.view.View p0, int p1);
    }
}
