package android.os;

public final class Process {

    public static final int SYSTEM_UID   = 1000;
    public static final int PHONE_UID    = 1001;
    public static final int SHELL_UID    = 2000;
    public static final int LOG_UID      = 1007;
    public static final int WIFI_UID     = 1010;
    public static final int MEDIA_UID    = 1013;
    public static final int AUDIOSERVER_UID = 1041;
    public static final int CAMERASERVER_UID = 1047;
    public static final int ROOT_UID     = 0;
    public static final int INVALID_UID  = -1;

    public static final int THREAD_PRIORITY_DEFAULT        = 0;
    public static final int THREAD_PRIORITY_LOWEST         = 19;
    public static final int THREAD_PRIORITY_BACKGROUND     = 10;
    public static final int THREAD_PRIORITY_FOREGROUND     = -2;
    public static final int THREAD_PRIORITY_DISPLAY        = -4;
    public static final int THREAD_PRIORITY_URGENT_DISPLAY = -8;
    public static final int THREAD_PRIORITY_AUDIO          = -16;
    public static final int THREAD_PRIORITY_URGENT_AUDIO   = -19;

    public static native void setArgV0(String name);
    public static native void setProcessGroup(int pid, int group);
    public static native void setThreadPriority(int tid, int priority);
    public static native int  myPid();
    public static native int  myUid();
    public static native int  getUidForName(String name);

    public static int myTid() { return (int) Thread.currentThread().getId(); }
    public static int myUserHandle() { return 0; }
    public static boolean isIsolated() { return false; }
    public static void killProcess(int pid) {}
    public static boolean supportsProcesses() { return true; }
    public static long getElapsedCpuTime() { return SystemClock.elapsedRealtime(); }
}
