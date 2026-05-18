package android.support.v4.widget;

public class ViewDragHelper {
    public void abort() {}
    public boolean canScroll(android.view.View p0, boolean p1, int p2, int p3, int p4, int p5) { return false; }
    public void cancel() {}
    public void captureChildView(android.view.View p0, int p1) {}
    public boolean checkNewEdgeDrag(float p0, float p1, int p2, int p3) { return false; }
    public boolean checkTouchSlop(int p0) { return false; }
    public boolean checkTouchSlop(int p0, int p1) { return false; }
    public boolean checkTouchSlop(android.view.View p0, float p1, float p2) { return false; }
    public float clampMag(float p0, float p1, float p2) { return 0f; }
    public void clearMotionHistory() {}
    public void clearMotionHistory(int p0) {}
    public int computeAxisDuration(int p0, int p1, int p2) { return 0; }
    public int computeSettleDuration(android.view.View p0, int p1, int p2, int p3, int p4) { return 0; }
    public boolean continueSettling(boolean p0) { return false; }
    public android.support.v4.widget.ViewDragHelper create(android.view.ViewGroup p0, float p1, android.support.v4.widget.ViewDragHelper.Callback p2) { return null; }
    public android.support.v4.widget.ViewDragHelper create(android.view.ViewGroup p0, android.support.v4.widget.ViewDragHelper.Callback p1) { return null; }
    public void dispatchViewReleased(float p0, float p1) {}
    public float distanceInfluenceForSnapDuration(float p0) { return 0f; }
    public void dragTo(int p0, int p1, int p2, int p3) {}
    public void ensureMotionHistorySizeForId(int p0) {}
    public android.view.View findTopChildUnder(int p0, int p1) { return null; }
    public void flingCapturedView(int p0, int p1, int p2, int p3) {}
    public boolean forceSettleCapturedViewAt(int p0, int p1, int p2, int p3) { return false; }
    public int getActivePointerId() { return 0; }
    public android.view.View getCapturedView() { return null; }
    public int getEdgeSize() { return 0; }
    public int getEdgesTouched(int p0, int p1) { return 0; }
    public float getMinVelocity() { return 0f; }
    public int getTouchSlop() { return 0; }
    public int getViewDragState() { return 0; }
    public boolean isCapturedViewUnder(int p0, int p1) { return false; }
    public boolean isEdgeTouched(int p0) { return false; }
    public boolean isEdgeTouched(int p0, int p1) { return false; }
    public boolean isPointerDown(int p0) { return false; }
    public boolean isValidPointerForActionMove(int p0) { return false; }
    public boolean isViewUnder(android.view.View p0, int p1, int p2) { return false; }
    public void processTouchEvent(android.view.MotionEvent p0) {}
    public void releaseViewForPointerUp() {}
    public void reportNewEdgeDrags(float p0, float p1, int p2) {}
    public void saveInitialMotion(float p0, float p1, int p2) {}
    public void saveLastMotion(android.view.MotionEvent p0) {}
    public void setDragState(int p0) {}
    public void setEdgeTrackingEnabled(int p0) {}
    public void setMinVelocity(float p0) {}
    public boolean settleCapturedViewAt(int p0, int p1) { return false; }
    public boolean shouldInterceptTouchEvent(android.view.MotionEvent p0) { return false; }
    public boolean smoothSlideViewTo(android.view.View p0, int p1, int p2) { return false; }
    public boolean tryCaptureViewForDrag(android.view.View p0, int p1) { return false; }

    public interface Callback {
        int clampViewPositionHorizontal(android.view.View p0, int p1, int p2);
        int clampViewPositionVertical(android.view.View p0, int p1, int p2);
        int getOrderedChildIndex(int p0);
        int getViewHorizontalDragRange(android.view.View p0);
        int getViewVerticalDragRange(android.view.View p0);
        void onEdgeDragStarted(int p0, int p1);
        boolean onEdgeLock(int p0);
        void onEdgeTouched(int p0, int p1);
        void onViewCaptured(android.view.View p0, int p1);
        void onViewDragStateChanged(int p0);
        void onViewPositionChanged(android.view.View p0, int p1, int p2, int p3, int p4);
        void onViewReleased(android.view.View p0, float p1, float p2);
        boolean tryCaptureView(android.view.View p0, int p1);
    }
}
