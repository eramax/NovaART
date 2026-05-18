package android.media;

public class MediaPlayer {
    public interface OnPreparedListener {
        void onPrepared(MediaPlayer mp);
    }
    public interface OnCompletionListener {
        void onCompletion(MediaPlayer mp);
    }
    public interface OnErrorListener {
        boolean onError(MediaPlayer mp, int what, int extra);
    }
    public interface OnSeekCompleteListener {
        void onSeekComplete(MediaPlayer mp);
    }
    public interface OnInfoListener {
        boolean onInfo(MediaPlayer mp, int what, int extra);
    }
    public interface OnBufferingUpdateListener {
        void onBufferingUpdate(MediaPlayer mp, int percent);
    }

    public static MediaPlayer create(android.content.Context context, int resid) { return null; }

    public MediaPlayer() {}
    public void setDataSource(String path) {}
    public void setDataSource(android.content.Context context, android.net.Uri uri) {}
    public void setDataSource(java.io.FileDescriptor fd) {}
    public void setDataSource(java.io.FileDescriptor fd, long offset, long length) {}
    public void prepare() {}
    public void prepareAsync() {}
    public void start() {}
    public void stop() {}
    public void pause() {}
    public void release() {}
    public void reset() {}
    public void setLooping(boolean looping) {}
    public boolean isPlaying() { return false; }
    public boolean isLooping() { return false; }
    public int getCurrentPosition() { return 0; }
    public int getDuration() { return 0; }
    public void seekTo(int msec) {}
    public void setVolume(float leftVolume, float rightVolume) {}
    public void setAudioStreamType(int streamtype) {}
    public void setOnPreparedListener(OnPreparedListener listener) {}
    public void setOnCompletionListener(OnCompletionListener listener) {}
    public void setOnErrorListener(OnErrorListener listener) {}
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {}
    public void setOnInfoListener(OnInfoListener listener) {}
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {}
    public void setSurface(android.view.Surface surface) {}
    public void setDisplay(android.view.SurfaceHolder sh) {}
    public int getVideoWidth() { return 0; }
    public int getVideoHeight() { return 0; }
}
