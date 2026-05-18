package android.view;

public class ViewPropertyAnimator {

    private final View mView;

    public ViewPropertyAnimator(View view) { mView = view; }

    public ViewPropertyAnimator setDuration(long duration) { return this; }
    public long getDuration() { return 300; }
    public ViewPropertyAnimator setStartDelay(long delay) { return this; }
    public long getStartDelay() { return 0; }
    public ViewPropertyAnimator setInterpolator(android.animation.TimeInterpolator interpolator) { return this; }
    public ViewPropertyAnimator withLayer() { return this; }
    public ViewPropertyAnimator withStartAction(Runnable runnable) { return this; }
    public ViewPropertyAnimator withEndAction(Runnable runnable) { if (runnable != null) runnable.run(); return this; }
    public ViewPropertyAnimator setListener(android.animation.Animator.AnimatorListener listener) { return this; }
    public ViewPropertyAnimator setUpdateListener(android.animation.ValueAnimator.AnimatorUpdateListener listener) { return this; }

    public ViewPropertyAnimator alpha(float value)          { mView.setAlpha(value); return this; }
    public ViewPropertyAnimator alphaBy(float value)        { return this; }
    public ViewPropertyAnimator translationX(float value)   { mView.setTranslationX(value); return this; }
    public ViewPropertyAnimator translationXBy(float value) { return this; }
    public ViewPropertyAnimator translationY(float value)   { mView.setTranslationY(value); return this; }
    public ViewPropertyAnimator translationYBy(float value) { return this; }
    public ViewPropertyAnimator translationZ(float value)   { return this; }
    public ViewPropertyAnimator translationZBy(float value) { return this; }
    public ViewPropertyAnimator scaleX(float value)         { mView.setScaleX(value); return this; }
    public ViewPropertyAnimator scaleXBy(float value)       { return this; }
    public ViewPropertyAnimator scaleY(float value)         { mView.setScaleY(value); return this; }
    public ViewPropertyAnimator scaleYBy(float value)       { return this; }
    public ViewPropertyAnimator rotation(float value)       { mView.setRotation(value); return this; }
    public ViewPropertyAnimator rotationBy(float value)     { return this; }
    public ViewPropertyAnimator rotationX(float value)      { return this; }
    public ViewPropertyAnimator rotationXBy(float value)    { return this; }
    public ViewPropertyAnimator rotationY(float value)      { return this; }
    public ViewPropertyAnimator rotationYBy(float value)    { return this; }
    public ViewPropertyAnimator x(float value)              { mView.setX(value); return this; }
    public ViewPropertyAnimator xBy(float value)            { return this; }
    public ViewPropertyAnimator y(float value)              { mView.setY(value); return this; }
    public ViewPropertyAnimator yBy(float value)            { return this; }
    public ViewPropertyAnimator z(float value)              { return this; }
    public ViewPropertyAnimator zBy(float value)            { return this; }

    public void start() {}
    public void cancel() {}
}
