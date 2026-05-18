package android.os;

import java.util.concurrent.Executor;

public class Handler {

    public interface Callback {
        boolean handleMessage(Message msg);
    }

    final Looper mLooper;
    final MessageQueue mQueue;
    final Callback mCallback;
    final boolean mAsynchronous;

    public Handler() { this(null, false); }
    public Handler(Callback callback) { this(null, false, callback); }
    public Handler(Looper looper) { this(looper, false); }
    public Handler(Looper looper, Callback callback) { this(looper, false, callback); }
    public Handler(boolean async) { this(null, async); }

    public Handler(Looper looper, boolean async) {
        mLooper = looper != null ? looper : Looper.getMainLooper();
        mQueue = mLooper.mQueue;
        mCallback = null;
        mAsynchronous = async;
    }

    public Handler(Looper looper, boolean async, Callback callback) {
        mLooper = looper != null ? looper : Looper.getMainLooper();
        mQueue = mLooper.mQueue;
        mCallback = callback;
        mAsynchronous = async;
    }

    public static Handler createAsync(Looper looper) { return new Handler(looper, true); }
    public static Handler createAsync(Looper looper, Callback callback) { return new Handler(looper, true, callback); }
    public static Handler getMain() { return new Handler(Looper.getMainLooper()); }
    public static Handler mainIfNull(Handler handler) { return handler == null ? getMain() : handler; }

    public final Looper getLooper() { return mLooper; }
    public final MessageQueue getQueue() { return mQueue; }

    public void handleMessage(Message msg) {}

    public void dispatchMessage(Message msg) {
        if (msg.callback != null) { handleCallback(msg); }
        else if (mCallback != null) { if (!mCallback.handleMessage(msg)) handleMessage(msg); }
        else { handleMessage(msg); }
    }

    private static void handleCallback(Message msg) { msg.callback.run(); }

    public final Message obtainMessage() { return Message.obtain(this); }
    public final Message obtainMessage(int what) { return Message.obtain(this, what); }
    public final Message obtainMessage(int what, Object obj) { return Message.obtain(this, what, obj); }
    public final Message obtainMessage(int what, int arg1, int arg2) { return Message.obtain(this, what, arg1, arg2); }
    public final Message obtainMessage(int what, int arg1, int arg2, Object obj) { return Message.obtain(this, what, arg1, arg2, obj); }

    public final boolean post(Runnable r) { return sendMessageDelayed(getPostMessage(r), 0); }
    public final boolean postAtTime(Runnable r, long uptimeMillis) { return sendMessageAtTime(getPostMessage(r), uptimeMillis); }
    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) { return sendMessageAtTime(getPostMessage(r, token), uptimeMillis); }
    public final boolean postDelayed(Runnable r, long delayMillis) { return sendMessageDelayed(getPostMessage(r), delayMillis); }
    public final boolean postDelayed(Runnable r, Object token, long delayMillis) { return sendMessageDelayed(getPostMessage(r, token), delayMillis); }
    public final boolean postAtFrontOfQueue(Runnable r) { return sendMessageAtFrontOfQueue(getPostMessage(r)); }
    public final void removeCallbacks(Runnable r) { mQueue.mMessages.removeIf(m -> m.callback == r); }
    public final void removeCallbacks(Runnable r, Object token) { removeCallbacks(r); }

    public final boolean sendMessage(Message msg) { return sendMessageDelayed(msg, 0); }
    public final boolean sendEmptyMessage(int what) { return sendEmptyMessageDelayed(what, 0); }
    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) { return sendMessageDelayed(obtainMessage(what), delayMillis); }
    public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) { return sendMessageAtTime(obtainMessage(what), uptimeMillis); }
    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (delayMillis < 0) delayMillis = 0;
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }
    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        MessageQueue queue = mQueue;
        if (queue == null) { msg.recycle(); return false; }
        return enqueueMessage(queue, msg, uptimeMillis);
    }
    public final boolean sendMessageAtFrontOfQueue(Message msg) { return enqueueMessage(mQueue, msg, 0); }

    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;
        if (mAsynchronous) msg.setAsynchronous(true);
        return queue.enqueueMessage(msg, uptimeMillis);
    }

    public final void removeMessages(int what) { mQueue.mMessages.removeIf(m -> m.what == what && m.target == this); }
    public final void removeMessages(int what, Object object) { removeMessages(what); }
    public final void removeCallbacksAndMessages(Object token) { mQueue.mMessages.removeIf(m -> m.target == this); }
    public final boolean hasMessages(int what) { return mQueue.mMessages.stream().anyMatch(m -> m.what == what && m.target == this); }
    public final boolean hasMessages(int what, Object obj) { return hasMessages(what); }
    public final boolean hasCallbacks(Runnable r) { return mQueue.mMessages.stream().anyMatch(m -> m.callback == r && m.target == this); }

    public final boolean isIdle() { return mQueue.isIdle(); }
    public String getMessageName(Message msg) { return "0x" + Integer.toHexString(msg.what); }

    private static Message getPostMessage(Runnable r) { Message m = Message.obtain(); m.callback = r; return m; }
    private static Message getPostMessage(Runnable r, Object token) { Message m = Message.obtain(); m.obj = token; m.callback = r; return m; }

    @Override public String toString() { return "Handler{" + Integer.toHexString(System.identityHashCode(this)) + "}"; }
}
