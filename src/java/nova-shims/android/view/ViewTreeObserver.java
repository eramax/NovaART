package android.view;

public final class ViewTreeObserver {
    public interface OnGlobalLayoutListener {
        void onGlobalLayout();
    }
    public interface OnPreDrawListener {
        boolean onPreDraw();
    }
    public interface OnScrollChangedListener {
        void onScrollChanged();
    }
    public interface OnTouchModeChangeListener {
        void onTouchModeChanged(boolean isInTouchMode);
    }
    public interface OnWindowFocusChangeListener {
        void onWindowFocusChanged(boolean hasFocus);
    }
    public interface OnWindowAttachListener {
        void onWindowAttached();
        void onWindowDetached();
    }
    public interface OnDrawListener {
        void onDraw();
    }

    public void addOnGlobalLayoutListener(OnGlobalLayoutListener listener) {}
    public void removeOnGlobalLayoutListener(OnGlobalLayoutListener listener) {}
    public void addOnPreDrawListener(OnPreDrawListener listener) {}
    public void removeOnPreDrawListener(OnPreDrawListener listener) {}
    public void addOnScrollChangedListener(OnScrollChangedListener listener) {}
    public void removeOnScrollChangedListener(OnScrollChangedListener listener) {}
    public void addOnTouchModeChangeListener(OnTouchModeChangeListener listener) {}
    public void removeOnTouchModeChangeListener(OnTouchModeChangeListener listener) {}
    public void addOnWindowFocusChangeListener(OnWindowFocusChangeListener listener) {}
    public void removeOnWindowFocusChangeListener(OnWindowFocusChangeListener listener) {}
    public void addOnWindowAttachListener(OnWindowAttachListener listener) {}
    public void removeOnWindowAttachListener(OnWindowAttachListener listener) {}
    public void addOnDrawListener(OnDrawListener listener) {}
    public void removeOnDrawListener(OnDrawListener listener) {}

    public void dispatchOnGlobalLayout() {}
    public boolean dispatchOnPreDraw() { return true; }

    public boolean isAlive() { return true; }
    public void merge(ViewTreeObserver observer) {}
}
