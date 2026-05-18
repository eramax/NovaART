package android.view;

public class Surface implements android.os.Parcelable {
    private boolean valid = true;

    public Surface() {
    }

    public void release() {
        valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(android.os.Parcel in) {
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
    }

    public static final Creator<Surface> CREATOR = new Creator<>() {
        @Override
        public Surface createFromParcel(android.os.Parcel source) {
            return new Surface();
        }

        @Override
        public Surface[] newArray(int size) {
            return new Surface[size];
        }
    };
}
