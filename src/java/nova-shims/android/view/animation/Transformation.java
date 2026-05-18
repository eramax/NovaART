package android.view.animation;

public class Transformation {
    public static final int TYPE_IDENTITY = 0x0;
    public static final int TYPE_ALPHA = 0x1;
    public static final int TYPE_MATRIX = 0x2;
    public static final int TYPE_BOTH = TYPE_ALPHA | TYPE_MATRIX;

    protected float mAlpha = 1.0f;
    protected int mTransformationType = TYPE_IDENTITY;

    public void clear() { mAlpha = 1.0f; mTransformationType = TYPE_IDENTITY; }
    public void set(Transformation t) { mAlpha = t.mAlpha; mTransformationType = t.mTransformationType; }
    public float getAlpha() { return mAlpha; }
    public void setAlpha(float alpha) { mAlpha = alpha; }
    public int getTransformationType() { return mTransformationType; }
    public void setTransformationType(int transformationType) { mTransformationType = transformationType; }
    public void compose(Transformation t) { mAlpha *= t.mAlpha; }
    public android.graphics.Matrix getMatrix() { return new android.graphics.Matrix(); }
}
