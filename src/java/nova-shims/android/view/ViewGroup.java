package android.view;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class ViewGroup extends View {
    protected List<View> mChildren = new ArrayList<>();

    public static class LayoutParams {
        public static final int MATCH_PARENT = -1;
        public static final int WRAP_CONTENT = -2;
        public int width;
        public int height;
        public LayoutParams(int width, int height) { this.width = width; this.height = height; }
        public LayoutParams(LayoutParams source) { this.width = source.width; this.height = source.height; }
    }

    public static class MarginLayoutParams extends LayoutParams {
        public int leftMargin, topMargin, rightMargin, bottomMargin;
        public MarginLayoutParams(int w, int h) { super(w, h); }
        public MarginLayoutParams(LayoutParams source) { super(source); }
    }

    public ViewGroup(Context context) {
        super(context);
    }

    public void addView(View child) {
        if (child == null) return;
        mChildren.add(child);
    }

    public void addView(View child, int index) {
        if (child == null) return;
        if (index < 0 || index >= mChildren.size()) mChildren.add(child);
        else mChildren.add(index, child);
    }

    public void addView(View child, LayoutParams params) {
        if (child == null) return;
        mChildren.add(child);
    }

    public void addView(View child, int width, int height) {
        addView(child, new LayoutParams(width, height));
    }

    public void addView(View child, int index, LayoutParams params) {
        addView(child, index);
    }

    public LayoutParams generateLayoutParams(android.util.AttributeSet attrs) {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public void removeView(View child) {
        mChildren.remove(child);
    }

    public int getChildCount() {
        return mChildren.size();
    }

    public View getChildAt(int index) {
        if (index < 0 || index >= mChildren.size()) {
            return null;
        }
        return mChildren.get(index);
    }

    public void removeAllViews() {
        mChildren.clear();
    }
}
