package android.util;

public class DisplayMetrics {

    public static final int DENSITY_LOW     = 120;
    public static final int DENSITY_MEDIUM  = 160;
    public static final int DENSITY_TV      = 213;
    public static final int DENSITY_HIGH    = 240;
    public static final int DENSITY_260     = 260;
    public static final int DENSITY_280     = 280;
    public static final int DENSITY_300     = 300;
    public static final int DENSITY_XHIGH   = 320;
    public static final int DENSITY_340     = 340;
    public static final int DENSITY_360     = 360;
    public static final int DENSITY_400     = 400;
    public static final int DENSITY_420     = 420;
    public static final int DENSITY_440     = 440;
    public static final int DENSITY_450     = 450;
    public static final int DENSITY_XXHIGH  = 480;
    public static final int DENSITY_560     = 560;
    public static final int DENSITY_600     = 600;
    public static final int DENSITY_XXXHIGH = 640;
    public static final int DENSITY_DEFAULT = DENSITY_MEDIUM;
    public static final float DENSITY_DEFAULT_SCALE = 1.0f / DENSITY_DEFAULT;
    public static final int DENSITY_DEVICE_STABLE = DENSITY_XXHIGH;

    public float density = 2.0f;
    public int densityDpi = DENSITY_XHIGH;
    public float scaledDensity = 2.0f;
    public float xdpi = 320f;
    public float ydpi = 320f;
    public int widthPixels  = 1080;
    public int heightPixels = 1920;
    public float noncompatWidthPixels  = 1080;
    public float noncompatHeightPixels = 1920;
    public float noncompatDensity  = 2.0f;
    public int noncompatDensityDpi = DENSITY_XHIGH;
    public float noncompatScaledDensity = 2.0f;
    public float noncompatXdpi = 320f;
    public float noncompatYdpi = 320f;

    public DisplayMetrics() {}

    public void setTo(DisplayMetrics o) {
        density = o.density; densityDpi = o.densityDpi; scaledDensity = o.scaledDensity;
        xdpi = o.xdpi; ydpi = o.ydpi; widthPixels = o.widthPixels; heightPixels = o.heightPixels;
    }
    public void setToDefaults() {
        density = 1.0f; densityDpi = DENSITY_DEFAULT; scaledDensity = 1.0f;
        xdpi = DENSITY_DEFAULT; ydpi = DENSITY_DEFAULT;
        widthPixels = 0; heightPixels = 0;
    }

    @Override public String toString() {
        return "DisplayMetrics{density=" + density + ", width=" + widthPixels + ", height=" + heightPixels + "}";
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof DisplayMetrics)) return false;
        DisplayMetrics d = (DisplayMetrics) o;
        return densityDpi == d.densityDpi && widthPixels == d.widthPixels && heightPixels == d.heightPixels;
    }
    @Override public int hashCode() { return densityDpi * 31 + widthPixels; }
}
