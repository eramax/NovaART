package android.view;

import android.graphics.PixelFormat;
import android.os.Parcel;
import android.os.Parcelable;

public interface WindowManager extends ViewManager {

    Display getDefaultDisplay();

    void removeViewImmediate(View view);

    default android.view.WindowMetrics getCurrentWindowMetrics() { return null; }
    default android.view.WindowMetrics getMaximumWindowMetrics() { return null; }

    class LayoutParams extends android.view.ViewGroup.LayoutParams implements Parcelable {

        public static final int TYPE_BASE_APPLICATION    = 1;
        public static final int TYPE_APPLICATION         = 2;
        public static final int TYPE_APPLICATION_STARTING = 3;
        public static final int TYPE_DRAWN_APPLICATION   = 4;
        public static final int FIRST_APPLICATION_WINDOW = 1;
        public static final int LAST_APPLICATION_WINDOW  = 99;
        public static final int FIRST_SUB_WINDOW         = 1000;
        public static final int TYPE_APPLICATION_PANEL   = FIRST_SUB_WINDOW;
        public static final int TYPE_APPLICATION_MEDIA   = FIRST_SUB_WINDOW + 1;
        public static final int TYPE_APPLICATION_ATTACHED_DIALOG = FIRST_SUB_WINDOW + 3;
        public static final int LAST_SUB_WINDOW          = 1999;
        public static final int FIRST_SYSTEM_WINDOW      = 2000;
        public static final int TYPE_STATUS_BAR          = FIRST_SYSTEM_WINDOW;
        public static final int TYPE_SEARCH_BAR          = FIRST_SYSTEM_WINDOW + 1;
        public static final int TYPE_PHONE               = FIRST_SYSTEM_WINDOW + 2;
        public static final int TYPE_SYSTEM_ALERT        = FIRST_SYSTEM_WINDOW + 3;
        public static final int TYPE_TOAST               = FIRST_SYSTEM_WINDOW + 5;
        public static final int TYPE_SYSTEM_OVERLAY      = FIRST_SYSTEM_WINDOW + 6;
        public static final int TYPE_APPLICATION_OVERLAY = FIRST_SYSTEM_WINDOW + 38;
        public static final int LAST_SYSTEM_WINDOW       = 2999;

        public static final int MEMORY_TYPE_NORMAL  = 0;
        public static final int MEMORY_TYPE_HARDWARE = 1;
        public static final int MEMORY_TYPE_GPU      = 2;
        public static final int MEMORY_TYPE_PUSH_BUFFERS = 3;

        public static final int FLAG_ALLOW_LOCK_WHILE_SCREEN_ON = 0x00000001;
        public static final int FLAG_DIM_BEHIND                 = 0x00000002;
        public static final int FLAG_BLUR_BEHIND                = 0x00000004;
        public static final int FLAG_NOT_FOCUSABLE              = 0x00000008;
        public static final int FLAG_NOT_TOUCHABLE              = 0x00000010;
        public static final int FLAG_NOT_TOUCH_MODAL            = 0x00000020;
        public static final int FLAG_TOUCHABLE_WHEN_WAKING      = 0x00000040;
        public static final int FLAG_KEEP_SCREEN_ON             = 0x00000080;
        public static final int FLAG_LAYOUT_IN_SCREEN           = 0x00000100;
        public static final int FLAG_LAYOUT_NO_LIMITS           = 0x00000200;
        public static final int FLAG_FULLSCREEN                 = 0x00000400;
        public static final int FLAG_FORCE_NOT_FULLSCREEN       = 0x00000800;
        public static final int FLAG_DITHER                     = 0x00001000;
        public static final int FLAG_SECURE                     = 0x00002000;
        public static final int FLAG_SCALED                     = 0x00004000;
        public static final int FLAG_IGNORE_CHEEK_PRESSES       = 0x00008000;
        public static final int FLAG_LAYOUT_INSET_DECOR         = 0x00010000;
        public static final int FLAG_ALT_FOCUSABLE_IM           = 0x00020000;
        public static final int FLAG_WATCH_OUTSIDE_TOUCH        = 0x00040000;
        public static final int FLAG_SHOW_WHEN_LOCKED           = 0x00080000;
        public static final int FLAG_SHOW_WALLPAPER             = 0x00100000;
        public static final int FLAG_TURN_SCREEN_ON             = 0x00200000;
        public static final int FLAG_DISMISS_KEYGUARD           = 0x00400000;
        public static final int FLAG_SPLIT_TOUCH                = 0x00800000;
        public static final int FLAG_HARDWARE_ACCELERATED       = 0x01000000;
        public static final int FLAG_LAYOUT_IN_OVERSCAN         = 0x02000000;
        public static final int FLAG_TRANSLUCENT_STATUS         = 0x04000000;
        public static final int FLAG_TRANSLUCENT_NAVIGATION     = 0x08000000;
        public static final int FLAG_LOCAL_FOCUS_MODE           = 0x10000000;
        public static final int FLAG_SLIPPERY                   = 0x20000000;
        public static final int FLAG_LAYOUT_ATTACHED_IN_DECOR   = 0x40000000;
        public static final int FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS = 0x80000000;

        public static final int SOFT_INPUT_STATE_UNCHANGED   = 0;
        public static final int SOFT_INPUT_STATE_HIDDEN      = 2;
        public static final int SOFT_INPUT_STATE_ALWAYS_HIDDEN = 3;
        public static final int SOFT_INPUT_STATE_VISIBLE     = 4;
        public static final int SOFT_INPUT_STATE_ALWAYS_VISIBLE = 5;
        public static final int SOFT_INPUT_ADJUST_UNSPECIFIED = 0x00;
        public static final int SOFT_INPUT_ADJUST_RESIZE     = 0x10;
        public static final int SOFT_INPUT_ADJUST_PAN        = 0x20;
        public static final int SOFT_INPUT_ADJUST_NOTHING    = 0x30;
        public static final int SOFT_INPUT_IS_FORWARD_NAVIGATION = 0x100;

        public static final int GRAVITY_DEFAULT = android.view.Gravity.DEFAULT_GRAVITY;

        public int type = TYPE_APPLICATION;
        public int flags;
        public int format = PixelFormat.OPAQUE;
        public float alpha = 1.0f;
        public float dimAmount = 1.0f;
        public int softInputMode;
        public int gravity;
        public float horizontalMargin;
        public float verticalMargin;
        public int x;
        public int y;
        public int windowAnimations;
        public String packageName;
        public int screenOrientation = -1;
        public int systemUiVisibility;
        public int subtreeSystemUiVisibility;
        public int layoutInDisplayCutoutMode;
        public int colorMode;
        public long hideTimeoutMilliseconds = -1;
        public int inputFeatures;
        public int surfaceInsets;
        public boolean hasSystemUiListeners;
        public int privateFlags;
        public int preferredRefreshRate;
        public int preferredDisplayModeId;
        public float preferredMinDisplayRefreshRate;
        public float preferredMaxDisplayRefreshRate;
        public int fitInsetsTypes;
        public int fitInsetsSides;
        public boolean fitInsetsIgnoringVisibility;
        public String accessibilityTitle;
        public int insetsFlags;
        public boolean insetsRoundedCornerFrame;
        public int rotationAnimation;
        public int samsungFlags;
        public android.graphics.Rect surfaceFrame;
        public int blurBehindRadius;
        public int forciblyShownTypes;
        public String title;
        public android.os.IBinder token;

        public LayoutParams() {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            type = TYPE_APPLICATION;
            format = PixelFormat.OPAQUE;
        }
        public LayoutParams(int type) {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            this.type = type;
            format = PixelFormat.OPAQUE;
        }
        public LayoutParams(int type, int flags) {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            this.type = type; this.flags = flags;
            format = PixelFormat.OPAQUE;
        }
        public LayoutParams(int type, int flags, int format) {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            this.type = type; this.flags = flags; this.format = format;
        }
        public LayoutParams(int w, int h, int type, int flags, int format) {
            super(w, h);
            this.type = type; this.flags = flags; this.format = format;
        }

        public void setFitInsetsTypes(int types) { fitInsetsTypes = types; }
        public int getFitInsetsTypes() { return fitInsetsTypes; }
        public void setFitInsetsSides(int sides) { fitInsetsSides = sides; }
        public int getFitInsetsSides() { return fitInsetsSides; }
        public void setFitInsetsIgnoringVisibility(boolean ignore) { fitInsetsIgnoringVisibility = ignore; }
        public boolean isFitInsetsIgnoringVisibility() { return fitInsetsIgnoringVisibility; }
        public void setTitle(CharSequence title) { this.title = title != null ? title.toString() : null; }
        public CharSequence getTitle() { return title; }
        public void setColorMode(int colorMode) { this.colorMode = colorMode; }
        public int getColorMode() { return colorMode; }
        public boolean isFullscreen() { return (flags & FLAG_FULLSCREEN) != 0; }

        @Override public int describeContents() { return 0; }
        @Override public void writeToParcel(Parcel dest, int flags) { dest.writeInt(type); dest.writeInt(this.flags); }
        public static final Parcelable.Creator<LayoutParams> CREATOR = new Parcelable.Creator<LayoutParams>() {
            @Override public LayoutParams createFromParcel(Parcel in) {
                LayoutParams lp = new LayoutParams();
                lp.type = in.readInt(); lp.flags = in.readInt(); return lp;
            }
            @Override public LayoutParams[] newArray(int size) { return new LayoutParams[size]; }
        };
    }
}
