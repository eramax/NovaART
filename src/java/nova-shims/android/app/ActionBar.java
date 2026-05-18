package android.app;

public abstract class ActionBar {
    public abstract void setTitle(CharSequence title);
    public abstract CharSequence getTitle();
    public abstract void hide();
    public abstract void show();
    public abstract boolean isShowing();
    public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {}
    public void setDisplayShowHomeEnabled(boolean showHome) {}
    public void setDisplayShowTitleEnabled(boolean showTitle) {}
    public void setHomeButtonEnabled(boolean enabled) {}
    public void setNavigationMode(int mode) {}
    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_LIST = 1;
    public static final int NAVIGATION_MODE_TABS = 2;
}
