package android.content;

import android.os.Parcel;
import android.os.Parcelable;

public final class ComponentName implements Parcelable, Cloneable, Comparable<ComponentName> {

    private final String mPackage;
    private final String mClass;

    public ComponentName(String pkg, String cls) {
        mPackage = pkg != null ? pkg : "";
        mClass   = cls  != null ? cls  : "";
    }

    public ComponentName(Context pkg, String cls) {
        mPackage = pkg.getPackageName();
        mClass   = cls != null ? cls : "";
    }

    public ComponentName(Context pkg, Class<?> cls) {
        mPackage = pkg.getPackageName();
        mClass   = cls.getName();
    }

    public String getPackageName()   { return mPackage; }
    public String getClassName()     { return mClass; }
    public String getShortClassName() {
        if (mClass.startsWith(mPackage)) {
            return mClass.substring(mPackage.length());
        }
        return mClass;
    }
    public String flattenToString()  { return mPackage + "/" + mClass; }
    public String flattenToShortString() { return mPackage + "/" + getShortClassName(); }

    public static ComponentName unflattenFromString(String str) {
        if (str == null) return null;
        int sep = str.indexOf('/');
        if (sep < 0) return null;
        return new ComponentName(str.substring(0, sep), str.substring(sep + 1));
    }

    public static ComponentName createRelative(String pkg, String cls) {
        if (cls.startsWith(".")) cls = pkg + cls;
        return new ComponentName(pkg, cls);
    }

    public static ComponentName createRelative(Context pkg, String cls) {
        return createRelative(pkg.getPackageName(), cls);
    }

    @Override public ComponentName clone() { return new ComponentName(mPackage, mClass); }
    @Override public int hashCode() { return mPackage.hashCode() * 31 + mClass.hashCode(); }
    @Override public boolean equals(Object o) {
        if (!(o instanceof ComponentName)) return false;
        ComponentName c = (ComponentName) o;
        return mPackage.equals(c.mPackage) && mClass.equals(c.mClass);
    }
    @Override public String toString() { return "ComponentInfo{" + mPackage + "/" + mClass + "}"; }
    @Override public int compareTo(ComponentName other) {
        int r = mPackage.compareTo(other.mPackage);
        return r != 0 ? r : mClass.compareTo(other.mClass);
    }

    @Override public int describeContents() { return 0; }
    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPackage);
        dest.writeString(mClass);
    }

    public static final Parcelable.Creator<ComponentName> CREATOR = new Parcelable.Creator<ComponentName>() {
        @Override public ComponentName createFromParcel(Parcel in) {
            return new ComponentName(in.readString(), in.readString());
        }
        @Override public ComponentName[] newArray(int size) { return new ComponentName[size]; }
    };

    public void writeToProto(Object proto, long fieldId) {}
}
