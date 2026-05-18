package android.os;

public final class SystemClock {

    private SystemClock() {}

    public static native long now();
    public static native long uptimeMillis();
    public static native long elapsedRealtime();
    public static native long elapsedRealtimeNanos();
    public static native long currentThreadTimeMillis();
    public static native long currentThreadTimeMicro();
    public static native long currentTimeMicro();

    public static boolean sleep(long ms) {
        try { Thread.sleep(ms); return true; }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); return false; }
    }

    public static java.time.Clock currentGnssTimeClock() { return java.time.Clock.systemUTC(); }
}
