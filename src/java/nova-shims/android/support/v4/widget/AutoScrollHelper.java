package android.support.v4.widget;

public class AutoScrollHelper {
    public boolean access$100(android.support.v4.widget.AutoScrollHelper p0) { return false; }
    public boolean access$102(android.support.v4.widget.AutoScrollHelper p0, boolean p1) { return false; }
    public boolean access$200(android.support.v4.widget.AutoScrollHelper p0) { return false; }
    public boolean access$202(android.support.v4.widget.AutoScrollHelper p0, boolean p1) { return false; }
    public android.support.v4.widget.AutoScrollHelper.ClampedScroller access$300(android.support.v4.widget.AutoScrollHelper p0) { return null; }
    public boolean access$400(android.support.v4.widget.AutoScrollHelper p0) { return false; }
    public boolean access$500(android.support.v4.widget.AutoScrollHelper p0) { return false; }
    public boolean access$502(android.support.v4.widget.AutoScrollHelper p0, boolean p1) { return false; }
    public void access$600(android.support.v4.widget.AutoScrollHelper p0) {}
    public android.view.View access$700(android.support.v4.widget.AutoScrollHelper p0) { return null; }
    public int access$800(int p0, int p1, int p2) { return 0; }
    public float access$900(float p0, float p1, float p2) { return 0f; }
    public boolean canTargetScrollHorizontally(int p0) { return false; }
    public boolean canTargetScrollVertically(int p0) { return false; }
    public void cancelTargetTouch() {}
    public float computeTargetVelocity(int p0, float p1, float p2, float p3) { return 0f; }
    public float constrain(float p0, float p1, float p2) { return 0f; }
    public float constrainEdgeValue(float p0, float p1) { return 0f; }
    public float getEdgeValue(float p0, float p1, float p2, float p3) { return 0f; }
    public boolean isEnabled() { return false; }
    public boolean isExclusive() { return false; }
    public boolean onTouch(android.view.View p0, android.view.MotionEvent p1) { return false; }
    public void requestStop() {}
    public void scrollTargetBy(int p0, int p1) {}
    public android.support.v4.widget.AutoScrollHelper setActivationDelay(int p0) { return null; }
    public android.support.v4.widget.AutoScrollHelper setEdgeType(int p0) { return null; }
    public android.support.v4.widget.AutoScrollHelper setEnabled(boolean p0) { return null; }
    public android.support.v4.widget.AutoScrollHelper setExclusive(boolean p0) { return null; }
    public android.support.v4.widget.AutoScrollHelper setMaximumEdges(float p0, float p1) { return null; }
    public android.support.v4.widget.AutoScrollHelper setMaximumVelocity(float p0, float p1) { return null; }
    public android.support.v4.widget.AutoScrollHelper setMinimumVelocity(float p0, float p1) { return null; }
    public android.support.v4.widget.AutoScrollHelper setRampDownDuration(int p0) { return null; }
    public android.support.v4.widget.AutoScrollHelper setRampUpDuration(int p0) { return null; }
    public android.support.v4.widget.AutoScrollHelper setRelativeEdges(float p0, float p1) { return null; }
    public android.support.v4.widget.AutoScrollHelper setRelativeVelocity(float p0, float p1) { return null; }
    public boolean shouldAnimate() { return false; }
    public void startAnimating() {}

    public static class ClampedScroller {
        public void computeScrollDelta() {}
        public int getDeltaX() { return 0; }
        public int getDeltaY() { return 0; }
        public int getHorizontalDirection() { return 0; }
        public float getValueAt(long p0) { return 0f; }
        public int getVerticalDirection() { return 0; }
        public float interpolateValue(float p0) { return 0f; }
        public boolean isFinished() { return false; }
        public void requestStop() {}
        public void setRampDownDuration(int p0) {}
        public void setRampUpDuration(int p0) {}
        public void setTargetVelocity(float p0, float p1) {}
        public void start() {}
    }

    public static class ScrollAnimationRunnable {
        public void run() {}
    }
}
