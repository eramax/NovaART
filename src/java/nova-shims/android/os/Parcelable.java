package android.os;

public interface Parcelable {
    int describeContents();
    void writeToParcel(android.os.Parcel dest, int flags);

    interface Creator<T> {
        T createFromParcel(android.os.Parcel source);
        T[] newArray(int size);
    }

    interface ClassLoaderCreator<T> extends Creator<T> {
        T createFromParcel(android.os.Parcel source, ClassLoader loader);
    }
}
