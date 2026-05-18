package android.content.pm;

import android.content.pm.ApplicationInfo;

public class ActivityInfo implements android.os.Parcelable {
    public ApplicationInfo applicationInfo;
    public String name;
    public String packageName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
    }

    public static final android.os.Parcelable.Creator<ActivityInfo> CREATOR =
            new android.os.Parcelable.Creator<>() {
                @Override
                public ActivityInfo createFromParcel(android.os.Parcel source) {
                    return new ActivityInfo();
                }

                @Override
                public ActivityInfo[] newArray(int size) {
                    return new ActivityInfo[size];
                }
            };
}
