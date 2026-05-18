package android.view;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public interface WindowManager {
    Display getDefaultDisplay();

    class LayoutParams implements Parcelable {
        public static final int FLAG_FULLSCREEN           = 0x00000400;
        public static final int FLAG_KEEP_SCREEN_ON       = 0x00000080;
        public static final int FLAG_LAYOUT_NO_LIMITS     = 0x00000200;
        public static final int FLAG_NOT_FOCUSABLE        = 0x00000008;
        public static final int TYPE_APPLICATION          = 2;
        public static final int TYPE_APPLICATION_OVERLAY  = 2038;
        public static final int MATCH_PARENT              = -1;
        public static final int WRAP_CONTENT              = -2;
        public static final int SOFT_INPUT_STATE_HIDDEN   = 1;

        public int type  = TYPE_APPLICATION;
        public int flags = 0;
        public int width  = MATCH_PARENT;
        public int height = MATCH_PARENT;
        public int format = 0;
        public int softInputMode = 0;
        public String packageName;
        public CharSequence title;
        public float x, y;
        public float alpha = 1.0f;
        public int gravity;

        public LayoutParams() {}
        public LayoutParams(int w, int h) { width = w; height = h; }
        public LayoutParams(int w, int h, int type, int flags, int format) {
            width = w; height = h; this.type = type; this.flags = flags; this.format = format;
        }

        @Override public int describeContents() { return 0; }
        @Override public void writeToParcel(Parcel dest, int f) {}

        public static final Parcelable.Creator<LayoutParams> CREATOR =
                new Parcelable.Creator<LayoutParams>() {
            @Override public LayoutParams createFromParcel(Parcel in) { return new LayoutParams(); }
            @Override public LayoutParams[] newArray(int size) { return new LayoutParams[size]; }
        };
    }
}
