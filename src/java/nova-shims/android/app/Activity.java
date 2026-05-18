package android.app;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class Activity extends Context {
    private final Application mApplication = new Application();
    private View mContentView;

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

    public void setContentView(View view) {
        if (mContentView != null) {
            mContentView.novaDetachFromWindow();
        }
        mContentView = view;
        if (mContentView != null) {
            mContentView.novaAttachToWindow();
        }
    }

    public View getContentView() {
        return mContentView;
    }
}
