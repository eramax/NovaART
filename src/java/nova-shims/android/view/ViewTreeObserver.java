package android.view;

import java.util.ArrayList;
import java.util.List;

public class ViewTreeObserver {
    public interface OnDrawListener {
        void onDraw();
    }

    private final List<OnDrawListener> mOnDrawListeners = new ArrayList<>();

    public void addOnDrawListener(OnDrawListener listener) {
        if (listener != null && !mOnDrawListeners.contains(listener)) {
            mOnDrawListeners.add(listener);
        }
    }

    public boolean isAlive() {
        return true;
    }

    public void removeOnDrawListener(OnDrawListener victim) {
        mOnDrawListeners.remove(victim);
    }
}
