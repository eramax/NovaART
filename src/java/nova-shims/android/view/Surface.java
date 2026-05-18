package android.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import nova.internal.CanvasRender;

public class Surface implements android.os.Parcelable {

    public static final int ROTATION_0   = 0;
    public static final int ROTATION_90  = 1;
    public static final int ROTATION_180 = 2;
    public static final int ROTATION_270 = 3;
    private static final String TAG = "NovaSurface";
    private boolean valid = true;
    private android.graphics.SurfaceTexture mSurfaceTexture;
    private int mFrameCount = 0;

    public Surface() {
    }

    public Surface(android.graphics.SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
        Log.d(TAG, "Surface(SurfaceTexture) created, novaBitmap=" + (surfaceTexture != null ? surfaceTexture.novaBitmap : null));
    }

    public Canvas lockCanvas(Rect dirty) {
        if (mSurfaceTexture != null && mSurfaceTexture.novaCanvas != null) {
            mFrameCount++;
            if (mFrameCount <= 3 || mFrameCount % 60 == 0) {
                Log.d(TAG, "lockCanvas #" + mFrameCount);
            }
            return mSurfaceTexture.novaCanvas;
        }
        return new Canvas();
    }

    public void unlockCanvasAndPost(Canvas canvas) {
        if (mSurfaceTexture != null && mSurfaceTexture.novaBitmap != null) {
            if (mFrameCount <= 3 || mFrameCount % 60 == 0) {
                Log.d(TAG, "unlockCanvasAndPost #" + mFrameCount + " → submitFrame");
            }
            try {
                CanvasRender.submitFrame(mSurfaceTexture.novaBitmap);
            } catch (Exception e) {
                Log.e(TAG, "submitFrame failed: " + e);
            }
        }
    }

    public void release() {
        valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    @Override public int describeContents() { return 0; }
    public void readFromParcel(android.os.Parcel in) {}
    @Override public void writeToParcel(android.os.Parcel dest, int flags) {}

    public static final android.os.Parcelable.Creator<Surface> CREATOR = new android.os.Parcelable.Creator<Surface>() {
        @Override public Surface createFromParcel(android.os.Parcel in) { return new Surface(); }
        @Override public Surface[] newArray(int size) { return new Surface[size]; }
    };
}
