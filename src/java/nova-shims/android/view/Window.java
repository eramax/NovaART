package android.view;

import android.content.Context;

public class Window {
    public static final int FEATURE_NO_TITLE = 1;
    public static final int FEATURE_ACTION_BAR = 8;

    private int mFlags;
    private final View mDecorView = new View(null);

    public Window() {
    }

    public void addFlags(int flags) {
        mFlags |= flags;
    }

    public void clearFlags(int flags) {
        mFlags &= ~flags;
    }

    public int getFlags() {
        return mFlags;
    }

    public void setFlags(int flags, int mask) {
        mFlags = (mFlags & ~mask) | (flags & mask);
    }

    public View getDecorView() {
        return mDecorView;
    }

    public View peekDecorView() {
        return mDecorView;
    }

    public void setSoftInputMode(int mode) {}

    public void setBackgroundDrawable(android.graphics.drawable.Drawable d) {}
}
