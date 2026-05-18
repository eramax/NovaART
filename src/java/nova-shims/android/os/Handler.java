package android.os;

import android.util.Log;

public class Handler {
    private static final String TAG = "NovaHandler";

    public interface Callback {
        boolean handleMessage(Message msg);
    }

    private final Looper mLooper;
    private final Callback mCallback;

    public Handler() {
        mLooper = Looper.myLooper();
        mCallback = null;
    }

    public Handler(Callback callback) {
        mLooper = Looper.myLooper();
        mCallback = callback;
    }

    public Handler(Looper looper) {
        mLooper = looper;
        mCallback = null;
    }

    public Handler(Looper looper, Callback callback) {
        mLooper = looper;
        mCallback = callback;
    }

    public void handleMessage(Message msg) {}

    public final boolean sendMessage(Message msg) {
        return sendMessageDelayed(msg, 0);
    }

    public final boolean sendEmptyMessage(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessage(msg);
    }

    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (delayMillis <= 0) {
            dispatchMessage(msg);
        } else {
            new Thread(() -> {
                try { Thread.sleep(delayMillis); } catch (InterruptedException e) { return; }
                dispatchMessage(msg);
            }).start();
        }
        return true;
    }

    public final boolean post(Runnable r) {
        Message msg = Message.obtain();
        msg.callback = r;
        return sendMessage(msg);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        Message msg = Message.obtain();
        msg.callback = r;
        return sendMessageDelayed(msg, delayMillis);
    }

    public final Looper getLooper() { return mLooper; }

    public final void removeCallbacksAndMessages(Object token) {}
    public final void removeCallbacks(Runnable r) {}
    public final void removeMessages(int what) {}
    public final boolean hasMessages(int what) { return false; }
    public final boolean sendMessageAtFrontOfQueue(Message msg) { return sendMessage(msg); }
    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        return sendMessageDelayed(Message.obtain(this, what), delayMillis);
    }

    public final void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            msg.callback.run();
        } else if (mCallback != null) {
            if (!mCallback.handleMessage(msg)) {
                handleMessage(msg);
            }
        } else {
            handleMessage(msg);
        }
    }
}
