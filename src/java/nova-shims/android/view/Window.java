package android.view;

public class Window {
    public static final int FEATURE_NO_TITLE = 1;

    private int mFlags;

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
}
