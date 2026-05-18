package android.media.session;

public class MediaController {
    public void adjustVolume(int p0, int p1) {}
    public boolean dispatchMediaButtonEvent(android.view.KeyEvent p0) { return false; }
    public android.os.Bundle getExtras() { return null; }
    public long getFlags() { return 0; }
    public android.media.MediaMetadata getMetadata() { return null; }
    public java.lang.String getPackageName() { return null; }
    public android.media.session.MediaController.PlaybackInfo getPlaybackInfo() { return null; }
    public android.media.session.PlaybackState getPlaybackState() { return null; }
    public java.util.List getQueue() { return null; }
    public java.lang.CharSequence getQueueTitle() { return null; }
    public int getRatingType() { return 0; }
    public android.app.PendingIntent getSessionActivity() { return null; }
    public android.os.Bundle getSessionInfo() { return null; }
    public android.media.session.MediaSession.Token getSessionToken() { return null; }
    public android.media.session.MediaController.TransportControls getTransportControls() { return null; }
    public void registerCallback(android.media.session.MediaController.Callback p0, android.os.Handler p1) {}
    public void sendCommand(java.lang.String p0, android.os.Bundle p1, android.os.ResultReceiver p2) {}
    public void setVolumeTo(int p0, int p1) {}
    public void unregisterCallback(android.media.session.MediaController.Callback p0) {}

    public interface Callback {
    }

    public static class PlaybackInfo {
        public android.media.AudioAttributes getAudioAttributes() { return null; }
        public int getCurrentVolume() { return 0; }
        public int getMaxVolume() { return 0; }
        public int getPlaybackType() { return 0; }
        public int getVolumeControl() { return 0; }
    }

    public static class TransportControls {
        public void fastForward() {}
        public void pause() {}
        public void play() {}
        public void playFromMediaId(java.lang.String p0, android.os.Bundle p1) {}
        public void playFromSearch(java.lang.String p0, android.os.Bundle p1) {}
        public void playFromUri(android.net.Uri p0, android.os.Bundle p1) {}
        public void prepare() {}
        public void prepareFromMediaId(java.lang.String p0, android.os.Bundle p1) {}
        public void prepareFromSearch(java.lang.String p0, android.os.Bundle p1) {}
        public void prepareFromUri(android.net.Uri p0, android.os.Bundle p1) {}
        public void rewind() {}
        public void seekTo(long p0) {}
        public void sendCustomAction(java.lang.String p0, android.os.Bundle p1) {}
        public void setPlaybackSpeed(float p0) {}
        public void setRating(android.media.Rating p0) {}
        public void skipToNext() {}
        public void skipToPrevious() {}
        public void skipToQueueItem(long p0) {}
        public void stop() {}
    }
}
