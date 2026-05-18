package android.content.pm;

public class PackageInfo implements android.os.Parcelable {
    public ApplicationInfo applicationInfo;
    public String packageName;
    public int versionCode;
    public String versionName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
    }

    public static final android.os.Parcelable.Creator<PackageInfo> CREATOR =
            new android.os.Parcelable.Creator<>() {
                @Override
                public PackageInfo createFromParcel(android.os.Parcel source) {
                    return new PackageInfo();
                }

                @Override
                public PackageInfo[] newArray(int size) {
                    return new PackageInfo[size];
                }
            };
}
