package android.os;

public final class Trace {

    public static final long TRACE_TAG_NEVER              = 0;
    public static final long TRACE_TAG_ALWAYS             = 1L << 0;
    public static final long TRACE_TAG_GRAPHICS           = 1L << 1;
    public static final long TRACE_TAG_INPUT              = 1L << 2;
    public static final long TRACE_TAG_VIEW               = 1L << 3;
    public static final long TRACE_TAG_WEBVIEW            = 1L << 4;
    public static final long TRACE_TAG_WINDOW_MANAGER     = 1L << 5;
    public static final long TRACE_TAG_ACTIVITY_MANAGER   = 1L << 6;
    public static final long TRACE_TAG_SYNC_MANAGER       = 1L << 7;
    public static final long TRACE_TAG_AUDIO              = 1L << 8;
    public static final long TRACE_TAG_VIDEO              = 1L << 9;
    public static final long TRACE_TAG_CAMERA             = 1L << 10;
    public static final long TRACE_TAG_HAL                = 1L << 11;
    public static final long TRACE_TAG_APP                = 1L << 12;
    public static final long TRACE_TAG_RESOURCES          = 1L << 13;
    public static final long TRACE_TAG_DALVIK             = 1L << 14;
    public static final long TRACE_TAG_RS                 = 1L << 15;
    public static final long TRACE_TAG_BIONIC             = 1L << 16;
    public static final long TRACE_TAG_POWER              = 1L << 17;
    public static final long TRACE_TAG_PACKAGE_MANAGER    = 1L << 18;
    public static final long TRACE_TAG_SYSTEM_SERVER      = 1L << 19;
    public static final long TRACE_TAG_DATABASE           = 1L << 20;
    public static final long TRACE_TAG_NETWORK            = 1L << 21;
    public static final long TRACE_TAG_ADB                = 1L << 22;
    public static final long TRACE_TAG_VIBRATOR           = 1L << 23;
    public static final long TRACE_TAG_AIDL               = 1L << 24;
    public static final long TRACE_TAG_THERMAL            = 1L << 25;

    private Trace() {}

    public static boolean isTagEnabled(long traceTag) { return false; }
    public static void traceCounter(long traceTag, String counterName, int counterValue) {}
    public static void traceBegin(long traceTag, String methodName) {}
    public static void traceEnd(long traceTag) {}
    public static void asyncTraceBegin(long traceTag, String methodName, int cookie) {}
    public static void asyncTraceEnd(long traceTag, String methodName, int cookie) {}
    public static void asyncTraceForTrackBegin(long traceTag, String trackName, String methodName, int cookie) {}
    public static void asyncTraceForTrackEnd(long traceTag, String trackName, int cookie) {}
    public static void instant(long traceTag, String eventName) {}
    public static void instantForTrack(long traceTag, String trackName, String eventName) {}
    public static void setAppTracingAllowed(boolean allowed) {}
    public static void setTracingEnabled(boolean enabled, int flags) {}
    public static long queryTraceTagsChangedCallback() { return 0L; }
    public static void forceEnableAppTracing() {}
}
