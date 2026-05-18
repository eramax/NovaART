package android.content.res;

public class Configuration {
    public static final int ORIENTATION_UNDEFINED = 0;
    public static final int ORIENTATION_PORTRAIT = 1;
    public static final int ORIENTATION_LANDSCAPE = 2;
    public static final int ORIENTATION_SQUARE = 3;

    public float fontScale = 1.0f;
    public int mcc;
    public int mnc;
    public String locale;
    public int orientation = ORIENTATION_PORTRAIT;
    public int screenWidthDp;
    public int screenHeightDp;
    public int smallestScreenWidthDp;
    public int densityDpi;
    public int keyboardHidden;
    public int hardKeyboardHidden;
    public int navigationHidden;
    public int uiMode;

    public Configuration() {}
    public Configuration(Configuration o) { setTo(o); }
    public void setTo(Configuration o) { if (o != null) { orientation = o.orientation; densityDpi = o.densityDpi; } }
    public int compareTo(Configuration that) { return 0; }
    public boolean equals(Configuration that) { return true; }
}
