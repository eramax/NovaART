package android.view;

public class KeyEvent {
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_UP = 1;
    public static final int ACTION_MULTIPLE = 2;

    public static final int KEYCODE_UNKNOWN = 0;
    public static final int KEYCODE_BACK = 4;
    public static final int KEYCODE_HOME = 3;
    public static final int KEYCODE_MENU = 82;
    public static final int KEYCODE_SEARCH = 84;
    public static final int KEYCODE_0 = 7;
    public static final int KEYCODE_1 = 8;
    public static final int KEYCODE_2 = 9;
    public static final int KEYCODE_3 = 10;
    public static final int KEYCODE_4 = 11;
    public static final int KEYCODE_5 = 12;
    public static final int KEYCODE_6 = 13;
    public static final int KEYCODE_7 = 14;
    public static final int KEYCODE_8 = 15;
    public static final int KEYCODE_9 = 16;
    public static final int KEYCODE_A = 29;
    public static final int KEYCODE_B = 30;
    public static final int KEYCODE_C = 31;
    public static final int KEYCODE_D = 32;
    public static final int KEYCODE_E = 33;
    public static final int KEYCODE_F = 34;
    public static final int KEYCODE_SPACE = 62;
    public static final int KEYCODE_ENTER = 66;
    public static final int KEYCODE_TAB = 61;
    public static final int KEYCODE_DPAD_UP = 19;
    public static final int KEYCODE_DPAD_DOWN = 20;
    public static final int KEYCODE_DPAD_LEFT = 21;
    public static final int KEYCODE_DPAD_RIGHT = 22;

    public static final int META_SHIFT_ON = 0x1;
    public static final int META_ALT_ON = 0x02;
    public static final int META_CTRL_ON = 0x1000;
    public static final int META_META_ON = 0x10000;

    private final int mAction;
    private final int mKeyCode;
    private final long mEventTime;
    private final int mMetaState;
    private final int mRepeatCount;

    private KeyEvent(int action, int keyCode, long eventTime, int metaState, int repeatCount) {
        this.mAction = action;
        this.mKeyCode = keyCode;
        this.mEventTime = eventTime;
        this.mMetaState = metaState;
        this.mRepeatCount = repeatCount;
    }

    public static KeyEvent obtain(int action, int keyCode, long eventTime, int metaState) {
        return new KeyEvent(action, keyCode, eventTime, metaState, 0);
    }

    public int getAction() {
        return mAction;
    }

    public int getKeyCode() {
        return mKeyCode;
    }

    public long getEventTime() {
        return mEventTime;
    }

    public int getMetaState() {
        return mMetaState;
    }

    public int getRepeatCount() {
        return mRepeatCount;
    }

    public static native int native_updateMetaState(int keyCode, boolean isDown, int metaState);
}
