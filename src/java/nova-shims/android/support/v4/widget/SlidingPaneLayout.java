package android.support.v4.widget;

public class SlidingPaneLayout {
    public boolean access$100(android.support.v4.widget.SlidingPaneLayout p0) { return false; }
    public void access$1000(android.support.v4.widget.SlidingPaneLayout p0, android.view.View p1) {}
    public java.util.ArrayList access$1100(android.support.v4.widget.SlidingPaneLayout p0) { return null; }
    public android.support.v4.widget.ViewDragHelper access$200(android.support.v4.widget.SlidingPaneLayout p0) { return null; }
    public float access$300(android.support.v4.widget.SlidingPaneLayout p0) { return 0f; }
    public android.view.View access$400(android.support.v4.widget.SlidingPaneLayout p0) { return null; }
    public boolean access$502(android.support.v4.widget.SlidingPaneLayout p0, boolean p1) { return false; }
    public void access$600(android.support.v4.widget.SlidingPaneLayout p0, int p1) {}
    public boolean access$700(android.support.v4.widget.SlidingPaneLayout p0) { return false; }
    public int access$800(android.support.v4.widget.SlidingPaneLayout p0) { return 0; }
    public boolean canScroll(android.view.View p0, boolean p1, int p2, int p3, int p4) { return false; }
    public boolean canSlide() { return false; }
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p0) { return false; }
    public boolean closePane() { return false; }
    public boolean closePane(android.view.View p0, int p1) { return false; }
    public void computeScroll() {}
    public void dimChildView(android.view.View p0, float p1, int p2) {}
    public void dispatchOnPanelClosed(android.view.View p0) {}
    public void dispatchOnPanelOpened(android.view.View p0) {}
    public void dispatchOnPanelSlide(android.view.View p0) {}
    public void draw(android.graphics.Canvas p0) {}
    public boolean drawChild(android.graphics.Canvas p0, android.view.View p1, long p2) { return false; }
    public android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() { return null; }
    public android.view.ViewGroup.LayoutParams generateLayoutParams(android.util.AttributeSet p0) { return null; }
    public android.view.View getChildAt(int p0) { return null; }
    public int getChildCount() { return 0; }
    public android.content.Context getContext() { return null; }
    public int getCoveredFadeColor() { return 0; }
    public int getHeight() { return 0; }
    public int getPaddingBottom() { return 0; }
    public int getPaddingLeft() { return 0; }
    public int getPaddingRight() { return 0; }
    public int getPaddingTop() { return 0; }
    public int getParallaxDistance() { return 0; }
    public android.content.res.Resources getResources() { return null; }
    public int getSliderFadeColor() { return 0; }
    public int getWidth() { return 0; }
    public void invalidate() {}
    public void invalidateChildRegion(android.view.View p0) {}
    public boolean isDimmed(android.view.View p0) { return false; }
    public boolean isInEditMode() { return false; }
    public boolean isInTouchMode() { return false; }
    public boolean isLayoutRtlSupport() { return false; }
    public boolean isOpen() { return false; }
    public boolean isSlideable() { return false; }
    public void onAttachedToWindow() {}
    public void onDetachedFromWindow() {}
    public boolean onInterceptTouchEvent(android.view.MotionEvent p0) { return false; }
    public void onLayout(boolean p0, int p1, int p2, int p3, int p4) {}
    public void onMeasure(int p0, int p1) {}
    public void onPanelDragged(int p0) {}
    public void onRestoreInstanceState(android.os.Parcelable p0) {}
    public android.os.Parcelable onSaveInstanceState() { return null; }
    public void onSizeChanged(int p0, int p1, int p2, int p3) {}
    public boolean onTouchEvent(android.view.MotionEvent p0) { return false; }
    public boolean openPane() { return false; }
    public boolean openPane(android.view.View p0, int p1) { return false; }
    public void parallaxOtherViews(float p0) {}
    public void requestChildFocus(android.view.View p0, android.view.View p1) {}
    public void requestLayout() {}
    public void sendAccessibilityEvent(int p0) {}
    public void setAllChildrenVisible() {}
    public void setCoveredFadeColor(int p0) {}
    public void setMeasuredDimension(int p0, int p1) {}
    public void setPanelSlideListener(android.support.v4.widget.SlidingPaneLayout.PanelSlideListener p0) {}
    public void setParallaxDistance(int p0) {}
    public void setShadowDrawable(android.graphics.drawable.Drawable p0) {}
    public void setShadowDrawableLeft(android.graphics.drawable.Drawable p0) {}
    public void setShadowDrawableRight(android.graphics.drawable.Drawable p0) {}
    public void setShadowResource(int p0) {}
    public void setShadowResourceLeft(int p0) {}
    public void setShadowResourceRight(int p0) {}
    public void setSliderFadeColor(int p0) {}
    public void setWillNotDraw(boolean p0) {}
    public void smoothSlideClosed() {}
    public void smoothSlideOpen() {}
    public boolean smoothSlideTo(float p0, int p1) { return false; }
    public void updateObscuredViewsVisibility(android.view.View p0) {}
    public boolean viewIsOpaque(android.view.View p0) { return false; }

    public static class AccessibilityDelegate {
        public void copyNodeInfoNoChildren(android.support.v4.view.accessibility.AccessibilityNodeInfoCompat p0, android.support.v4.view.accessibility.AccessibilityNodeInfoCompat p1) {}
        public boolean filter(android.view.View p0) { return false; }
        public void onInitializeAccessibilityEvent(android.view.View p0, android.view.accessibility.AccessibilityEvent p1) {}
        public void onInitializeAccessibilityNodeInfo(android.view.View p0, android.support.v4.view.accessibility.AccessibilityNodeInfoCompat p1) {}
        public boolean onRequestSendAccessibilityEvent(android.view.ViewGroup p0, android.view.View p1, android.view.accessibility.AccessibilityEvent p2) { return false; }
    }

    public static class DisableLayerRunnable {
        public void run() {}
    }

    public interface DragHelperCallback {
        int clampViewPositionHorizontal(android.view.View p0, int p1, int p2);
        int clampViewPositionVertical(android.view.View p0, int p1, int p2);
        int getViewHorizontalDragRange(android.view.View p0);
        void onEdgeDragStarted(int p0, int p1);
        void onViewCaptured(android.view.View p0, int p1);
        void onViewDragStateChanged(int p0);
        void onViewPositionChanged(android.view.View p0, int p1, int p2, int p3, int p4);
        void onViewReleased(android.view.View p0, float p1, float p2);
        boolean tryCaptureView(android.view.View p0, int p1);
    }

    public static class LayoutParams {
    }

    public interface PanelSlideListener {
        void onPanelClosed(android.view.View p0);
        void onPanelOpened(android.view.View p0);
        void onPanelSlide(android.view.View p0, float p1);
    }

    public static class SavedState {
        public android.os.Parcelable getSuperState() { return null; }
        public void writeToParcel(android.os.Parcel p0, int p1) {}
    }

    public interface SimplePanelSlideListener {
        void onPanelClosed(android.view.View p0);
        void onPanelOpened(android.view.View p0);
        void onPanelSlide(android.view.View p0, float p1);
    }

    public static class SlidingPanelLayoutImpl {
        public void invalidateChildRegion(android.support.v4.widget.SlidingPaneLayout p0, android.view.View p1) {}
    }

    public static class SlidingPanelLayoutImplBase {
        public void invalidateChildRegion(android.support.v4.widget.SlidingPaneLayout p0, android.view.View p1) {}
    }

    public static class SlidingPanelLayoutImplJB {
        public void invalidateChildRegion(android.support.v4.widget.SlidingPaneLayout p0, android.view.View p1) {}
    }

    public static class SlidingPanelLayoutImplJBMR1 {
        public void invalidateChildRegion(android.support.v4.widget.SlidingPaneLayout p0, android.view.View p1) {}
    }
}
