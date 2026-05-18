package android.view;

public class InputChannel implements android.os.Parcelable {
    public InputChannel() {}

    protected InputChannel(android.os.Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(android.os.Parcel in) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
    }

    public static final Creator<InputChannel> CREATOR = new Creator<>() {
        @Override
        public InputChannel createFromParcel(android.os.Parcel source) {
            return new InputChannel(source);
        }

        @Override
        public InputChannel[] newArray(int size) {
            return new InputChannel[size];
        }
    };
}
