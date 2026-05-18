package android.view;

public class SurfaceControl implements android.os.Parcelable {
    public SurfaceControl() {}

    protected SurfaceControl(android.os.Parcel in) {
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

    public static final Creator<SurfaceControl> CREATOR = new Creator<>() {
        @Override
        public SurfaceControl createFromParcel(android.os.Parcel source) {
            return new SurfaceControl(source);
        }

        @Override
        public SurfaceControl[] newArray(int size) {
            return new SurfaceControl[size];
        }
    };

    public static class Transaction implements android.os.Parcelable {
        public Transaction() {}

        protected Transaction(android.os.Parcel in) {
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

        public static final Creator<Transaction> CREATOR = new Creator<>() {
            @Override
            public Transaction createFromParcel(android.os.Parcel source) {
                return new Transaction(source);
            }

            @Override
            public Transaction[] newArray(int size) {
                return new Transaction[size];
            }
        };
    }
}
