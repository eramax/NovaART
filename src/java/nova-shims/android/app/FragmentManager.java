package android.app;

public abstract class FragmentManager {

    public abstract FragmentTransaction beginTransaction();
    public abstract Fragment findFragmentById(int id);
    public abstract Fragment findFragmentByTag(String tag);
    public abstract void popBackStack();
    public abstract boolean popBackStackImmediate();
    public abstract void popBackStack(String name, int flags);
    public abstract boolean popBackStackImmediate(String name, int flags);
    public abstract int getBackStackEntryCount();
    public abstract BackStackEntry getBackStackEntryAt(int index);

    public static final int POP_BACK_STACK_INCLUSIVE = 1;

    public interface OnBackStackChangedListener {
        void onBackStackChanged();
    }

    public interface BackStackEntry {
        int getId();
        String getName();
        int getBreadCrumbTitleRes();
        int getBreadCrumbShortTitleRes();
        CharSequence getBreadCrumbTitle();
        CharSequence getBreadCrumbShortTitle();
    }

    public static class NovaFragmentManager extends FragmentManager {
        @Override public FragmentTransaction beginTransaction() { return new FragmentTransaction.NovaFragmentTransaction(); }
        @Override public Fragment findFragmentById(int id) { return null; }
        @Override public Fragment findFragmentByTag(String tag) { return null; }
        @Override public void popBackStack() {}
        @Override public boolean popBackStackImmediate() { return false; }
        @Override public void popBackStack(String name, int flags) {}
        @Override public boolean popBackStackImmediate(String name, int flags) { return false; }
        @Override public int getBackStackEntryCount() { return 0; }
        @Override public BackStackEntry getBackStackEntryAt(int index) { return null; }
    }
}
