package android.view;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class ViewGroup extends View {
    protected List<View> mChildren = new ArrayList<>();

    public ViewGroup(Context context) {
        super(context);
    }

    public void addView(View child) {
        if (child == null) {
            return;
        }
        mChildren.add(child);
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
