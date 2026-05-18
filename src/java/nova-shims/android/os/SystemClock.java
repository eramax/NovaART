package android.os;

/**
 * Minimal NovaART shim for android.os.SystemClock.
 *
 * This keeps the Java surface aligned with the current JNI registration table
 * without importing the full Android framework implementation.
 */
public final class SystemClock {
    private SystemClock() {}

    public static void sleep(long ms) {
        long start = uptimeMillis();
        long remaining = ms;
        boolean interrupted = false;

        while (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                interrupted = true;
            }
            remaining = start + ms - uptimeMillis();
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    public static native long now();
    public static native long uptimeMillis();
    public static native long elapsedRealtime();
    public static native long elapsedRealtimeNanos();
    public static native long currentThreadTimeMillis();
    public static native long currentThreadTimeMicro();
    public static native long currentTimeMicro();
}
