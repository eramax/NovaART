package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class AlertDialog extends Dialog {
    private CharSequence mTitle;
    private CharSequence mMessage;

    public AlertDialog(Context context) {
        super(context);
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public void setMessage(CharSequence message) {
        mMessage = message;
    }

    public CharSequence getMessage() {
        return mMessage;
    }

    public static class Builder {
        private final Context mContext;
        private final AlertDialog mDialog;

        public Builder(Context context) {
            mContext = context;
            mDialog = new AlertDialog(context);
        }

        public Builder setTitle(CharSequence title) {
            mDialog.setTitle(title);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            mDialog.setMessage(message);
            return this;
        }

        public Builder setView(View view) {
            mDialog.setContentView(view);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            return this;
        }

        public Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
            return this;
        }

        public Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
            return this;
        }

        public Builder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
            return this;
        }

        public AlertDialog create() {
            return mDialog;
        }

        public AlertDialog show() {
            mDialog.show();
            return mDialog;
        }
    }
}
