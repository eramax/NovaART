package android.content.pm;

public class ApplicationInfo implements android.os.Parcelable {
    public String className;
    public String packageName;
    public String sourceDir;
    public int targetSdkVersion = android.os.Build.VERSION.SDK_INT;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
    }

    public static final android.os.Parcelable.Creator<ApplicationInfo> CREATOR =
            new android.os.Parcelable.Creator<>() {
                @Override
                public ApplicationInfo createFromParcel(android.os.Parcel source) {
                    return new ApplicationInfo();
                }

                @Override
                public ApplicationInfo[] newArray(int size) {
                    return new ApplicationInfo[size];
                }
            };
}
