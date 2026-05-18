package android.support.customtabs;

public interface ICustomTabsCallback {
    void extraCallback(java.lang.String p0, android.os.Bundle p1);
    android.os.Bundle extraCallbackWithResult(java.lang.String p0, android.os.Bundle p1);
    void onActivityLayout(int p0, int p1, int p2, int p3, int p4, android.os.Bundle p5);
    void onActivityResized(int p0, int p1, android.os.Bundle p2);
    void onMessageChannelReady(android.os.Bundle p0);
    void onMinimized(android.os.Bundle p0);
    void onNavigationEvent(int p0, android.os.Bundle p1);
    void onPostMessage(java.lang.String p0, android.os.Bundle p1);
    void onRelationshipValidationResult(int p0, android.net.Uri p1, boolean p2, android.os.Bundle p3);
    void onUnminimized(android.os.Bundle p0);
    void onWarmupCompleted(android.os.Bundle p0);

    public static class Default {
        public android.os.IBinder asBinder() { return null; }
        public void extraCallback(java.lang.String p0, android.os.Bundle p1) {}
        public android.os.Bundle extraCallbackWithResult(java.lang.String p0, android.os.Bundle p1) { return null; }
        public void onActivityLayout(int p0, int p1, int p2, int p3, int p4, android.os.Bundle p5) {}
        public void onActivityResized(int p0, int p1, android.os.Bundle p2) {}
        public void onMessageChannelReady(android.os.Bundle p0) {}
        public void onMinimized(android.os.Bundle p0) {}
        public void onNavigationEvent(int p0, android.os.Bundle p1) {}
        public void onPostMessage(java.lang.String p0, android.os.Bundle p1) {}
        public void onRelationshipValidationResult(int p0, android.net.Uri p1, boolean p2, android.os.Bundle p3) {}
        public void onUnminimized(android.os.Bundle p0) {}
        public void onWarmupCompleted(android.os.Bundle p0) {}
    }

    public static class Proxy {
        public android.os.IBinder asBinder() { return null; }
        public void extraCallback(java.lang.String p0, android.os.Bundle p1) {}
        public android.os.Bundle extraCallbackWithResult(java.lang.String p0, android.os.Bundle p1) { return null; }
        public java.lang.String getInterfaceDescriptor() { return null; }
        public void onActivityLayout(int p0, int p1, int p2, int p3, int p4, android.os.Bundle p5) {}
        public void onActivityResized(int p0, int p1, android.os.Bundle p2) {}
        public void onMessageChannelReady(android.os.Bundle p0) {}
        public void onMinimized(android.os.Bundle p0) {}
        public void onNavigationEvent(int p0, android.os.Bundle p1) {}
        public void onPostMessage(java.lang.String p0, android.os.Bundle p1) {}
        public void onRelationshipValidationResult(int p0, android.net.Uri p1, boolean p2, android.os.Bundle p3) {}
        public void onUnminimized(android.os.Bundle p0) {}
        public void onWarmupCompleted(android.os.Bundle p0) {}
    }

    public static class Stub {
        public android.os.IBinder asBinder() { return null; }
        public android.support.customtabs.ICustomTabsCallback asInterface(android.os.IBinder p0) { return null; }
        public boolean onTransact(int p0, android.os.Parcel p1, android.os.Parcel p2, int p3) { return false; }
    }

    public static class _Parcel {
        public java.lang.Object access$000(android.os.Parcel p0, android.os.Parcelable.Creator p1) { return null; }
        public void access$100(android.os.Parcel p0, android.os.Parcelable p1, int p2) {}
        public java.lang.Object readTypedObject(android.os.Parcel p0, android.os.Parcelable.Creator p1) { return null; }
        public void writeTypedObject(android.os.Parcel p0, android.os.Parcelable p1, int p2) {}
    }
}
