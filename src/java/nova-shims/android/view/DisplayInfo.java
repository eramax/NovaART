package android.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;

public final class DisplayInfo implements Parcelable {

    public int displayId = Display.DEFAULT_DISPLAY;
    public String name = "NovaDisplay";
    public String uniqueId;
    public int appWidth  = 1080;
    public int appHeight = 1920;
    public int logicalWidth  = 1080;
    public int logicalHeight = 1920;
    public int physicalXDpi = 320;
    public int physicalYDpi = 320;
    public int logicalDensityDpi = DisplayMetrics.DENSITY_XHIGH;
    public float refreshRate = 60f;
    public long presentationDeadlineNanos;
    public int flags;
    public int type = Display.TYPE_INTERNAL;
    public int state = Display.STATE_ON;
    public int rotation = Surface.ROTATION_0;
    public int colorMode;
    public Display.HdrCapabilities hdrCapabilities;
    public int layerStack;
    public android.graphics.Rect appVsyncOffsetNanos;
    public long appVsyncOffset;
    public int overscanLeft, overscanTop, overscanRight, overscanBottom;

    public DisplayInfo() {}

    public boolean hasAccess(int uid) { return true; }
    public boolean isHdr() { return false; }
    public boolean isWideColorGamut() { return false; }
    public int getNaturalWidth()  { return logicalWidth; }
    public int getNaturalHeight() { return logicalHeight; }

    public void copyFrom(DisplayInfo other) {
        displayId = other.displayId; name = other.name;
        appWidth = other.appWidth; appHeight = other.appHeight;
        logicalWidth = other.logicalWidth; logicalHeight = other.logicalHeight;
        refreshRate = other.refreshRate; flags = other.flags;
    }

    public void getAppMetrics(DisplayMetrics outMetrics) {
        outMetrics.widthPixels = appWidth; outMetrics.heightPixels = appHeight;
        outMetrics.densityDpi = logicalDensityDpi;
        outMetrics.density = logicalDensityDpi / (float) DisplayMetrics.DENSITY_DEFAULT;
        outMetrics.scaledDensity = outMetrics.density;
        outMetrics.xdpi = physicalXDpi; outMetrics.ydpi = physicalYDpi;
    }

    public void getLogicalMetrics(DisplayMetrics outMetrics, Object ci, Object config) {
        getAppMetrics(outMetrics);
    }

    public int getCompatibleWidthForOrientation(int orientation) { return logicalWidth; }

    @Override public int describeContents() { return 0; }
    @Override public void writeToParcel(Parcel dest, int flags) { dest.writeInt(displayId); dest.writeInt(logicalWidth); dest.writeInt(logicalHeight); }
    public static final Parcelable.Creator<DisplayInfo> CREATOR = new Parcelable.Creator<DisplayInfo>() {
        @Override public DisplayInfo createFromParcel(Parcel in) {
            DisplayInfo di = new DisplayInfo();
            di.displayId = in.readInt(); di.logicalWidth = in.readInt(); di.logicalHeight = in.readInt();
            di.appWidth = di.logicalWidth; di.appHeight = di.logicalHeight;
            return di;
        }
        @Override public DisplayInfo[] newArray(int size) { return new DisplayInfo[size]; }
    };
}
