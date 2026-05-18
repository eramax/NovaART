package android.app;

public abstract class FragmentTransaction {

    public abstract FragmentTransaction add(int containerViewId, Fragment fragment);
    public abstract FragmentTransaction add(int containerViewId, Fragment fragment, String tag);
    public abstract FragmentTransaction add(Fragment fragment, String tag);
    public abstract FragmentTransaction replace(int containerViewId, Fragment fragment);
    public abstract FragmentTransaction replace(int containerViewId, Fragment fragment, String tag);
    public abstract FragmentTransaction remove(Fragment fragment);
    public abstract FragmentTransaction hide(Fragment fragment);
    public abstract FragmentTransaction show(Fragment fragment);
    public abstract FragmentTransaction detach(Fragment fragment);
    public abstract FragmentTransaction attach(Fragment fragment);
    public abstract int commit();
    public abstract int commitAllowingStateLoss();
    public abstract void commitNow();
    public abstract void commitNowAllowingStateLoss();
    public abstract FragmentTransaction setCustomAnimations(int enter, int exit);
    public abstract FragmentTransaction setCustomAnimations(int enter, int exit, int popEnter, int popExit);
    public abstract FragmentTransaction setTransition(int transit);
    public abstract FragmentTransaction addToBackStack(String name);
    public abstract FragmentTransaction disallowAddToBackStack();
    public abstract boolean isAddToBackStackAllowed();
    public abstract boolean isEmpty();

    public static final int TRANSIT_NONE = 0;
    public static final int TRANSIT_FRAGMENT_OPEN = 1;
    public static final int TRANSIT_FRAGMENT_CLOSE = 2;
    public static final int TRANSIT_FRAGMENT_FADE = 3;

    public static class NovaFragmentTransaction extends FragmentTransaction {
        @Override public FragmentTransaction add(int containerViewId, Fragment fragment) { return this; }
        @Override public FragmentTransaction add(int containerViewId, Fragment fragment, String tag) { return this; }
        @Override public FragmentTransaction add(Fragment fragment, String tag) { return this; }
        @Override public FragmentTransaction replace(int containerViewId, Fragment fragment) { return this; }
        @Override public FragmentTransaction replace(int containerViewId, Fragment fragment, String tag) { return this; }
        @Override public FragmentTransaction remove(Fragment fragment) { return this; }
        @Override public FragmentTransaction hide(Fragment fragment) { return this; }
        @Override public FragmentTransaction show(Fragment fragment) { return this; }
        @Override public FragmentTransaction detach(Fragment fragment) { return this; }
        @Override public FragmentTransaction attach(Fragment fragment) { return this; }
        @Override public int commit() { return 0; }
        @Override public int commitAllowingStateLoss() { return 0; }
        @Override public void commitNow() {}
        @Override public void commitNowAllowingStateLoss() {}
        @Override public FragmentTransaction setCustomAnimations(int enter, int exit) { return this; }
        @Override public FragmentTransaction setCustomAnimations(int enter, int exit, int popEnter, int popExit) { return this; }
        @Override public FragmentTransaction setTransition(int transit) { return this; }
        @Override public FragmentTransaction addToBackStack(String name) { return this; }
        @Override public FragmentTransaction disallowAddToBackStack() { return this; }
        @Override public boolean isAddToBackStackAllowed() { return true; }
        @Override public boolean isEmpty() { return true; }
    }
}
