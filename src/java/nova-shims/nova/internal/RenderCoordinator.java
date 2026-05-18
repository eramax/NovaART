package nova.internal;

import android.view.View;
import android.view.Choreographer;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public final class RenderCoordinator {
    private static final String TAG = "RenderCoordinator";
    private static RenderCoordinator sInstance;
    private volatile boolean mRunning;
    private Thread mRenderThread;
    private View mRootView;
    private Bitmap mBackBuffer;
    private Canvas mBackCanvas;
    private final Object mLock = new Object();
    private long mFrameTimeNanos;

    public static synchronized RenderCoordinator getInstance() {
        if (sInstance == null) {
            sInstance = new RenderCoordinator();
        }
        return sInstance;
    }

    private RenderCoordinator() {
        mRunning = false;
        mFrameTimeNanos = System.nanoTime();
    }

    public void start(View rootView, int width, int height) {
        if (mRunning) return;

        Log.d(TAG, "Starting render coordinator for " + width + "x" + height);
        synchronized (mLock) {
            mRootView = rootView;
            mBackBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mBackCanvas = new Canvas(mBackBuffer);
            mRunning = true;
        }

        mRenderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                renderLoop();
            }
        }, "NovaRenderThread");
        mRenderThread.start();
    }

    public void stop() {
        mRunning = false;
        if (mRenderThread != null) {
            try {
                mRenderThread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted waiting for render thread", e);
            }
            mRenderThread = null;
        }
        synchronized (mLock) {
            mBackCanvas = null;
            if (mBackBuffer != null) {
                mBackBuffer.recycle();
                mBackBuffer = null;
            }
        }
    }

    private void renderLoop() {
        while (mRunning) {
            try {
                synchronized (mLock) {
                    if (mBackCanvas != null && mRootView != null) {
                        mBackCanvas.drawColor(Color.WHITE);
                        mRootView.draw(mBackCanvas);
                        submitFrame();
                    }
                }

                mFrameTimeNanos = System.nanoTime();
                Choreographer.notifyFrameTime(mFrameTimeNanos);

                Thread.sleep(16);
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                Log.e(TAG, "Error in render loop", e);
            }
        }
    }

    private void submitFrame() {
        if (mBackBuffer == null) return;
        try {
            CanvasRender.submitFrame(mBackBuffer);
        } catch (Exception e) {
            Log.e(TAG, "Error submitting frame", e);
        }
    }
}
