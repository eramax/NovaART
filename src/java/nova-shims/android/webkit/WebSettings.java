package android.webkit;

public class WebSettings {
    public enum RenderPriority {
        NORMAL,
        HIGH,
        LOW
    }

    public enum LayoutAlgorithm {
        NORMAL,
        SINGLE_COLUMN,
        NARROW_COLUMNS,
        TEXT_AUTOSIZING
    }

    private boolean mJavaScriptEnabled;

    public void setJavaScriptEnabled(boolean enabled) {
        mJavaScriptEnabled = enabled;
    }

    public boolean getJavaScriptEnabled() {
        return mJavaScriptEnabled;
    }

    public void setBuiltInZoomControls(boolean enabled) {
    }

    public void setDatabaseEnabled(boolean enabled) {
    }

    public void setDatabasePath(String path) {
    }

    public void setDefaultTextEncodingName(String encoding) {
    }

    public void setDisplayZoomControls(boolean enabled) {
    }

    public void setJavaScriptCanOpenWindowsAutomatically(boolean enabled) {
    }

    public void setDomStorageEnabled(boolean enabled) {
    }

    public void setAppCacheEnabled(boolean enabled) {
    }

    public void setAppCachePath(String appCachePath) {
    }

    public void setAllowContentAccess(boolean allow) {
    }

    public void setAllowFileAccess(boolean allow) {
    }

    public void setLoadWithOverviewMode(boolean enabled) {
    }

    public void setMixedContentMode(int mode) {
    }

    public void setTextZoom(int textZoom) {
    }

    public void setRenderPriority(RenderPriority priority) {
    }

    public void setSupportZoom(boolean enabled) {
    }

    public void setSupportMultipleWindows(boolean supported) {
    }

    public void setGeolocationEnabled(boolean enabled) {
    }

    public void setUserAgentString(String ua) {
    }

    public void setUseWideViewPort(boolean enabled) {
    }
}
