package android.widget;

public class PopupWindow {
    public void dismiss() {}
    public int getAnimationStyle() { return 0; }
    public android.graphics.drawable.Drawable getBackground() { return null; }
    public android.view.View getContentView() { return null; }
    public int getHeight() { return 0; }
    public int getInputMethodMode() { return 0; }
    public int getMaxAvailableHeight(android.view.View p0, int p1) { return 0; }
    public int getMaxAvailableHeight(android.view.View p0, int p1, boolean p2) { return 0; }
    public boolean getOverlapAnchor() { return false; }
    public int getSoftInputMode() { return 0; }
    public int getWidth() { return 0; }
    public int getWindowLayoutType() { return 0; }
    public boolean isAboveAnchor() { return false; }
    public boolean isShowing() { return false; }
    public void setAnimationStyle(int p0) {}
    public void setAttachedInDecor(boolean p0) {}
    public void setBackgroundDrawable(android.graphics.drawable.Drawable p0) {}
    public void setClippingEnabled(boolean p0) {}
    public void setContentView(android.view.View p0) {}
    public void setElevation(float p0) {}
    public void setEnterTransition(android.transition.Transition p0) {}
    public void setEpicenterBounds(android.graphics.Rect p0) {}
    public void setExitTransition(android.transition.Transition p0) {}
    public void setFocusable(boolean p0) {}
    public void setHeight(int p0) {}
    public void setInputMethodMode(int p0) {}
    public void setIsClippedToScreen(boolean p0) {}
    public void setOnDismissListener(android.widget.PopupWindow.OnDismissListener p0) {}
    public void setOutsideTouchable(boolean p0) {}
    public void setOverlapAnchor(boolean p0) {}
    public void setSoftInputMode(int p0) {}
    public void setTouchInterceptor(android.view.View.OnTouchListener p0) {}
    public void setTouchModal(boolean p0) {}
    public void setTouchable(boolean p0) {}
    public void setWidth(int p0) {}
    public void setWindowLayoutMode(int p0, int p1) {}
    public void setWindowLayoutType(int p0) {}
    public void showAsDropDown(android.view.View p0, int p1, int p2) {}
    public void showAsDropDown(android.view.View p0, int p1, int p2, int p3) {}
    public void showAsDropDown(android.view.View p0) {}
    public void showAtLocation(android.view.View p0, int p1, int p2, int p3) {}
    public void update(android.view.View p0, int p1, int p2, int p3, int p4) {}
    public void update(int p0, int p1, int p2, int p3) {}

    public interface OnDismissListener {
        void onDismiss();
    }
}
