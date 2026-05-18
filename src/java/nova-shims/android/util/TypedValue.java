package android.util;

public class TypedValue {
    public static final int TYPE_NULL       = 0x00;
    public static final int TYPE_REFERENCE  = 0x01;
    public static final int TYPE_STRING     = 0x03;
    public static final int TYPE_FLOAT      = 0x04;
    public static final int TYPE_INT_DEC    = 0x10;
    public static final int TYPE_INT_HEX    = 0x11;
    public static final int TYPE_INT_BOOLEAN = 0x12;
    public static final int TYPE_INT_COLOR_ARGB8 = 0x1c;
    public static final int TYPE_INT_COLOR_RGB8  = 0x1d;
    public static final int TYPE_DIMENSION  = 0x05;

    public static final int COMPLEX_UNIT_DP  = 1;
    public static final int COMPLEX_UNIT_SP  = 2;
    public static final int COMPLEX_UNIT_PX  = 0;

    public int type;
    public int data;
    public float fraction;
    public CharSequence string;
    public int resourceId;

    public static float applyDimension(int unit, float value, DisplayMetrics metrics) {
        switch (unit) {
            case COMPLEX_UNIT_PX: return value;
            case COMPLEX_UNIT_DP: return value * metrics.density;
            case COMPLEX_UNIT_SP: return value * metrics.scaledDensity;
            default: return value;
        }
    }

    public float getDimension(DisplayMetrics metrics) { return fraction; }
}
