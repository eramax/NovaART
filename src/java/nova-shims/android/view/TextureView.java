package android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import nova.internal.CanvasRender;

public class TextureView extends View {
    private static final String TAG = "NovaTextureView";

    public interface SurfaceTextureListener {
        void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);
        void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height);
        boolean onSurfaceTextureDestroyed(SurfaceTexture surface);
        void onSurfaceTextureUpdated(SurfaceTexture surface);
    }

    private SurfaceTextureListener mListener;
    private SurfaceTexture mSurfaceTexture;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private int mWidth  = 960;
    private int mHeight = 540;
    private boolean mAvailable;

    public TextureView(Context context) {
        super(context);
    }

    public TextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSurfaceTextureListener(SurfaceTextureListener listener) {
        mListener = listener;
        if (mAvailable && mListener != null && mSurfaceTexture != null) {
            mListener.onSurfaceTextureAvailable(mSurfaceTexture, mWidth, mHeight);
        }
    }

    public SurfaceTextureListener getSurfaceTextureListener() { return mListener; }

    public SurfaceTexture getSurfaceTexture() { return mSurfaceTexture; }

    public boolean isAvailable() { return mAvailable; }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initSurface();
    }

    private void initSurface() {
        if (mAvailable) return;
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mSurfaceTexture = new SurfaceTexture(0);
        mAvailable = true;
        Log.d(TAG, "TextureView surface available " + mWidth + "x" + mHeight);
        if (mListener != null) {
            mListener.onSurfaceTextureAvailable(mSurfaceTexture, mWidth, mHeight);
        }
    }

    public Canvas lockCanvas() {
        return lockCanvas(null);
    }

    public Canvas lockCanvas(Rect dirty) {
        if (!mAvailable) initSurface();
        return mCanvas;
    }

    public void unlockCanvasAndPost(Canvas canvas) {
        if (mBitmap == null) return;
        try {
            CanvasRender.submitFrame(mBitmap);
        } catch (Exception e) {
            Log.e(TAG, "unlockCanvasAndPost failed: " + e);
        }
    }

    public Bitmap getBitmap() { return mBitmap; }

    public Bitmap getBitmap(int width, int height) {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    public void setOpaque(boolean opaque) {}
    public boolean isOpaque() { return true; }

    public void setLayerType(int layerType, android.graphics.Paint paint) {}
}
