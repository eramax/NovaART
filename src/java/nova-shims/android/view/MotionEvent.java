package android.view;

public class MotionEvent {
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_UP = 1;
    public static final int ACTION_MOVE = 2;
    public static final int ACTION_CANCEL = 3;
    public static final int ACTION_OUTSIDE = 4;

    public static final int TOOL_TYPE_UNKNOWN = 0;
    public static final int TOOL_TYPE_FINGER = 1;
    public static final int TOOL_TYPE_STYLUS = 2;
    public static final int TOOL_TYPE_MOUSE = 3;

    private final long mEventTime;
    private final int mAction;
    private final float mX;
    private final float mY;
    private final int mPointerCount;
    private final int mMetaState;

    private MotionEvent(long eventTime, int action, float x, float y, int pointerCount, int metaState) {
        this.mEventTime = eventTime;
        this.mAction = action;
        this.mX = x;
        this.mY = y;
        this.mPointerCount = pointerCount;
        this.mMetaState = metaState;
    }

    public static MotionEvent obtain(long eventTime, int action, float x, float y) {
        return new MotionEvent(eventTime, action, x, y, 1, 0);
    }

    public int getAction() {
        return mAction;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public long getEventTime() {
        return mEventTime;
    }

    public int getPointerCount() {
        return mPointerCount;
    }

    public int getMetaState() {
        return mMetaState;
    }

    public void recycle() {
    }

    public static native void native_classifyMotionEvent(MotionEvent event);
}
