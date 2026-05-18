package android.view;

public class DisplayInfo implements android.os.Parcelable {
    public int logicalWidth;
    public int logicalHeight;
    public float refreshRate;

    public DisplayInfo() {}

    protected DisplayInfo(android.os.Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(android.os.Parcel in) {
        logicalWidth = in.readInt();
        logicalHeight = in.readInt();
        refreshRate = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(logicalWidth);
        dest.writeInt(logicalHeight);
        dest.writeFloat(refreshRate);
    }

    public static final Creator<DisplayInfo> CREATOR = new Creator<>() {
        @Override
        public DisplayInfo createFromParcel(android.os.Parcel source) {
            return new DisplayInfo(source);
        }

        @Override
        public DisplayInfo[] newArray(int size) {
            return new DisplayInfo[size];
        }
    };
}
