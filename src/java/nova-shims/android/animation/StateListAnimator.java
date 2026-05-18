package android.animation;

public class StateListAnimator implements Cloneable {
    public void addState(int[] specs, Animator animator) {}
    public void jumpToCurrentState() {}
    @Override
    public StateListAnimator clone() {
        try { return (StateListAnimator) super.clone(); }
        catch (CloneNotSupportedException e) { throw new RuntimeException(e); }
    }
}
