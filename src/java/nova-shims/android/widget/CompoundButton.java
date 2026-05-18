package android.widget;

public class CompoundButton {
    public void draw(android.graphics.Canvas p0) {}
    public void drawableHotspotChanged(float p0, float p1) {}
    public void drawableStateChanged() {}
    public android.graphics.drawable.Drawable getBackground() { return null; }
    public android.graphics.drawable.Drawable getButtonDrawable() { return null; }
    public android.content.res.ColorStateList getButtonTintList() { return null; }
    public android.graphics.PorterDuff.Mode getButtonTintMode() { return null; }
    public int getCompoundPaddingLeft() { return 0; }
    public int getCompoundPaddingRight() { return 0; }
    public android.content.Context getContext() { return null; }
    public android.view.ActionMode.Callback getCustomSelectionActionModeCallback() { return null; }
    public int[] getDrawableState() { return null; }
    public int getGravity() { return 0; }
    public int getHeight() { return 0; }
    public int getId() { return 0; }
    public int getMeasuredHeight() { return 0; }
    public int getMeasuredWidthAndState() { return 0; }
    public int getPaddingBottom() { return 0; }
    public int getPaddingLeft() { return 0; }
    public int getPaddingRight() { return 0; }
    public int getPaddingTop() { return 0; }
    public android.view.ViewParent getParent() { return null; }
    public android.content.res.Resources getResources() { return null; }
    public java.lang.CharSequence getText() { return null; }
    public android.content.res.ColorStateList getTextColors() { return null; }
    public int getVisibility() { return 0; }
    public int getWidth() { return 0; }
    public android.os.IBinder getWindowToken() { return null; }
    public void invalidate() {}
    public boolean isChecked() { return false; }
    public boolean isEnabled() { return false; }
    public void jumpDrawablesToCurrentState() {}
    public int[] mergeDrawableStates(int[] p0, int[] p1) { return null; }
    public void onAttachedToWindow() {}
    public int[] onCreateDrawableState(int p0) { return null; }
    public void onDraw(android.graphics.Canvas p0) {}
    public void onInitializeAccessibilityEvent(android.view.accessibility.AccessibilityEvent p0) {}
    public void onInitializeAccessibilityNodeInfo(android.view.accessibility.AccessibilityNodeInfo p0) {}
    public void onLayout(boolean p0, int p1, int p2, int p3, int p4) {}
    public void onMeasure(int p0, int p1) {}
    public void onPopulateAccessibilityEvent(android.view.accessibility.AccessibilityEvent p0) {}
    public boolean onTouchEvent(android.view.MotionEvent p0) { return false; }
    public void playSoundEffect(int p0) {}
    public void refreshDrawableState() {}
    public void requestLayout() {}
    public void setAllCaps(boolean p0) {}
    public void setButtonDrawable(android.graphics.drawable.Drawable p0) {}
    public void setButtonTintList(android.content.res.ColorStateList p0) {}
    public void setButtonTintMode(android.graphics.PorterDuff.Mode p0) {}
    public void setChecked(boolean p0) {}
    public void setCustomSelectionActionModeCallback(android.view.ActionMode.Callback p0) {}
    public void setEnabled(boolean p0) {}
    public void setFilters(android.text.InputFilter[] p0) {}
    public void setMeasuredDimension(int p0, int p1) {}
    public void setOnCheckedChangeListener(android.widget.CompoundButton.OnCheckedChangeListener p0) {}
    public void setOnClickListener(android.view.View.OnClickListener p0) {}
    public void setText(java.lang.CharSequence p0) {}
    public void setVisibility(int p0) {}
    public void toggle() {}
    public boolean verifyDrawable(android.graphics.drawable.Drawable p0) { return false; }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(android.widget.CompoundButton p0, boolean p1);
    }
}
