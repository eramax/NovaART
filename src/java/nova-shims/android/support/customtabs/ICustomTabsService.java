package android.support.customtabs;

public class ICustomTabsService {
    public android.os.Bundle extraCommand(java.lang.String p0, android.os.Bundle p1) { return null; }
    public boolean isEngagementSignalsApiAvailable(android.support.customtabs.ICustomTabsCallback p0, android.os.Bundle p1) { return false; }
    public boolean mayLaunchUrl(android.support.customtabs.ICustomTabsCallback p0, android.net.Uri p1, android.os.Bundle p2, java.util.List p3) { return false; }
    public boolean newSession(android.support.customtabs.ICustomTabsCallback p0) { return false; }
    public boolean newSessionWithExtras(android.support.customtabs.ICustomTabsCallback p0, android.os.Bundle p1) { return false; }
    public int postMessage(android.support.customtabs.ICustomTabsCallback p0, java.lang.String p1, android.os.Bundle p2) { return 0; }
    public boolean receiveFile(android.support.customtabs.ICustomTabsCallback p0, android.net.Uri p1, int p2, android.os.Bundle p3) { return false; }
    public boolean requestPostMessageChannel(android.support.customtabs.ICustomTabsCallback p0, android.net.Uri p1) { return false; }
    public boolean requestPostMessageChannelWithExtras(android.support.customtabs.ICustomTabsCallback p0, android.net.Uri p1, android.os.Bundle p2) { return false; }
    public boolean setEngagementSignalsCallback(android.support.customtabs.ICustomTabsCallback p0, android.os.IBinder p1, android.os.Bundle p2) { return false; }
    public boolean updateVisuals(android.support.customtabs.ICustomTabsCallback p0, android.os.Bundle p1) { return false; }
    public boolean validateRelationship(android.support.customtabs.ICustomTabsCallback p0, int p1, android.net.Uri p2, android.os.Bundle p3) { return false; }
    public boolean warmup(long p0) { return false; }

    public static class Default {
        public android.os.IBinder asBinder() { return null; }
        public android.os.Bundle extraCommand(java.lang.String p0, android.os.Bundle p1) { return null; }
        public boolean isEngagementSignalsApiAvailable(android.support.customtabs.ICustomTabsCallback p0, android.os.Bundle p1) { return false; }
        public boolean mayLaunchUrl(android.support.customtabs.ICustomTabsCallback p0, android.net.Uri p1, android.os.Bundle p2, java.util.List p3) { return false; }
        public boolean newSession(android.support.customtabs.ICustomTabsCallback p0) { return false; }
        public boolean newSessionWithExtras(android.support.customtabs.ICustomTabsCallback p0, android.os.Bundle p1) { return false; }
        public int postMessage(android.support.customtabs.ICustomTabsCallback p0, java.lang.String p1, android.os.Bundle p2) { return 0; }
        public boolean receiveFile(android.support.customtabs.ICustomTabsCallback p0, android.net.Uri p1, int p2, android.os.Bundle p3) { return false; }
        public boolean requestPostMessageChannel(android.support.customtabs.ICustomTabsCallback p0, android.net.Uri p1) { return false; }
        public boolean requestPostMessageChannelWithExtras(android.support.customtabs.ICustomTabsCallback p0, android.net.Uri p1, android.os.Bundle p2) { return false; }
        public boolean setEngagementSignalsCallback(android.support.customtabs.ICustomTabsCallback p0, android.os.IBinder p1, android.os.Bundle p2) { return false; }
        public boolean updateVisuals(android.support.customtabs.ICustomTabsCallback p0, android.os.Bundle p1) { return false; }
        public boolean validateRelationship(android.support.customtabs.ICustomTabsCallback p0, int p1, android.net.Uri p2, android.os.Bundle p3) { return false; }
        public boolean warmup(long p0) { return false; }
    }

    public static class Proxy {
        public android.os.IBinder asBinder() { return null; }
        public android.os.Bundle extraCommand(java.lang.String p0, android.os.Bundle p1) { return null; }
        public java.lang.String getInterfaceDescriptor() { return null; }
        public boolean isEngagementSignalsApiAvailable(android.support.customtabs.ICustomTabsCallback p0, android.os.Bundle p1) { return false; }
        public boolean mayLaunchUrl(android.support.customtabs.ICustomTabsCallback p0, android.net.Uri p1, android.os.Bundle p2, java.util.List p3) { return false; }
        public boolean newSession(android.support.customtabs.ICustomTabsCallback p0) { return false; }
        public boolean newSessionWithExtras(android.support.customtabs.ICustomTabsCallback p0, android.os.Bundle p1) { return false; }
        public int postMessage(android.support.customtabs.ICustomTabsCallback p0, java.lang.String p1, android.os.Bundle p2) { return 0; }
        public boolean receiveFile(android.support.customtabs.ICustomTabsCallback p0, android.net.Uri p1, int p2, android.os.Bundle p3) { return false; }
        public boolean requestPostMessageChannel(android.support.customtabs.ICustomTabsCallback p0, android.net.Uri p1) { return false; }
        public boolean requestPostMessageChannelWithExtras(android.support.customtabs.ICustomTabsCallback p0, android.net.Uri p1, android.os.Bundle p2) { return false; }
        public boolean setEngagementSignalsCallback(android.support.customtabs.ICustomTabsCallback p0, android.os.IBinder p1, android.os.Bundle p2) { return false; }
        public boolean updateVisuals(android.support.customtabs.ICustomTabsCallback p0, android.os.Bundle p1) { return false; }
        public boolean validateRelationship(android.support.customtabs.ICustomTabsCallback p0, int p1, android.net.Uri p2, android.os.Bundle p3) { return false; }
        public boolean warmup(long p0) { return false; }
        public boolean warmup() { return false; }
    }

    public static class Stub {
        public android.os.IBinder asBinder() { return null; }
        public android.support.customtabs.ICustomTabsService asInterface(android.os.IBinder p0) { return null; }
        public boolean onTransact(int p0, android.os.Parcel p1, android.os.Parcel p2, int p3) { return false; }
    }

    public static class _Parcel {
        public java.lang.Object access$000(android.os.Parcel p0, android.os.Parcelable.Creator p1) { return null; }
        public void access$100(android.os.Parcel p0, android.os.Parcelable p1, int p2) {}
        public void access$200(android.os.Parcel p0, java.util.List p1, int p2) {}
        public java.lang.Object readTypedObject(android.os.Parcel p0, android.os.Parcelable.Creator p1) { return null; }
        public void writeTypedList(android.os.Parcel p0, java.util.List p1, int p2) {}
        public void writeTypedObject(android.os.Parcel p0, android.os.Parcelable p1, int p2) {}
    }
}
