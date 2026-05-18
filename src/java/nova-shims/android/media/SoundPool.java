package android.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

public class SoundPool {
    public interface OnLoadCompleteListener {
        void onLoadComplete(SoundPool soundPool, int sampleId, int status);
    }

    public static class Builder {
        public Builder setMaxStreams(int maxStreams) { return this; }
        public Builder setAudioAttributes(AudioAttributes attrs) { return this; }
        public SoundPool build() { return new SoundPool(0, 0, 0); }
    }

    public SoundPool(int maxStreams, int streamType, int srcQuality) {}

    public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {}
    public int load(Context context, int resId, int priority) { return 0; }
    public int load(String path, int priority) { return 0; }
    public int load(AssetFileDescriptor afd, int priority) { return 0; }
    public int play(int soundID, float leftVolume, float rightVolume,
                    int priority, int loop, float rate) { return 0; }
    public void stop(int streamID) {}
    public void pause(int streamID) {}
    public void resume(int streamID) {}
    public void autoPause() {}
    public void autoResume() {}
    public void unload(int soundID) {}
    public void setVolume(int streamID, float leftVolume, float rightVolume) {}
    public void setRate(int streamID, float rate) {}
    public void release() {}
}
