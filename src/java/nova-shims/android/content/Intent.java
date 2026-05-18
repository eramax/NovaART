package android.content;

import android.os.Bundle;

public class Intent {
    private String mAction;
    private Class<?> mTargetClass;
    private String mPackageName;
    private Bundle mExtras;

    public Intent() {
    }

    public Intent(String action) {
        mAction = action;
    }

    public Intent(Context context, Class<?> cls) {
        mTargetClass = cls;
        if (context != null) {
            mPackageName = context.getPackageName();
        }
    }

    public String getAction() {
        return mAction;
    }

    public Bundle getExtras() {
        return mExtras;
    }

    public String getPackage() {
        return mPackageName;
    }

    public Class<?> getTargetClass() {
        return mTargetClass;
    }

    public boolean getBooleanExtra(String name, boolean defaultValue) {
        return mExtras != null ? mExtras.getBoolean(name, defaultValue) : defaultValue;
    }

    public int getIntExtra(String name, int defaultValue) {
        return mExtras != null ? mExtras.getInt(name, defaultValue) : defaultValue;
    }

    public String getStringExtra(String name) {
        return mExtras != null ? mExtras.getString(name) : null;
    }

    public Intent putExtra(String name, boolean value) {
        ensureExtras().putBoolean(name, value);
        return this;
    }

    public Intent putExtra(String name, int value) {
        ensureExtras().putInt(name, value);
        return this;
    }

    public Intent putExtra(String name, String value) {
        ensureExtras().putString(name, value);
        return this;
    }

    public Intent setAction(String action) {
        mAction = action;
        return this;
    }

    public Intent setClass(Context context, Class<?> cls) {
        mTargetClass = cls;
        if (context != null) {
            mPackageName = context.getPackageName();
        }
        return this;
    }

    public Intent setClassName(String packageName, String className) {
        mPackageName = packageName;
        try {
            mTargetClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            mTargetClass = null;
        }
        return this;
    }

    private Bundle ensureExtras() {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        return mExtras;
    }
}
