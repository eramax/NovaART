package android.view;

import android.os.Parcel;
import android.os.Parcelable;

public final class WindowRelayoutResult implements Parcelable {

    public final SurfaceControl surfaceControl = new SurfaceControl();
    public int frames;
    public int syncSeqId;

    public WindowRelayoutResult() {}

    public void readFromParcel(android.os.Parcel in) {}

    @Override public int describeContents() { return 0; }
    @Override public void writeToParcel(Parcel dest, int flags) {}
    public static final Parcelable.Creator<WindowRelayoutResult> CREATOR = new Parcelable.Creator<WindowRelayoutResult>() {
        @Override public WindowRelayoutResult createFromParcel(Parcel in) { return new WindowRelayoutResult(); }
        @Override public WindowRelayoutResult[] newArray(int size) { return new WindowRelayoutResult[size]; }
    };
}
