package android.animation;

import java.util.ArrayList;
import java.util.List;

public class AnimatorSet extends Animator {
    private List<Animator> mPlayTogether = new ArrayList<>();
    private long mDuration = 300;
    private volatile boolean mRunning = false;

    public class Builder {
        private final Animator mAnim;
        Builder(Animator anim) { mAnim = anim; }
        public Builder with(Animator anim) { mPlayTogether.add(anim); return this; }
        public Builder before(Animator anim) { return this; }
        public Builder after(Animator anim) { return this; }
        public Builder after(long delay) { return this; }
    }

    public Builder play(Animator anim) {
        mPlayTogether.add(anim);
        return new Builder(anim);
    }

    public void playTogether(Animator... items) {
        for (Animator a : items) mPlayTogether.add(a);
    }

    public void playSequentially(Animator... items) {
        for (Animator a : items) mPlayTogether.add(a);
    }

    @Override
    public void start() {
        mRunning = true;
        notifyStart();
        for (Animator a : mPlayTogether) a.start();
    }

    @Override
    public void cancel() {
        mRunning = false;
        for (Animator a : mPlayTogether) a.cancel();
    }

    @Override
    public boolean isRunning() { return mRunning; }

    @Override
    public long getDuration() { return mDuration; }

    @Override
    public Animator setDuration(long duration) {
        mDuration = duration;
        for (Animator a : mPlayTogether) a.setDuration(duration);
        return this;
    }
}
