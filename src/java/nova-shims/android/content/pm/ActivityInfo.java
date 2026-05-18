package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

public class ActivityInfo extends ComponentInfo implements Parcelable {

    public static final int SCREEN_ORIENTATION_UNSPECIFIED   = -1;
    public static final int SCREEN_ORIENTATION_LANDSCAPE     =  0;
    public static final int SCREEN_ORIENTATION_PORTRAIT      =  1;
    public static final int SCREEN_ORIENTATION_USER          =  2;
    public static final int SCREEN_ORIENTATION_BEHIND        =  3;
    public static final int SCREEN_ORIENTATION_SENSOR        =  4;
    public static final int SCREEN_ORIENTATION_NOSENSOR      =  5;
    public static final int SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6;
    public static final int SCREEN_ORIENTATION_SENSOR_PORTRAIT  = 7;
    public static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
    public static final int SCREEN_ORIENTATION_REVERSE_PORTRAIT  = 9;
    public static final int SCREEN_ORIENTATION_FULL_SENSOR   = 10;
    public static final int SCREEN_ORIENTATION_USER_LANDSCAPE = 11;
    public static final int SCREEN_ORIENTATION_USER_PORTRAIT  = 12;
    public static final int SCREEN_ORIENTATION_FULL_USER     = 13;
    public static final int SCREEN_ORIENTATION_LOCKED        = 14;

    public static final int CONFIG_MCC              = 0x0001;
    public static final int CONFIG_MNC              = 0x0002;
    public static final int CONFIG_LOCALE           = 0x0004;
    public static final int CONFIG_TOUCHSCREEN      = 0x0008;
    public static final int CONFIG_KEYBOARD         = 0x0010;
    public static final int CONFIG_KEYBOARD_HIDDEN  = 0x0020;
    public static final int CONFIG_NAVIGATION       = 0x0040;
    public static final int CONFIG_ORIENTATION      = 0x0080;
    public static final int CONFIG_SCREEN_LAYOUT    = 0x0100;
    public static final int CONFIG_UI_MODE          = 0x0200;
    public static final int CONFIG_SCREEN_SIZE      = 0x0400;
    public static final int CONFIG_SMALLEST_SCREEN_SIZE = 0x0800;
    public static final int CONFIG_DENSITY          = 0x1000;
    public static final int CONFIG_LAYOUT_DIRECTION = 0x2000;
    public static final int CONFIG_FONT_SCALE       = 0x40000000;

    public static final int FLAG_ALLOW_TASK_REPARENTING = 0x0040;
    public static final int FLAG_ALWAYS_RETAIN_TASK_STATE = 0x0008;
    public static final int FLAG_CLEAR_TASK_ON_LAUNCH = 0x0004;
    public static final int FLAG_FINISH_ON_TASK_LAUNCH = 0x0002;
    public static final int FLAG_HARDWARE_ACCELERATED = 0x0200;
    public static final int FLAG_MULTIPROCESS       = 0x0001;
    public static final int FLAG_NO_HISTORY         = 0x0080;
    public static final int FLAG_SHOW_FOR_ALL_USERS = 0x4000;
    public static final int FLAG_STATE_NOT_NEEDED   = 0x0010;

    public static final int LAUNCH_MULTIPLE         = 0;
    public static final int LAUNCH_SINGLE_TOP       = 1;
    public static final int LAUNCH_SINGLE_TASK      = 2;
    public static final int LAUNCH_SINGLE_INSTANCE  = 3;

    public static final int DOCUMENT_LAUNCH_NONE    = 0;
    public static final int DOCUMENT_LAUNCH_INTO_EXISTING = 1;
    public static final int DOCUMENT_LAUNCH_ALWAYS  = 2;
    public static final int DOCUMENT_LAUNCH_NEVER   = 3;

    public static final int PERSIST_ROOT_ONLY       = 0;
    public static final int PERSIST_NEVER           = 1;
    public static final int PERSIST_ACROSS_REBOOTS  = 2;

    public int theme;
    public int launchMode;
    public int documentLaunchMode;
    public String permission;
    public String taskAffinity;
    public String targetActivity;
    public int flags;
    public int configChanges;
    public int screenOrientation = SCREEN_ORIENTATION_UNSPECIFIED;
    public int softInputMode;
    public int uiOptions;
    public int persistableMode;
    public int maxRecents;
    public int windowLayout;

    public ActivityInfo() {}
    public ActivityInfo(ActivityInfo orig) { this.name = orig.name; this.packageName = orig.packageName; }

    @Override public int describeContents() { return 0; }
    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(packageName);
    }

    public static final Parcelable.Creator<ActivityInfo> CREATOR = new Parcelable.Creator<ActivityInfo>() {
        @Override public ActivityInfo createFromParcel(Parcel in) {
            ActivityInfo ai = new ActivityInfo();
            ai.name = in.readString();
            ai.packageName = in.readString();
            return ai;
        }
        @Override public ActivityInfo[] newArray(int size) { return new ActivityInfo[size]; }
    };
}
