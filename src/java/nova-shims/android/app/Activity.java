package android.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.util.Log;

public class Activity extends Context {
    private static final String TAG = "NovaActivity";
    private final Application mApplication = new Application();
    private final Window mWindow = new Window();
    private View mContentView;
    private boolean mFinished;
    private Intent mIntent;

    public Activity() {
    }

    protected void onCreate(Bundle icicle) {
    }

    protected void onPause() {
    }

    protected void onResume() {
    }

    public Application getApplication() {
        return mApplication;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public Window getWindow() {
        return mWindow;
    }

    public WindowManager getWindowManager() {
        return (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }

    public android.content.SharedPreferences getPreferences(int mode) {
        return getSharedPreferences(getClass().getSimpleName(), mode);
    }

    public void setVolumeControlStream(int streamType) {}
    public void setRequestedOrientation(int requestedOrientation) {}
    public void runOnUiThread(Runnable action) { action.run(); }

    public boolean isFinishing() {
        return mFinished;
    }

    public boolean requestWindowFeature(int featureId) {
        return true;
    }

    public void finish() {
        mFinished = true;
    }

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    public void setContentView(View view) {
        Log.d(TAG, "setContentView(View) called with " + (view != null ? view.getClass().getName() : "null"));
        if (mContentView != null) {
            mContentView.novaDetachFromWindow();
        }
        mContentView = view;
        if (mContentView != null) {
            mContentView.novaAttachToWindow();
        }
    }

    public void setContentView(int layoutResId) {
        Log.d(TAG, "setContentView(int) called with layoutResId=" + layoutResId);
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
        View inflatedView = inflater.inflate(layoutResId, null);
        if (inflatedView != null) {
            setContentView(inflatedView);
        } else {
            Log.e(TAG, "Failed to inflate layout, using fallback");
            setContentView(new View(this));
        }
    }

    public View findViewById(int id) {
        if (mContentView instanceof WebView) {
            return mContentView;
        }
        return new WebView(this);
    }

    public View getContentView() {
        return mContentView;
    }
}
