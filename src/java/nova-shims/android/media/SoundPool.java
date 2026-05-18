package android.media;

public class SoundPool {
    public interface OnLoadCompleteListener {
        void onLoadComplete(SoundPool soundPool, int sampleId, int status);
    }

    public SoundPool(int maxStreams, int streamType, int srcQuality) {}
    public int load(String path, int priority) { return 0; }
    public int load(android.content.Context context, int resId, int priority) { return 0; }
    public int load(java.io.FileDescriptor fd, long offset, long length, int priority) { return 0; }
    public int load(android.content.res.AssetFileDescriptor afd, int priority) { return 0; }
    public final int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) { return 0; }
    public final void stop(int streamID) {}
    public final void pause(int streamID) {}
    public final void resume(int streamID) {}
    public final void setVolume(int streamID, float leftVolume, float rightVolume) {}
    public final void setLoop(int streamID, int loop) {}
    public final void setRate(int streamID, float rate) {}
    public final void setPriority(int streamID, int priority) {}
    public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {}
    public void release() {}
    public void autoPause() {}
    public void autoResume() {}
    public void unload(int soundID) { return; }
}
