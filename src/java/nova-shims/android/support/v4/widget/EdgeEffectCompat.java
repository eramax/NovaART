package android.support.v4.widget;

public class EdgeEffectCompat {
    public boolean draw(android.graphics.Canvas p0) { return false; }
    public void finish() {}
    public boolean isFinished() { return false; }
    public boolean onAbsorb(int p0) { return false; }
    public boolean onPull(float p0) { return false; }
    public boolean onPull(float p0, float p1) { return false; }
    public boolean onRelease() { return false; }
    public void setSize(int p0, int p1) {}

    public static class BaseEdgeEffectImpl {
        public boolean draw(java.lang.Object p0, android.graphics.Canvas p1) { return false; }
        public void finish(java.lang.Object p0) {}
        public boolean isFinished(java.lang.Object p0) { return false; }
        public java.lang.Object newEdgeEffect(android.content.Context p0) { return null; }
        public boolean onAbsorb(java.lang.Object p0, int p1) { return false; }
        public boolean onPull(java.lang.Object p0, float p1) { return false; }
        public boolean onPull(java.lang.Object p0, float p1, float p2) { return false; }
        public boolean onRelease(java.lang.Object p0) { return false; }
        public void setSize(java.lang.Object p0, int p1, int p2) {}
    }

    public static class EdgeEffectIcsImpl {
        public boolean draw(java.lang.Object p0, android.graphics.Canvas p1) { return false; }
        public void finish(java.lang.Object p0) {}
        public boolean isFinished(java.lang.Object p0) { return false; }
        public java.lang.Object newEdgeEffect(android.content.Context p0) { return null; }
        public boolean onAbsorb(java.lang.Object p0, int p1) { return false; }
        public boolean onPull(java.lang.Object p0, float p1) { return false; }
        public boolean onPull(java.lang.Object p0, float p1, float p2) { return false; }
        public boolean onRelease(java.lang.Object p0) { return false; }
        public void setSize(java.lang.Object p0, int p1, int p2) {}
    }

    public static class EdgeEffectImpl {
        public boolean draw(java.lang.Object p0, android.graphics.Canvas p1) { return false; }
        public void finish(java.lang.Object p0) {}
        public boolean isFinished(java.lang.Object p0) { return false; }
        public java.lang.Object newEdgeEffect(android.content.Context p0) { return null; }
        public boolean onAbsorb(java.lang.Object p0, int p1) { return false; }
        public boolean onPull(java.lang.Object p0, float p1) { return false; }
        public boolean onPull(java.lang.Object p0, float p1, float p2) { return false; }
        public boolean onRelease(java.lang.Object p0) { return false; }
        public void setSize(java.lang.Object p0, int p1, int p2) {}
    }

    public static class EdgeEffectLollipopImpl {
        public boolean onPull(java.lang.Object p0, float p1, float p2) { return false; }
    }
}
