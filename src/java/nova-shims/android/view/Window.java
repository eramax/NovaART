package android.view;

import android.content.Context;

public class Window {

    public interface Callback {
        boolean dispatchKeyEvent(KeyEvent event);
        boolean dispatchTouchEvent(MotionEvent event);
        void onWindowFocusChanged(boolean hasFocus);
        void onAttachedToWindow();
        void onDetachedFromWindow();
        boolean onMenuItemSelected(int featureId, android.view.MenuItem item);
        void onWindowAttributesChanged(WindowManager.LayoutParams attrs);
        void onContentChanged();
    }

    private final Context mContext;
    private WindowManager.LayoutParams mAttributes = new WindowManager.LayoutParams();
    private Callback mCallback;
    private View mDecorView;
    private int mFeatures;

    public Window(Context context) { mContext = context; }
    public Window() { mContext = null; }

    public Context getContext() { return mContext; }

    public void setCallback(Callback callback) { mCallback = callback; }
    public Callback getCallback() { return mCallback; }

    public void setContentView(View view) { mDecorView = view; }
    public void setContentView(int layoutResID) {}
    public void addContentView(View view, ViewGroup.LayoutParams params) {}
    public View getDecorView() { return mDecorView; }
    public View peekDecorView() { return mDecorView; }

    public WindowManager.LayoutParams getAttributes() { return mAttributes; }
    public void setAttributes(WindowManager.LayoutParams a) { mAttributes = a; }

    public void setFlags(int flags, int mask) {
        mAttributes.flags = (mAttributes.flags & ~mask) | (flags & mask);
    }

    public void addFlags(int flags) { mAttributes.flags |= flags; }
    public void clearFlags(int flags) { mAttributes.flags &= ~flags; }

    public boolean hasFeature(int feature) { return (mFeatures & (1 << feature)) != 0; }
    public boolean requestFeature(int featureId) { mFeatures |= (1 << featureId); return true; }

    public static final int FEATURE_NO_TITLE          = 1;
    public static final int FEATURE_INDETERMINATE_PROGRESS = 5;
    public static final int FEATURE_SWIPE_TO_DISMISS  = 11;
    public static final int FEATURE_CONTENT_TRANSITIONS = 12;
    public static final int FEATURE_ACTIVITY_TRANSITIONS = 13;
    public static final int FEATURE_ACTION_BAR        = 8;
    public static final int FEATURE_ACTION_BAR_OVERLAY = 9;
    public static final int FEATURE_ACTION_MODE_OVERLAY = 10;
    public static final int FEATURE_LEFT_ICON         = 3;
    public static final int FEATURE_RIGHT_ICON        = 4;
    public static final int FEATURE_OPTIONS_PANEL     = 0;
    public static final int FEATURE_CONTEXT_MENU      = 6;
    public static final int FEATURE_CUSTOM_TITLE      = 7;

    public void setTitle(CharSequence title) {}
    public void setTitleColor(int textColor) {}
    public void setSoftInputMode(int mode) { mAttributes.softInputMode = mode; }
    public void setWindowManager(WindowManager wm, android.os.IBinder appToken, String appName) {}
    public void setWindowManager(WindowManager wm, android.os.IBinder appToken, String appName, boolean hardwareAccelerated) {}
    public WindowManager getWindowManager() { return null; }
    public void takeSurface(android.view.SurfaceHolder.Callback2 callback) {}
    public void takeInputQueue(android.view.InputQueue.Callback callback) {}

    public void setBackgroundDrawable(android.graphics.drawable.Drawable d) {}
    public void setBackgroundDrawableResource(int resid) {}
    public void setElevation(float elevation) {}
    public float getElevation() { return 0f; }
    public void setClipToOutline(boolean clipToOutline) {}
    public void setGravity(int gravity) { mAttributes.gravity = gravity; }
    public void setLayout(int width, int height) { mAttributes.width = width; mAttributes.height = height; }
    public void setType(int type) { mAttributes.type = type; }
    public void setFormat(int format) { mAttributes.format = format; }

    public android.content.res.Resources getResources() { return mContext != null ? mContext.getResources() : null; }
    public View getCurrentFocus() { return null; }
    public View findViewById(int id) { return null; }
    public <T extends View> T requireViewById(int id) { return null; }
    public boolean performContextMenuIdentifierAction(int id, int flags) { return false; }
    public boolean performPanelIdentifierAction(int featureId, int id, int flags) { return false; }
    public boolean performPanelShortcut(int featureId, int keyCode, KeyEvent event, int flags) { return false; }
    public void invalidatePanelMenu(int featureId) {}
    public void closePanel(int featureId) {}
    public void openPanel(int featureId, KeyEvent event) {}
    public void togglePanel(int featureId, KeyEvent event) {}
    public void setStatusBarColor(int color) {}
    public void setNavigationBarColor(int color) {}
    public int getStatusBarColor() { return 0; }
    public int getNavigationBarColor() { return 0; }
    public void setDecorCaptionShade(int decorCaptionShade) {}
    public void setResizingCaptionDrawable(android.graphics.drawable.Drawable drawable) {}
    public android.transition.TransitionManager getTransitionManager() { return null; }
    public void setTransitionManager(android.transition.TransitionManager tm) {}
    public android.transition.Scene getContentScene() { return null; }
    public void setAllowEnterTransitionOverlap(boolean allow) {}
    public boolean getAllowEnterTransitionOverlap() { return false; }
    public void setAllowReturnTransitionOverlap(boolean allow) {}
    public boolean getAllowReturnTransitionOverlap() { return false; }
    public void setSharedElementsUseOverlay(boolean use) {}
    public boolean getSharedElementsUseOverlay() { return false; }
    public void setEnterTransition(android.transition.Transition transition) {}
    public void setReturnTransition(android.transition.Transition transition) {}
    public void setExitTransition(android.transition.Transition transition) {}
    public void setReenterTransition(android.transition.Transition transition) {}
    public android.transition.Transition getEnterTransition() { return null; }
    public android.transition.Transition getReturnTransition() { return null; }
    public android.transition.Transition getExitTransition() { return null; }
    public android.transition.Transition getReenterTransition() { return null; }
    public void setSharedElementEnterTransition(android.transition.Transition transition) {}
    public void setSharedElementReturnTransition(android.transition.Transition transition) {}
    public void setSharedElementExitTransition(android.transition.Transition transition) {}
    public void setSharedElementReenterTransition(android.transition.Transition transition) {}
    public android.transition.Transition getSharedElementEnterTransition() { return null; }
    public android.transition.Transition getSharedElementReturnTransition() { return null; }
    public android.transition.Transition getSharedElementExitTransition() { return null; }
    public android.transition.Transition getSharedElementReenterTransition() { return null; }
}
