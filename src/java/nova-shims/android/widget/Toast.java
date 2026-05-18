package android.widget;

import android.content.Context;

public class Toast {
    public static final int LENGTH_SHORT = 0;
    public static final int LENGTH_LONG = 1;

    private final Context mContext;
    private final CharSequence mText;
    private final int mDuration;

    private Toast(Context context, CharSequence text, int duration) {
        mContext = context;
        mText = text;
        mDuration = duration;
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        return new Toast(context, text, duration);
    }

    public static Toast makeText(Context context, int resId, int duration) {
        return new Toast(context, "", duration);
    }

    public void show() {
    }
}
