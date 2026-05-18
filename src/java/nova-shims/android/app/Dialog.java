package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class Dialog implements DialogInterface {
    private final Context mContext;
    private View mContentView;

    public Dialog(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContentView(View view) {
        mContentView = view;
    }

    public View getContentView() {
        return mContentView;
    }

    public void show() {
    }

    public void dismiss() {
    }
}
