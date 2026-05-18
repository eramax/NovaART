package android.graphics.drawable;

import android.graphics.Canvas;

public abstract class Drawable {
    public abstract void draw(Canvas canvas);
    public int getIntrinsicWidth() { return -1; }
    public int getIntrinsicHeight() { return -1; }
    public void setBounds(int left, int top, int right, int bottom) {}
    public void setAlpha(int alpha) {}
    public int getOpacity() { return -3; /* PixelFormat.TRANSLUCENT */ }
}
