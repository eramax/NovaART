package android.graphics;

public class SurfaceTexture {

    public interface OnFrameAvailableListener {
        void onFrameAvailable(SurfaceTexture surfaceTexture);
    }

    // Nova extensions for in-process rendering
    public Bitmap novaBitmap;
    public Canvas novaCanvas;

    private int mTexName;
    private boolean mReleased;

    public SurfaceTexture(int texName) { mTexName = texName; }
    public SurfaceTexture(boolean singleBufferMode) {}
    public SurfaceTexture(int texName, boolean singleBufferMode) { mTexName = texName; }

    public void setOnFrameAvailableListener(OnFrameAvailableListener listener) {}
    public void setOnFrameAvailableListener(OnFrameAvailableListener listener, android.os.Handler handler) {}
    public void setDefaultBufferSize(int width, int height) {}
    public void updateTexImage() {}
    public void releaseTexImage() {}
    public void getTransformMatrix(float[] mtx) {
        if (mtx != null && mtx.length >= 16) {
            // Identity matrix
            mtx[0]=1; mtx[1]=0; mtx[2]=0; mtx[3]=0;
            mtx[4]=0; mtx[5]=1; mtx[6]=0; mtx[7]=0;
            mtx[8]=0; mtx[9]=0; mtx[10]=1; mtx[11]=0;
            mtx[12]=0; mtx[13]=0; mtx[14]=0; mtx[15]=1;
        }
    }
    public long getTimestamp() { return System.nanoTime(); }
    public void release() { mReleased = true; }
    public boolean isReleased() { return mReleased; }
    public void detachFromGLContext() {}
    public void attachToGLContext(int texName) { mTexName = texName; }
}
