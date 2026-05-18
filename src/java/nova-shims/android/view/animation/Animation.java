package android.view.animation;

public abstract class Animation {
    public static final int ABSOLUTE = 0;
    public static final int RELATIVE_TO_SELF = 1;
    public static final int RELATIVE_TO_PARENT = 2;
    public static final int RESTART = 1;
    public static final int REVERSE = 2;
    public static final int INFINITE = -1;
    public static final int START_ON_FIRST_FRAME = -1;
    public static final int ZORDER_NORMAL = 0;
    public static final int ZORDER_TOP = 1;
    public static final int ZORDER_BOTTOM = -1;

    public interface AnimationListener {
        void onAnimationStart(Animation animation);
        void onAnimationEnd(Animation animation);
        void onAnimationRepeat(Animation animation);
    }

    public void setDuration(long durationMillis) {}
    public void setRepeatCount(int repeatCount) {}
    public void setRepeatMode(int repeatMode) {}
    public void setInterpolator(Interpolator i) {}
    public void setAnimationListener(AnimationListener listener) {}
    public void setFillAfter(boolean fillAfter) {}
    public void setFillBefore(boolean fillBefore) {}
    public void setStartOffset(long startOffset) {}
    public void start() {}
    public void cancel() {}
    public void reset() {}
    public boolean hasStarted() { return false; }
    public boolean hasEnded() { return true; }
    public boolean isInitialized() { return false; }
    public long getStartTime() { return -1; }
    public long getDuration() { return 0; }
    public void initialize(int width, int height, int parentWidth, int parentHeight) {}
    public boolean getTransformation(long currentTime, android.view.animation.Transformation outTransformation) { return false; }
    public boolean isFillEnabled() { return false; }
}
