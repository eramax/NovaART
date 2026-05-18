package android.view;

import android.graphics.Point;
import android.util.DisplayMetrics;

public final class Display {
    public static final int DEFAULT_DISPLAY = 0;

    private final int mWidth;
    private final int mHeight;

    public Display(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getDisplayId() { return DEFAULT_DISPLAY; }
    public int getWidth()  { return mWidth; }
    public int getHeight() { return mHeight; }

    public void getSize(Point outSize) {
        if (outSize != null) { outSize.x = mWidth; outSize.y = mHeight; }
    }

    public void getMetrics(DisplayMetrics outMetrics) {
        if (outMetrics != null) {
            outMetrics.widthPixels = mWidth;
            outMetrics.heightPixels = mHeight;
        }
    }

    public int getRotation() { return 0; }
    public float getRefreshRate() { return 60.0f; }
}
