package android.view;

import android.animation.Animator;

public class ViewPropertyAnimator {
    private final View mView;

    public ViewPropertyAnimator(View view) {
        mView = view;
    }

    public ViewPropertyAnimator setDuration(long duration) { return this; }
    public ViewPropertyAnimator setStartDelay(long startDelay) { return this; }
    public ViewPropertyAnimator setInterpolator(android.view.animation.Interpolator interpolator) { return this; }
    public ViewPropertyAnimator setListener(Animator.AnimatorListener listener) { return this; }

    public ViewPropertyAnimator alpha(float value) { return this; }
    public ViewPropertyAnimator alphaBy(float value) { return this; }
    public ViewPropertyAnimator translationX(float value) { return this; }
    public ViewPropertyAnimator translationY(float value) { return this; }
    public ViewPropertyAnimator translationZ(float value) { return this; }
    public ViewPropertyAnimator scaleX(float value) { return this; }
    public ViewPropertyAnimator scaleY(float value) { return this; }
    public ViewPropertyAnimator rotation(float value) { return this; }
    public ViewPropertyAnimator rotationX(float value) { return this; }
    public ViewPropertyAnimator rotationY(float value) { return this; }
    public ViewPropertyAnimator x(float value) { return this; }
    public ViewPropertyAnimator y(float value) { return this; }
    public ViewPropertyAnimator z(float value) { return this; }

    public void start() {}
    public void cancel() {}

    public ViewPropertyAnimator withStartAction(Runnable runnable) { return this; }
    public ViewPropertyAnimator withEndAction(Runnable runnable) { return this; }
    public ViewPropertyAnimator withLayer() { return this; }
}
