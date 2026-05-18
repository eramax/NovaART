package android.support.v4.widget;

public class MaterialProgressDrawable {
    public void access$000(android.support.v4.widget.MaterialProgressDrawable p0, float p1, android.support.v4.widget.MaterialProgressDrawable.Ring p2) {}
    public float access$100(android.support.v4.widget.MaterialProgressDrawable p0, android.support.v4.widget.MaterialProgressDrawable.Ring p1) { return 0f; }
    public void access$200(android.support.v4.widget.MaterialProgressDrawable p0, float p1, android.support.v4.widget.MaterialProgressDrawable.Ring p2) {}
    public android.view.animation.Interpolator access$300() { return null; }
    public float access$400(android.support.v4.widget.MaterialProgressDrawable p0) { return 0f; }
    public float access$402(android.support.v4.widget.MaterialProgressDrawable p0, float p1) { return 0f; }
    public void applyFinishTranslation(float p0, android.support.v4.widget.MaterialProgressDrawable.Ring p1) {}
    public void draw(android.graphics.Canvas p0) {}
    public int evaluateColorChange(float p0, int p1, int p2) { return 0; }
    public int getAlpha() { return 0; }
    public android.graphics.Rect getBounds() { return null; }
    public int getIntrinsicHeight() { return 0; }
    public int getIntrinsicWidth() { return 0; }
    public float getMinProgressArc(android.support.v4.widget.MaterialProgressDrawable.Ring p0) { return 0f; }
    public int getOpacity() { return 0; }
    public float getRotation() { return 0f; }
    public void invalidateSelf() {}
    public boolean isRunning() { return false; }
    public void scheduleSelf(java.lang.Runnable p0, long p1) {}
    public void setAlpha(int p0) {}
    public void setArrowScale(float p0) {}
    public void setBackgroundColor(int p0) {}
    public void setColorFilter(android.graphics.ColorFilter p0) {}
    public void setColorSchemeColors(int[] p0) {}
    public void setProgressRotation(float p0) {}
    public void setRotation(float p0) {}
    public void setSizeParameters(double p0, double p1, double p2, double p3, float p4, float p5) {}
    public void setStartEndTrim(float p0, float p1) {}
    public void setupAnimators() {}
    public void showArrow(boolean p0) {}
    public void start() {}
    public void stop() {}
    public void unscheduleSelf(java.lang.Runnable p0) {}
    public void updateRingColor(float p0, android.support.v4.widget.MaterialProgressDrawable.Ring p1) {}
    public void updateSizes(int p0) {}

    public static class Ring {
        public void draw(android.graphics.Canvas p0, android.graphics.Rect p1) {}
        public void drawTriangle(android.graphics.Canvas p0, float p1, float p2, android.graphics.Rect p3) {}
        public int getAlpha() { return 0; }
        public double getCenterRadius() { return 0.0; }
        public float getEndTrim() { return 0f; }
        public float getInsets() { return 0f; }
        public int getNextColor() { return 0; }
        public int getNextColorIndex() { return 0; }
        public float getRotation() { return 0f; }
        public float getStartTrim() { return 0f; }
        public int getStartingColor() { return 0; }
        public float getStartingEndTrim() { return 0f; }
        public float getStartingRotation() { return 0f; }
        public float getStartingStartTrim() { return 0f; }
        public float getStrokeWidth() { return 0f; }
        public void goToNextColor() {}
        public void invalidateSelf() {}
        public void resetOriginals() {}
        public void setAlpha(int p0) {}
        public void setArrowDimensions(float p0, float p1) {}
        public void setArrowScale(float p0) {}
        public void setBackgroundColor(int p0) {}
        public void setCenterRadius(double p0) {}
        public void setColor(int p0) {}
        public void setColorFilter(android.graphics.ColorFilter p0) {}
        public void setColorIndex(int p0) {}
        public void setColors(int[] p0) {}
        public void setEndTrim(float p0) {}
        public void setInsets(int p0, int p1) {}
        public void setRotation(float p0) {}
        public void setShowArrow(boolean p0) {}
        public void setStartTrim(float p0) {}
        public void setStrokeWidth(float p0) {}
        public void storeOriginals() {}
    }
}
