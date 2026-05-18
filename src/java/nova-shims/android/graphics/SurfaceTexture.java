package android.graphics;

public class SurfaceTexture {
    public interface OnFrameAvailableListener {
        void onFrameAvailable(SurfaceTexture surfaceTexture);
    }

    private int mTexName;
    private OnFrameAvailableListener mListener;

    public SurfaceTexture(int texName) {
        mTexName = texName;
    }

    public SurfaceTexture(boolean singleBufferMode) {}

    public void setOnFrameAvailableListener(OnFrameAvailableListener listener) {
        mListener = listener;
    }

    public void setDefaultBufferSize(int width, int height) {}

    public void updateTexImage() {
        if (mListener != null) mListener.onFrameAvailable(this);
    }

    public void getTransformMatrix(float[] mtx) {
        if (mtx != null && mtx.length >= 16) {
            android.opengl.Matrix.setIdentityM(mtx, 0);
        }
    }

    public long getTimestamp() { return System.nanoTime(); }

    public void attachToGLContext(int texName) { mTexName = texName; }
    public void detachFromGLContext() {}

    public void release() {}
    public boolean isReleased() { return false; }
}
