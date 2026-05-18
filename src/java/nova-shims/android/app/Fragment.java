package android.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment {
    private Activity mActivity;
    private Bundle mArguments;

    public Fragment() {}

    public static <F extends Fragment> F instantiate(Context context, String fname) {
        try { return (F) Class.forName(fname).getDeclaredConstructor().newInstance(); }
        catch (Exception e) { throw new RuntimeException("Unable to instantiate fragment: " + fname, e); }
    }

    public void setArguments(Bundle args) { mArguments = args; }
    public Bundle getArguments() { return mArguments; }
    public Activity getActivity() { return mActivity; }
    public Context getContext() { return mActivity; }
    public View getView() { return null; }
    public int getId() { return 0; }
    public String getTag() { return null; }

    public void onCreate(Bundle savedInstanceState) {}
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return null; }
    public void onViewCreated(View view, Bundle savedInstanceState) {}
    public void onActivityCreated(Bundle savedInstanceState) {}
    public void onStart() {}
    public void onResume() {}
    public void onPause() {}
    public void onStop() {}
    public void onDestroyView() {}
    public void onDestroy() {}
    public void onDetach() {}
    public void onAttach(Context context) {}
    public void onAttach(Activity activity) { mActivity = activity; }

    public boolean isAdded() { return mActivity != null; }
    public boolean isDetached() { return mActivity == null; }
    public boolean isHidden() { return false; }
    public boolean isResumed() { return false; }
    public boolean isVisible() { return false; }

    public FragmentManager getFragmentManager() { return mActivity != null ? mActivity.getFragmentManager() : null; }
    public FragmentManager getChildFragmentManager() { return new FragmentManager.NovaFragmentManager(); }
}
