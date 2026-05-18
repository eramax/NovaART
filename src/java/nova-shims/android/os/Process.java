package android.os;

public final class Process {
    private Process() {}

    public static native void setArgV0(String name);
    public static native void setProcessGroup(int pid, int group);
    public static native void setThreadPriority(int tid, int priority);
    public static native int myPid();
    public static native int myUid();
    public static native int getUidForName(String name);
}
