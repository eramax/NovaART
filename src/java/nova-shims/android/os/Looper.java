package android.os;

public final class Looper {
    private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
    private static final Looper sMainLooper = new Looper(Thread.currentThread());
    private final Thread mThread;

    static {
        sThreadLocal.set(sMainLooper);
    }

    private Looper(Thread thread) {
        mThread = thread;
    }

    public static Looper getMainLooper() {
        return sMainLooper;
    }

    public static Looper myLooper() {
        Looper looper = sThreadLocal.get();
        return looper != null ? looper : sMainLooper;
    }

    public static void loop() {
    }

    public static void prepare() {
        if (sThreadLocal.get() == null) {
            sThreadLocal.set(new Looper(Thread.currentThread()));
        }
    }

    public static void prepareMainLooper() {
        sThreadLocal.set(sMainLooper);
    }

    public Thread getThread() {
        return mThread;
    }
}
