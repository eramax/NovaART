package android.content;

public class ComponentName implements android.os.Parcelable {
    private final String mPackage;
    private final String mClassName;

    public ComponentName(String pkg, String cls) {
        mPackage = pkg;
        mClassName = cls;
    }

    public String getClassName() {
        return mClassName;
    }

    public String getPackageName() {
        return mPackage;
    }

    @Override
    public String toString() {
        return mPackage + "/" + mClassName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
    }

    public static final android.os.Parcelable.Creator<ComponentName> CREATOR =
            new android.os.Parcelable.Creator<>() {
                @Override
                public ComponentName createFromParcel(android.os.Parcel source) {
                    return new ComponentName("", "");
                }

                @Override
                public ComponentName[] newArray(int size) {
                    return new ComponentName[size];
                }
            };
}
