package android.widget;

import android.content.Context;
import android.view.ViewGroup;

public class LinearLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int mOrientation = VERTICAL;
    private int mGravity = 0;

    public LinearLayout(Context context) {
        super(context);
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
    }
}
