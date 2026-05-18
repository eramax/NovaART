package android.view.animation;

public class DecelerateInterpolator implements Interpolator {
    private final float mFactor;
    public DecelerateInterpolator() { mFactor = 1.0f; }
    public DecelerateInterpolator(float factor) { mFactor = factor; }
    @Override public float getInterpolation(float input) {
        if (mFactor == 1.0f) return 1.0f - (1.0f - input) * (1.0f - input);
        return (float)(1.0 - Math.pow(1.0 - input, 2.0 * mFactor));
    }
}
