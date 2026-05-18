package android.graphics;

public final class RectF {
    public float left;
    public float top;
    public float right;
    public float bottom;

    public RectF() {}

    public RectF(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public RectF(Rect r) {
        if (r != null) { left = r.left; top = r.top; right = r.right; bottom = r.bottom; }
    }

    public RectF(RectF r) {
        if (r != null) { left = r.left; top = r.top; right = r.right; bottom = r.bottom; }
    }

    public void set(RectF src) {
        left = src.left; top = src.top; right = src.right; bottom = src.bottom;
    }

    public void inset(float dx, float dy) {
        left += dx; top += dy; right -= dx; bottom -= dy;
    }

    public void union(float x, float y) {
        if (x < left) left = x; if (x > right) right = x;
        if (y < top) top = y; if (y > bottom) bottom = y;
    }

    public float width() { return right - left; }
    public float height() { return bottom - top; }
    public boolean isEmpty() { return left >= right || top >= bottom; }

    public void set(float left, float top, float right, float bottom) {
        this.left = left; this.top = top; this.right = right; this.bottom = bottom;
    }

    public void offset(float dx, float dy) {
        left += dx; top += dy; right += dx; bottom += dy;
    }

    public boolean contains(float x, float y) {
        return left < right && top < bottom && x >= left && x < right && y >= top && y < bottom;
    }

    public void roundOut(Rect dst) {
        dst.set((int)Math.floor(left), (int)Math.floor(top),
                (int)Math.ceil(right), (int)Math.ceil(bottom));
    }

    @Override
    public String toString() {
        return "RectF(" + left + ", " + top + ", " + right + ", " + bottom + ")";
    }
}
