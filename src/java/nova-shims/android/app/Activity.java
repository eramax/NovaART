package android.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

public class Activity extends Context {
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
        if (mContentView != null) {
            mContentView.novaDetachFromWindow();
        }
        mContentView = view;
        if (mContentView != null) {
            mContentView.novaAttachToWindow();
        }
    }

    public void setContentView(int layoutResId) {
        setContentView(new View(this));
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
