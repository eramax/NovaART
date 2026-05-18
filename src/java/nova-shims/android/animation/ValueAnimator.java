package android.animation;

import android.util.Log;

public class ValueAnimator extends Animator {
    private static final String TAG = "NovaValueAnimator";

    public static final int RESTART = 1;
    public static final int REVERSE = 2;
    public static final int INFINITE = -1;

    public interface AnimatorUpdateListener {
        void onAnimationUpdate(ValueAnimator animation);
    }

    private long mDuration = 300;
    private long mStartDelay = 0;
    private int mRepeatCount = 0;
    private int mRepeatMode = RESTART;
    private float mAnimatedFraction = 0f;
    private Object mAnimatedValue = 0f;
    private Object[] mValues;
    private volatile boolean mRunning = false;
    private Thread mAnimThread;

    private java.util.List<AnimatorUpdateListener> mUpdateListeners;

    public static ValueAnimator ofFloat(float... values) {
        ValueAnimator va = new ValueAnimator();
        va.setFloatValues(values);
        return va;
    }

    public static ValueAnimator ofInt(int... values) {
        ValueAnimator va = new ValueAnimator();
        va.setIntValues(values);
        return va;
    }

    public static ValueAnimator ofObject(TypeEvaluator evaluator, Object... values) {
        ValueAnimator va = new ValueAnimator();
        va.mValues = values;
        if (values.length > 0) va.mAnimatedValue = values[0];
        return va;
    }

    public void setFloatValues(float... values) {
        if (values.length > 0) mAnimatedValue = values[0];
        mValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) mValues[i] = values[i];
    }

    public void setIntValues(int... values) {
        if (values.length > 0) mAnimatedValue = values[0];
        mValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) mValues[i] = values[i];
    }

    public void addUpdateListener(AnimatorUpdateListener listener) {
        if (mUpdateListeners == null) mUpdateListeners = new java.util.ArrayList<>();
        mUpdateListeners.add(listener);
    }

    public void removeUpdateListener(AnimatorUpdateListener listener) {
        if (mUpdateListeners != null) mUpdateListeners.remove(listener);
    }

    public void removeAllUpdateListeners() {
        if (mUpdateListeners != null) mUpdateListeners.clear();
    }

    public Object getAnimatedValue() { return mAnimatedValue; }
    public float getAnimatedFraction() { return mAnimatedFraction; }

    @Override
    public void start() {
        if (mRunning) return;
        mRunning = true;
        Log.d(TAG, "start() duration=" + mDuration + " repeat=" + mRepeatCount);
        notifyStart();
        mAnimThread = new Thread(this::animLoop, "NovaAnimator");
        mAnimThread.setDaemon(true);
        mAnimThread.start();
    }

    private void animLoop() {
        int frameDelay = 16; // ~60fps
        long startTime = System.currentTimeMillis();
        int iterations = 0;
        while (mRunning) {
            long elapsed = System.currentTimeMillis() - startTime;
            long cycleDuration = mDuration > 0 ? mDuration : frameDelay;
            long cycleElapsed = elapsed % cycleDuration;
            mAnimatedFraction = cycleDuration > 0 ? (float) cycleElapsed / cycleDuration : 0f;

            // interpolate animated value
            if (mValues != null && mValues.length >= 2) {
                Object start = mValues[0];
                Object end = mValues[mValues.length - 1];
                if (start instanceof Float) {
                    float s = (Float) start, e = (Float) end;
                    mAnimatedValue = s + (e - s) * mAnimatedFraction;
                } else if (start instanceof Integer) {
                    int s = (Integer) start, e = (Integer) end;
                    mAnimatedValue = (int)(s + (e - s) * mAnimatedFraction);
                }
            }

            fireUpdateListeners();

            if (mRepeatCount != INFINITE) {
                long totalDuration = cycleDuration * (mRepeatCount + 1);
                if (elapsed >= totalDuration) {
                    mRunning = false;
                    notifyEnd();
                    break;
                }
            } else {
                long cycleNum = elapsed / cycleDuration;
                if (cycleNum > iterations) {
                    iterations = (int) cycleNum;
                    notifyRepeat();
                }
            }

            try { Thread.sleep(frameDelay); }
            catch (InterruptedException e) { break; }
        }
    }

    private void fireUpdateListeners() {
        if (mUpdateListeners == null) return;
        for (AnimatorUpdateListener l : new java.util.ArrayList<>(mUpdateListeners)) {
            try { l.onAnimationUpdate(this); }
            catch (Exception e) { Log.e(TAG, "updateListener error: " + e); }
        }
    }

    @Override
    public void cancel() {
        mRunning = false;
        if (mAnimThread != null) mAnimThread.interrupt();
    }

    @Override
    public boolean isRunning() { return mRunning; }

    @Override
    public long getDuration() { return mDuration; }

    @Override
    public Animator setDuration(long duration) { mDuration = duration; return this; }

    @Override
    public long getStartDelay() { return mStartDelay; }

    @Override
    public void setStartDelay(long startDelay) { mStartDelay = startDelay; }

    public int getRepeatCount() { return mRepeatCount; }
    public void setRepeatCount(int value) { mRepeatCount = value; }

    public int getRepeatMode() { return mRepeatMode; }
    public void setRepeatMode(int value) { mRepeatMode = value; }

    public void setInterpolator(android.view.animation.Interpolator value) {}
    public android.view.animation.Interpolator getInterpolator() { return null; }

    public void setEvaluator(TypeEvaluator value) {}

    public long getCurrentPlayTime() { return 0; }
    public void setCurrentPlayTime(long playTime) {}
    public void reverse() {}

    public interface TypeEvaluator<T> {
        T evaluate(float fraction, T startValue, T endValue);
    }
}
