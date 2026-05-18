package android.animation;

public abstract class Animator implements Cloneable {
    public interface AnimatorListener {
        void onAnimationStart(Animator animation);
        void onAnimationEnd(Animator animation);
        void onAnimationCancel(Animator animation);
        void onAnimationRepeat(Animator animation);
    }

    public interface AnimatorPauseListener {
        void onAnimationPause(Animator animation);
        void onAnimationResume(Animator animation);
    }

    private java.util.List<AnimatorListener> mListeners;

    public void addListener(AnimatorListener listener) {
        if (mListeners == null) mListeners = new java.util.ArrayList<>();
        mListeners.add(listener);
    }

    public void removeListener(AnimatorListener listener) {
        if (mListeners != null) mListeners.remove(listener);
    }

    public void removeAllListeners() {
        if (mListeners != null) mListeners.clear();
    }

    public java.util.ArrayList<AnimatorListener> getListeners() {
        return mListeners != null ? new java.util.ArrayList<>(mListeners) : new java.util.ArrayList<>();
    }

    protected void notifyStart() {
        if (mListeners != null)
            for (AnimatorListener l : new java.util.ArrayList<>(mListeners)) l.onAnimationStart(this);
    }

    protected void notifyEnd() {
        if (mListeners != null)
            for (AnimatorListener l : new java.util.ArrayList<>(mListeners)) l.onAnimationEnd(this);
    }

    protected void notifyRepeat() {
        if (mListeners != null)
            for (AnimatorListener l : new java.util.ArrayList<>(mListeners)) l.onAnimationRepeat(this);
    }

    public abstract void start();
    public abstract void cancel();
    public abstract boolean isRunning();
    public abstract long getDuration();
    public abstract Animator setDuration(long duration);

    public void addPauseListener(AnimatorPauseListener listener) {}
    public void removePauseListener(AnimatorPauseListener listener) {}
    public void pause() {}
    public void resume() {}
    public boolean isPaused() { return false; }
    public boolean isStarted() { return isRunning(); }
    public long getStartDelay() { return 0; }
    public void setStartDelay(long startDelay) {}
    public void setTarget(Object target) {}

    @Override
    public Animator clone() {
        try { return (Animator) super.clone(); }
        catch (CloneNotSupportedException e) { throw new RuntimeException(e); }
    }
}
