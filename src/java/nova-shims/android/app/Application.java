package android.app;

import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import java.util.ArrayList;

public class Application extends ContextWrapper implements ComponentCallbacks2 {

    private final ArrayList<ActivityLifecycleCallbacks> mActivityLifecycleCallbacks = new ArrayList<>();

    public interface ActivityLifecycleCallbacks {
        default void onActivityPreCreated(Activity activity, Bundle savedInstanceState) {}
        void onActivityCreated(Activity activity, Bundle savedInstanceState);
        default void onActivityPostCreated(Activity activity, Bundle savedInstanceState) {}
        default void onActivityPreStarted(Activity activity) {}
        void onActivityStarted(Activity activity);
        default void onActivityPostStarted(Activity activity) {}
        default void onActivityPreResumed(Activity activity) {}
        void onActivityResumed(Activity activity);
        default void onActivityPostResumed(Activity activity) {}
        default void onActivityPrePaused(Activity activity) {}
        void onActivityPaused(Activity activity);
        default void onActivityPostPaused(Activity activity) {}
        default void onActivityPreStopped(Activity activity) {}
        void onActivityStopped(Activity activity);
        default void onActivityPostStopped(Activity activity) {}
        default void onActivityPreSaveInstanceState(Activity activity, Bundle outState) {}
        void onActivitySaveInstanceState(Activity activity, Bundle outState);
        default void onActivityPostSaveInstanceState(Activity activity, Bundle outState) {}
        default void onActivityPreDestroyed(Activity activity) {}
        void onActivityDestroyed(Activity activity);
        default void onActivityPostDestroyed(Activity activity) {}
    }

    public interface OnProvideAssistDataListener {
        void onProvideAssistData(Activity activity, Bundle data);
    }

    public Application() { super(null); }

    public void onCreate() {}
    public void onTerminate() {}

    @Override public void onConfigurationChanged(Configuration newConfig) {}
    @Override public void onLowMemory() {}
    @Override public void onTrimMemory(int level) {}

    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        if (callback != null) mActivityLifecycleCallbacks.add(callback);
    }
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        mActivityLifecycleCallbacks.remove(callback);
    }

    public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback) {}
    public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener callback) {}

    public void registerComponentCallbacks(ComponentCallbacks callback) {}
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {}

    @Override public Context getApplicationContext() { return this; }
}
