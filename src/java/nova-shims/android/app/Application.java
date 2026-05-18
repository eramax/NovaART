package android.app;

import android.content.Context;

public class Application extends Context {
    public void onCreate() {}
    public void onTerminate() {}
    public void onLowMemory() {}
    public void onTrimMemory(int level) {}
    public Context getApplicationContext() { return this; }
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks cb) {}
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks cb) {}

    public interface ActivityLifecycleCallbacks {
        default void onActivityCreated(Activity activity, android.os.Bundle savedInstanceState) {}
        default void onActivityStarted(Activity activity) {}
        default void onActivityResumed(Activity activity) {}
        default void onActivityPaused(Activity activity) {}
        default void onActivityStopped(Activity activity) {}
        default void onActivitySaveInstanceState(Activity activity, android.os.Bundle outState) {}
        default void onActivityDestroyed(Activity activity) {}
    }
}
