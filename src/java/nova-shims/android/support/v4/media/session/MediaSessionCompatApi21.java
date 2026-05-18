package android.support.v4.media.session;

public class MediaSessionCompatApi21 {
    public java.lang.Object createCallback(android.support.v4.media.session.MediaSessionCompatApi21.Callback p0) { return null; }
    public java.lang.Object createSession(android.content.Context p0, java.lang.String p1) { return null; }
    public android.os.Parcelable getSessionToken(java.lang.Object p0) { return null; }
    public boolean isActive(java.lang.Object p0) { return false; }
    public void release(java.lang.Object p0) {}
    public void sendSessionEvent(java.lang.Object p0, java.lang.String p1, android.os.Bundle p2) {}
    public void setActive(java.lang.Object p0, boolean p1) {}
    public void setCallback(java.lang.Object p0, java.lang.Object p1, android.os.Handler p2) {}
    public void setExtras(java.lang.Object p0, android.os.Bundle p1) {}
    public void setFlags(java.lang.Object p0, int p1) {}
    public void setMediaButtonReceiver(java.lang.Object p0, android.app.PendingIntent p1) {}
    public void setMetadata(java.lang.Object p0, java.lang.Object p1) {}
    public void setPlaybackState(java.lang.Object p0, java.lang.Object p1) {}
    public void setPlaybackToLocal(java.lang.Object p0, int p1) {}
    public void setPlaybackToRemote(java.lang.Object p0, java.lang.Object p1) {}
    public void setQueue(java.lang.Object p0, java.util.List p1) {}
    public void setQueueTitle(java.lang.Object p0, java.lang.CharSequence p1) {}
    public void setSessionActivity(java.lang.Object p0, android.app.PendingIntent p1) {}
    public java.lang.Object verifySession(java.lang.Object p0) { return null; }
    public java.lang.Object verifyToken(java.lang.Object p0) { return null; }

    public interface Callback {
        void onCommand(java.lang.String p0, android.os.Bundle p1, android.os.ResultReceiver p2);
        void onCustomAction(java.lang.String p0, android.os.Bundle p1);
        void onFastForward();
        boolean onMediaButtonEvent(android.content.Intent p0);
        void onPause();
        void onPlay();
        void onPlayFromMediaId(java.lang.String p0, android.os.Bundle p1);
        void onPlayFromSearch(java.lang.String p0, android.os.Bundle p1);
        void onRewind();
        void onSeekTo(long p0);
        void onSetRating(java.lang.Object p0);
        void onSkipToNext();
        void onSkipToPrevious();
        void onSkipToQueueItem(long p0);
        void onStop();
    }

    public static class CallbackProxy {
        public void onCommand(java.lang.String p0, android.os.Bundle p1, android.os.ResultReceiver p2) {}
        public void onCustomAction(java.lang.String p0, android.os.Bundle p1) {}
        public void onFastForward() {}
        public boolean onMediaButtonEvent(android.content.Intent p0) { return false; }
        public void onPause() {}
        public void onPlay() {}
        public void onPlayFromMediaId(java.lang.String p0, android.os.Bundle p1) {}
        public void onPlayFromSearch(java.lang.String p0, android.os.Bundle p1) {}
        public void onRewind() {}
        public void onSeekTo(long p0) {}
        public void onSetRating(android.media.Rating p0) {}
        public void onSkipToNext() {}
        public void onSkipToPrevious() {}
        public void onSkipToQueueItem(long p0) {}
        public void onStop() {}
    }

    public static class QueueItem {
        public java.lang.Object createItem(java.lang.Object p0, long p1) { return null; }
        public java.lang.Object getDescription(java.lang.Object p0) { return null; }
        public long getQueueId(java.lang.Object p0) { return 0; }
    }
}
