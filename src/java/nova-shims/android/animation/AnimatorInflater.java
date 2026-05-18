package android.animation;

import android.content.Context;

public class AnimatorInflater {
    public static Animator loadAnimator(Context context, int id) {
        return new ValueAnimator();
    }

    public static StateListAnimator loadStateListAnimator(Context context, int id) {
        return new StateListAnimator();
    }
}
