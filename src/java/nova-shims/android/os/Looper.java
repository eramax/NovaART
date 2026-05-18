package android.os;

public final class Looper {

    private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
    private static Looper sMainLooper;

    final MessageQueue mQueue;
    final Thread mThread;
    private boolean mInLoop;

    private Looper(boolean quitAllowed) {
        mQueue = new MessageQueue(quitAllowed);
        mThread = Thread.currentThread();
    }

    public static void prepare() { prepare(true); }
    public static void prepare(boolean quitAllowed) {
        if (sThreadLocal.get() != null) throw new RuntimeException("Only one Looper per thread");
        sThreadLocal.set(new Looper(quitAllowed));
    }

    public static void prepareMainLooper() {
        prepare(false);
        synchronized (Looper.class) {
            if (sMainLooper != null) throw new IllegalStateException("Main looper already prepared");
            sMainLooper = myLooper();
        }
    }

    public static synchronized Looper getMainLooper() {
        if (sMainLooper == null) {
            // Auto-prepare main looper for in-process use
            Looper l = new Looper(false);
            sMainLooper = l;
            sThreadLocal.set(l);
        }
        return sMainLooper;
    }

    public static Looper myLooper() { return sThreadLocal.get(); }

    public static MessageQueue myQueue() {
        Looper l = myLooper();
        if (l == null) throw new RuntimeException("Not a looper thread");
        return l.mQueue;
    }

    public static void loop() {
        Looper me = myLooper();
        if (me == null) throw new RuntimeException("No Looper; Looper.prepare() not called");
        me.mInLoop = true;
        MessageQueue queue = me.mQueue;
        for (;;) {
            Message msg = queue.next();
            if (msg == null) return;
            try {
                msg.target.dispatchMessage(msg);
            } catch (Exception e) {
                android.util.Log.e("Looper", "Exception in message handling", e);
            }
            msg.recycleUnchecked();
        }
    }

    public void quit()      { mQueue.quit(false); }
    public void quitSafely(){ mQueue.quit(true); }

    public Thread getThread() { return mThread; }
    public MessageQueue getQueue() { return mQueue; }
    public boolean isCurrentThread() { return Thread.currentThread() == mThread; }

    public static boolean isMainThread() {
        return sMainLooper != null && sMainLooper.isCurrentThread();
    }

    @Override public String toString() { return "Looper{" + Integer.toHexString(System.identityHashCode(this)) + "}"; }
}
