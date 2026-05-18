package android.app;

import android.os.Bundle;

public abstract class FragmentManager {
    public abstract Fragment findFragmentById(int id);
    public abstract Fragment findFragmentByTag(String tag);
    public abstract FragmentTransaction beginTransaction();
    public abstract boolean executePendingTransactions();
    public abstract void popBackStack();
    public abstract boolean popBackStackImmediate();
    public abstract int getBackStackEntryCount();
    public abstract void addOnBackStackChangedListener(OnBackStackChangedListener listener);
    public abstract void removeOnBackStackChangedListener(OnBackStackChangedListener listener);

    public interface OnBackStackChangedListener {
        void onBackStackChanged();
    }

    public static class NovaFragmentManager extends FragmentManager {
        @Override public Fragment findFragmentById(int id) { return null; }
        @Override public Fragment findFragmentByTag(String tag) { return null; }
        @Override public FragmentTransaction beginTransaction() { return new FragmentTransaction.NovaFragmentTransaction(); }
        @Override public boolean executePendingTransactions() { return false; }
        @Override public void popBackStack() {}
        @Override public boolean popBackStackImmediate() { return false; }
        @Override public int getBackStackEntryCount() { return 0; }
        @Override public void addOnBackStackChangedListener(OnBackStackChangedListener l) {}
        @Override public void removeOnBackStackChangedListener(OnBackStackChangedListener l) {}
    }
}
