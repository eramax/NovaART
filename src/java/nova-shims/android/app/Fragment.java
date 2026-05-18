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
        try {
            Class<?> cls = Class.forName(fname, true, context.getClass().getClassLoader());
            return (F) cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate " + fname, e);
        }
    }

    public void setArguments(Bundle args) { mArguments = args; }
    public Bundle getArguments() { return mArguments; }

    public Activity getActivity() { return mActivity; }
    public Context getContext() { return mActivity; }

    public void onAttach(Activity activity) { mActivity = activity; }
    public void onAttach(Context context) {}
    public void onCreate(Bundle savedInstanceState) {}
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return null; }
    public void onViewCreated(View view, Bundle savedInstanceState) {}
    public void onStart() {}
    public void onResume() {}
    public void onPause() {}
    public void onStop() {}
    public void onDestroyView() {}
    public void onDestroy() {}
    public void onDetach() {}
}
