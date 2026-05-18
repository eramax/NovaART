package android.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.util.Log;
import android.content.res.Configuration;

public class Activity extends ContextWrapper {
    private static final String TAG = "NovaActivity";
    private Application mApplication;
    private final Window mWindow = new Window(this);
    private View mContentView;
    private boolean mFinished;
    private Intent mIntent;

    public Activity() {
        super(null);
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

    @Override
    public Object getSystemService(String name) {
        if (Context.WINDOW_SERVICE.equals(name)) {
            return new WindowManager() {
                private final Display mDefaultDisplay = new Display();
                @Override public Display getDefaultDisplay() { return mDefaultDisplay; }
                @Override public void addView(View view, android.view.ViewGroup.LayoutParams params) {}
                @Override public void updateViewLayout(View view, android.view.ViewGroup.LayoutParams params) {}
                @Override public void removeView(View view) {}
                @Override public void removeViewImmediate(View view) {}
            };
        }
        if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            return android.view.LayoutInflater.from(this);
        }
        if (Context.POWER_SERVICE.equals(name) || Context.DISPLAY_SERVICE.equals(name)) {
            return null;
        }
        return null;
    }

    @Override
    public String getSystemServiceName(Class<?> serviceClass) { return null; }

    private final FragmentManager mFragmentManager = new FragmentManager.NovaFragmentManager();

    public FragmentManager getFragmentManager() { return mFragmentManager; }

    public Object getLastNonConfigurationInstance() { return null; }
    public Object onRetainNonConfigurationInstance() { return null; }
    public void onConfigurationChanged(Configuration newConfig) {}
    public void onSaveInstanceState(android.os.Bundle outState) {}
    public void onRestoreInstanceState(android.os.Bundle savedInstanceState) {}
    public void onStart() {}
    public void onStop() {}
    public void onDestroy() {}
    public void invalidateOptionsMenu() {}
    public boolean onCreateOptionsMenu(android.view.Menu menu) { return false; }
    public boolean onOptionsItemSelected(android.view.MenuItem item) { return false; }
    public void supportInvalidateOptionsMenu() {}
    public android.view.MenuInflater getMenuInflater() { return null; }
    public void onBackPressed() {}
    public void onWindowFocusChanged(boolean hasFocus) {}
    public android.app.ActionBar getActionBar() { return null; }

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
            setContentView(new android.widget.LinearLayout(this));
        }
    }

    public <T extends View> T findViewById(int id) {
        return (T) mContentView;
    }

    public View getContentView() {
        return mContentView;
    }
}
