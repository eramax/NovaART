package android.media;

import android.content.Context;
import android.net.Uri;

public class MediaPlayer {
    public interface OnPreparedListener { void onPrepared(MediaPlayer mp); }
    public interface OnErrorListener { boolean onError(MediaPlayer mp, int what, int extra); }
    public interface OnCompletionListener { void onCompletion(MediaPlayer mp); }

    public static MediaPlayer create(Context context, int resid) { return new MediaPlayer(); }
    public static MediaPlayer create(Context context, Uri uri) { return new MediaPlayer(); }

    public void setOnPreparedListener(OnPreparedListener l) {}
    public void setOnErrorListener(OnErrorListener l) {}
    public void setOnCompletionListener(OnCompletionListener l) {}
    public void setLooping(boolean looping) {}
    public void setVolume(float leftVolume, float rightVolume) {}
    public void start() {}
    public void pause() {}
    public void stop() {}
    public void release() {}
    public void reset() {}
    public void prepare() {}
    public void prepareAsync() {}
    public boolean isPlaying() { return false; }
    public int getDuration() { return 0; }
    public int getCurrentPosition() { return 0; }
    public void seekTo(int msec) {}
    public void setDataSource(String path) {}
    public void setDataSource(java.io.FileDescriptor fd) {}
    public void setDataSource(java.io.FileDescriptor fd, long offset, long length) {}
    public void setDataSource(Context context, Uri uri) {}
    public void setAudioStreamType(int streamtype) {}
    public void setAudioAttributes(AudioAttributes attrs) {}
}
