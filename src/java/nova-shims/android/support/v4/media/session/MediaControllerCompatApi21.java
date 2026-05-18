package android.support.v4.media.session;

public class MediaControllerCompatApi21 {
    public void adjustVolume(java.lang.Object p0, int p1, int p2) {}
    public java.lang.Object createCallback(android.support.v4.media.session.MediaControllerCompatApi21.Callback p0) { return null; }
    public boolean dispatchMediaButtonEvent(java.lang.Object p0, android.view.KeyEvent p1) { return false; }
    public java.lang.Object fromToken(android.content.Context p0, java.lang.Object p1) { return null; }
    public android.os.Bundle getExtras(java.lang.Object p0) { return null; }
    public long getFlags(java.lang.Object p0) { return 0; }
    public java.lang.Object getMetadata(java.lang.Object p0) { return null; }
    public java.lang.String getPackageName(java.lang.Object p0) { return null; }
    public java.lang.Object getPlaybackInfo(java.lang.Object p0) { return null; }
    public java.lang.Object getPlaybackState(java.lang.Object p0) { return null; }
    public java.util.List getQueue(java.lang.Object p0) { return null; }
    public java.lang.CharSequence getQueueTitle(java.lang.Object p0) { return null; }
    public int getRatingType(java.lang.Object p0) { return 0; }
    public android.app.PendingIntent getSessionActivity(java.lang.Object p0) { return null; }
    public java.lang.Object getTransportControls(java.lang.Object p0) { return null; }
    public void registerCallback(java.lang.Object p0, java.lang.Object p1, android.os.Handler p2) {}
    public void sendCommand(java.lang.Object p0, java.lang.String p1, android.os.Bundle p2, android.os.ResultReceiver p3) {}
    public void setVolumeTo(java.lang.Object p0, int p1, int p2) {}
    public void unregisterCallback(java.lang.Object p0, java.lang.Object p1) {}

    public interface Callback {
        void onMetadataChanged(java.lang.Object p0);
        void onPlaybackStateChanged(java.lang.Object p0);
        void onSessionDestroyed();
        void onSessionEvent(java.lang.String p0, android.os.Bundle p1);
    }

    public static class CallbackProxy {
        public void onMetadataChanged(android.media.MediaMetadata p0) {}
        public void onPlaybackStateChanged(android.media.session.PlaybackState p0) {}
        public void onSessionDestroyed() {}
        public void onSessionEvent(java.lang.String p0, android.os.Bundle p1) {}
    }

    public static class PlaybackInfo {
        public android.media.AudioAttributes getAudioAttributes(java.lang.Object p0) { return null; }
        public int getCurrentVolume(java.lang.Object p0) { return 0; }
        public int getLegacyAudioStream(java.lang.Object p0) { return 0; }
        public int getMaxVolume(java.lang.Object p0) { return 0; }
        public int getPlaybackType(java.lang.Object p0) { return 0; }
        public int getVolumeControl(java.lang.Object p0) { return 0; }
        public int toLegacyStreamType(android.media.AudioAttributes p0) { return 0; }
    }

    public static class TransportControls {
        public void fastForward(java.lang.Object p0) {}
        public void pause(java.lang.Object p0) {}
        public void play(java.lang.Object p0) {}
        public void playFromMediaId(java.lang.Object p0, java.lang.String p1, android.os.Bundle p2) {}
        public void playFromSearch(java.lang.Object p0, java.lang.String p1, android.os.Bundle p2) {}
        public void rewind(java.lang.Object p0) {}
        public void seekTo(java.lang.Object p0, long p1) {}
        public void sendCustomAction(java.lang.Object p0, java.lang.String p1, android.os.Bundle p2) {}
        public void setRating(java.lang.Object p0, java.lang.Object p1) {}
        public void skipToNext(java.lang.Object p0) {}
        public void skipToPrevious(java.lang.Object p0) {}
        public void skipToQueueItem(java.lang.Object p0, long p1) {}
        public void stop(java.lang.Object p0) {}
    }
}
