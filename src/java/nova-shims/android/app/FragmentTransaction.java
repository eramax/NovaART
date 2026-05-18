package android.app;

public abstract class FragmentTransaction {
    public abstract FragmentTransaction add(Fragment fragment, String tag);
    public abstract FragmentTransaction add(int containerViewId, Fragment fragment);
    public abstract FragmentTransaction add(int containerViewId, Fragment fragment, String tag);
    public abstract FragmentTransaction replace(int containerViewId, Fragment fragment);
    public abstract FragmentTransaction replace(int containerViewId, Fragment fragment, String tag);
    public abstract FragmentTransaction remove(Fragment fragment);
    public abstract FragmentTransaction show(Fragment fragment);
    public abstract FragmentTransaction hide(Fragment fragment);
    public abstract FragmentTransaction attach(Fragment fragment);
    public abstract FragmentTransaction detach(Fragment fragment);
    public abstract FragmentTransaction addToBackStack(String name);
    public abstract int commit();
    public abstract int commitAllowingStateLoss();
    public abstract void commitNow();

    public static class NovaFragmentTransaction extends FragmentTransaction {
        @Override public FragmentTransaction add(Fragment f, String t) { return this; }
        @Override public FragmentTransaction add(int c, Fragment f) { return this; }
        @Override public FragmentTransaction add(int c, Fragment f, String t) { return this; }
        @Override public FragmentTransaction replace(int c, Fragment f) { return this; }
        @Override public FragmentTransaction replace(int c, Fragment f, String t) { return this; }
        @Override public FragmentTransaction remove(Fragment f) { return this; }
        @Override public FragmentTransaction show(Fragment f) { return this; }
        @Override public FragmentTransaction hide(Fragment f) { return this; }
        @Override public FragmentTransaction attach(Fragment f) { return this; }
        @Override public FragmentTransaction detach(Fragment f) { return this; }
        @Override public FragmentTransaction addToBackStack(String name) { return this; }
        @Override public int commit() { return 0; }
        @Override public int commitAllowingStateLoss() { return 0; }
        @Override public void commitNow() {}
    }
}
