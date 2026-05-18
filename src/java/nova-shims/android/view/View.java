package android.view;

import android.content.Context;
import android.util.AttributeSet;

public class View {
    private final Context mContext;

    public View(Context context) {
        mContext = context;
    }

    public View(Context context, AttributeSet attrs) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    protected void onAttachedToWindow() {
    }

    protected void onDetachedFromWindow() {
    }
}
