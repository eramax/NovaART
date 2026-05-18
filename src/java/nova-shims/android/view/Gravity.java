package android.view;

public class Gravity {
    public static final int NO_GRAVITY       = 0x0000;
    public static final int AXIS_SPECIFIED   = 0x0001;
    public static final int AXIS_PULL_BEFORE = 0x0002;
    public static final int AXIS_PULL_AFTER  = 0x0004;
    public static final int AXIS_CLIP        = 0x0008;
    public static final int AXIS_X_SHIFT     = 0;
    public static final int AXIS_Y_SHIFT     = 4;
    public static final int TOP        = (AXIS_PULL_BEFORE | AXIS_SPECIFIED) << AXIS_Y_SHIFT;
    public static final int BOTTOM     = (AXIS_PULL_AFTER  | AXIS_SPECIFIED) << AXIS_Y_SHIFT;
    public static final int LEFT       = (AXIS_PULL_BEFORE | AXIS_SPECIFIED) << AXIS_X_SHIFT;
    public static final int RIGHT      = (AXIS_PULL_AFTER  | AXIS_SPECIFIED) << AXIS_X_SHIFT;
    public static final int CENTER_VERTICAL   = AXIS_SPECIFIED << AXIS_Y_SHIFT;
    public static final int FILL_VERTICAL     = TOP | BOTTOM;
    public static final int CENTER_HORIZONTAL = AXIS_SPECIFIED << AXIS_X_SHIFT;
    public static final int FILL_HORIZONTAL   = LEFT | RIGHT;
    public static final int CENTER     = CENTER_VERTICAL | CENTER_HORIZONTAL;
    public static final int FILL       = FILL_VERTICAL | FILL_HORIZONTAL;
    public static final int CLIP_VERTICAL   = AXIS_CLIP << AXIS_Y_SHIFT;
    public static final int CLIP_HORIZONTAL = AXIS_CLIP << AXIS_X_SHIFT;
    public static final int RELATIVE_LAYOUT_DIRECTION = 0x00800000;
    public static final int HORIZONTAL_GRAVITY_MASK = (AXIS_SPECIFIED | AXIS_PULL_BEFORE | AXIS_PULL_AFTER) << AXIS_X_SHIFT;
    public static final int VERTICAL_GRAVITY_MASK   = (AXIS_SPECIFIED | AXIS_PULL_BEFORE | AXIS_PULL_AFTER) << AXIS_Y_SHIFT;
    public static final int RELATIVE_HORIZONTAL_GRAVITY_MASK = HORIZONTAL_GRAVITY_MASK | RELATIVE_LAYOUT_DIRECTION;
    public static final int START      = RELATIVE_LAYOUT_DIRECTION | LEFT;
    public static final int END        = RELATIVE_LAYOUT_DIRECTION | RIGHT;
    public static final int DISPLAY_CLIP_VERTICAL   = 0x10000000;
    public static final int DISPLAY_CLIP_HORIZONTAL = 0x01000000;
    public static final int DEFAULT_GRAVITY = NO_GRAVITY;

    private Gravity() {}
}
