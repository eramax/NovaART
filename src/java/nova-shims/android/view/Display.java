package android.view;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;

public final class Display {

    public static final int DEFAULT_DISPLAY = 0;
    public static final int INVALID_DISPLAY = -1;

    public static final int STATE_UNKNOWN  = 0;
    public static final int STATE_OFF      = 1;
    public static final int STATE_ON       = 2;
    public static final int STATE_DOZE     = 3;
    public static final int STATE_DOZE_SUSPEND = 4;
    public static final int STATE_VR       = 5;
    public static final int STATE_ON_SUSPEND = 6;

    public static final int FLAG_SUPPORTS_PROTECTED_BUFFERS = 0x00000001;
    public static final int FLAG_SECURE                     = 0x00000002;
    public static final int FLAG_PRIVATE                    = 0x00000004;
    public static final int FLAG_PRESENTATION               = 0x00000008;
    public static final int FLAG_ROUND                      = 0x00000010;
    public static final int FLAG_CAN_SHOW_WITH_INSECURE_KEYGUARD = 0x00000020;
    public static final int FLAG_SHOULD_SHOW_SYSTEM_DECORATIONS = 0x00000040;
    public static final int FLAG_TRUSTED                    = 0x00000400;
    public static final int FLAG_OWN_DISPLAY_ADAPTER        = 0x00001000;
    public static final int FLAG_DEVICE_DISPLAY             = 0x00002000;
    public static final int FLAG_STEAL_TOP_FOCUS_DISABLED   = 0x00004000;

    public static final int TYPE_UNKNOWN         = 0;
    public static final int TYPE_INTERNAL        = 1;
    public static final int TYPE_EXTERNAL        = 2;
    public static final int TYPE_WIFI            = 3;
    public static final int TYPE_OVERLAY         = 4;
    public static final int TYPE_VIRTUAL         = 5;

    public static final int REMOVE_MODE_DESTROY_CONTENT = 0;
    public static final int REMOVE_MODE_MOVE_CONTENT_TO_PRIMARY = 1;

    private final int mDisplayId;
    private int mWidth  = 1080;
    private int mHeight = 1920;
    private float mRefreshRate = 60f;

    public Display(int displayId) { mDisplayId = displayId; }
    public Display() { mDisplayId = DEFAULT_DISPLAY; }

    public int getDisplayId() { return mDisplayId; }
    public String getName() { return "NovaDisplay#" + mDisplayId; }
    public int getFlags() { return 0; }
    public int getType() { return TYPE_INTERNAL; }
    public String getAddress() { return null; }

    public void getSize(Point outSize) { outSize.x = mWidth; outSize.y = mHeight; }
    public void getRealSize(Point outSize) { outSize.x = mWidth; outSize.y = mHeight; }
    public void getRectSize(Rect outSize) { outSize.set(0, 0, mWidth, mHeight); }
    public void getRealMetrics(DisplayMetrics outMetrics) { outMetrics.widthPixels = mWidth; outMetrics.heightPixels = mHeight; }
    public void getMetrics(DisplayMetrics outMetrics) { outMetrics.widthPixels = mWidth; outMetrics.heightPixels = mHeight; }

    public int getWidth()  { return mWidth; }
    public int getHeight() { return mHeight; }
    public float getRefreshRate() { return mRefreshRate; }
    public float[] getSupportedRefreshRates() { return new float[]{ mRefreshRate }; }
    public android.view.Display.Mode[] getSupportedModes() { return new android.view.Display.Mode[0]; }
    public android.view.Display.Mode getMode() { return null; }
    public int getState() { return STATE_ON; }
    public long getPresentationDeadlineNanos() { return 0L; }
    public int getRotation() { return android.view.Surface.ROTATION_0; }
    public int getOrientation() { return getRotation(); }
    public boolean isValid() { return true; }
    public boolean isMinimalPostProcessingSupported() { return false; }

    public android.hardware.display.DeviceProductInfo getDeviceProductInfo() { return null; }
    public android.graphics.ColorSpace[] getSupportedWideColorGamut() { return new android.graphics.ColorSpace[0]; }
    public android.graphics.ColorSpace getPreferredWideGamutColorSpace() { return null; }
    public boolean isHdr() { return false; }
    public boolean isWideColorGamut() { return false; }
    public int getColorMode() { return 0; }
    public float getBrightnessInfo() { return 1.0f; }
    public android.view.DisplayCutout getCutout() { return null; }

    @Override public String toString() { return "Display{id=" + mDisplayId + ", " + mWidth + "x" + mHeight + "}"; }

    public static final class Mode {
        public static final int INVALID_MODE_ID = -1;
        private final int mModeId;
        private final int mWidth;
        private final int mHeight;
        private final float mRefreshRate;

        public Mode(int modeId, int width, int height, float refreshRate) {
            mModeId = modeId; mWidth = width; mHeight = height; mRefreshRate = refreshRate;
        }
        public int getModeId() { return mModeId; }
        public int getPhysicalWidth()  { return mWidth; }
        public int getPhysicalHeight() { return mHeight; }
        public float getRefreshRate()  { return mRefreshRate; }
        public boolean isValid() { return mModeId != INVALID_MODE_ID; }
        public float[] getAlternativeRefreshRates() { return new float[0]; }
        @Override public String toString() { return "Mode{id=" + mModeId + ", " + mWidth + "x" + mHeight + "@" + mRefreshRate + "}"; }
    }

    public static final class HdrCapabilities implements android.os.Parcelable {
        public static final int HDR_TYPE_INVALID = -1;
        public static final int HDR_TYPE_DOLBY_VISION = 1;
        public static final int HDR_TYPE_HDR10 = 2;
        public static final int HDR_TYPE_HLG  = 3;
        public static final int HDR_TYPE_HDR10_PLUS = 4;
        public static final float INVALID_LUMINANCE = -1f;
        private final int[] mSupportedHdrTypes;
        public HdrCapabilities(int[] types, float max, float maxAvg, float min) { mSupportedHdrTypes = types; }
        public int[] getSupportedHdrTypes() { return mSupportedHdrTypes; }
        public float getDesiredMaxLuminance() { return INVALID_LUMINANCE; }
        public float getDesiredMaxAverageLuminance() { return INVALID_LUMINANCE; }
        public float getDesiredMinLuminance() { return INVALID_LUMINANCE; }
        @Override public int describeContents() { return 0; }
        @Override public void writeToParcel(android.os.Parcel dest, int flags) {}
        public static final android.os.Parcelable.Creator<HdrCapabilities> CREATOR = new android.os.Parcelable.Creator<HdrCapabilities>() {
            @Override public HdrCapabilities createFromParcel(android.os.Parcel in) { return new HdrCapabilities(new int[0], 0, 0, 0); }
            @Override public HdrCapabilities[] newArray(int size) { return new HdrCapabilities[size]; }
        };
    }
}
