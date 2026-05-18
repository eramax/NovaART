package android.view;

public class WindowRelayoutResult implements android.os.Parcelable {
    public WindowRelayoutResult() {}

    protected WindowRelayoutResult(android.os.Parcel in) {
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

    public static final Creator<WindowRelayoutResult> CREATOR = new Creator<>() {
        @Override
        public WindowRelayoutResult createFromParcel(android.os.Parcel source) {
            return new WindowRelayoutResult(source);
        }

        @Override
        public WindowRelayoutResult[] newArray(int size) {
            return new WindowRelayoutResult[size];
        }
    };
}
