package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;

public final class Rect implements Parcelable {

    public int left;
    public int top;
    public int right;
    public int bottom;

    public Rect() {}
    public Rect(int left, int top, int right, int bottom) {
        this.left = left; this.top = top; this.right = right; this.bottom = bottom;
    }
    public Rect(Rect r) {
        if (r != null) { left = r.left; top = r.top; right = r.right; bottom = r.bottom; }
    }

    public boolean isEmpty() { return left >= right || top >= bottom; }
    public int width()  { return right - left; }
    public int height() { return bottom - top; }
    public int centerX() { return (left + right) >> 1; }
    public int centerY() { return (top + bottom) >> 1; }
    public float exactCenterX() { return (left + right) * 0.5f; }
    public float exactCenterY() { return (top + bottom) * 0.5f; }

    public void set(int left, int top, int right, int bottom) {
        this.left = left; this.top = top; this.right = right; this.bottom = bottom;
    }
    public void set(Rect src) { left = src.left; top = src.top; right = src.right; bottom = src.bottom; }
    public void setEmpty() { left = top = right = bottom = 0; }
    public void offset(int dx, int dy) { left += dx; top += dy; right += dx; bottom += dy; }
    public void offsetTo(int newLeft, int newTop) {
        right += newLeft - left; bottom += newTop - top; left = newLeft; top = newTop;
    }
    public void inset(int dx, int dy) { left += dx; top += dy; right -= dx; bottom -= dy; }
    public void inset(Rect insets) { left += insets.left; top += insets.top; right -= insets.right; bottom -= insets.bottom; }
    public void sort() {
        if (left > right) { int tmp = left; left = right; right = tmp; }
        if (top > bottom) { int tmp = top; top = bottom; bottom = tmp; }
    }

    public boolean contains(int x, int y) { return left < right && top < bottom && x >= left && x < right && y >= top && y < bottom; }
    public boolean contains(int left, int top, int right, int bottom) {
        return this.left <= left && this.top <= top && this.right >= right && this.bottom >= bottom;
    }
    public boolean contains(Rect r) { return contains(r.left, r.top, r.right, r.bottom); }

    public boolean intersects(int left, int top, int right, int bottom) {
        return this.left < right && left < this.right && this.top < bottom && top < this.bottom;
    }
    public boolean intersects(Rect r) { return intersects(r.left, r.top, r.right, r.bottom); }
    public static boolean intersects(Rect a, Rect b) { return a.intersects(b); }

    public boolean intersect(int left, int top, int right, int bottom) {
        if (this.left < right && left < this.right && this.top < bottom && top < this.bottom) {
            if (this.left < left) this.left = left;
            if (this.top  < top)  this.top  = top;
            if (this.right  > right)  this.right  = right;
            if (this.bottom > bottom) this.bottom = bottom;
            return true;
        }
        return false;
    }
    public boolean intersect(Rect r) { return intersect(r.left, r.top, r.right, r.bottom); }

    public void union(int left, int top, int right, int bottom) {
        if (left < this.left) this.left = left;
        if (top  < this.top)  this.top  = top;
        if (right  > this.right)  this.right  = right;
        if (bottom > this.bottom) this.bottom = bottom;
    }
    public void union(Rect r) { union(r.left, r.top, r.right, r.bottom); }
    public void union(int x, int y) {
        if (x < left) left = x; else if (x > right) right = x;
        if (y < top)  top  = y; else if (y > bottom) bottom = y;
    }

    public static boolean intersect(Rect dst, Rect a, Rect b) {
        if (a.left < b.right && b.left < a.right && a.top < b.bottom && b.top < a.bottom) {
            dst.left   = Math.max(a.left,   b.left);
            dst.top    = Math.max(a.top,    b.top);
            dst.right  = Math.min(a.right,  b.right);
            dst.bottom = Math.min(a.bottom, b.bottom);
            return true;
        }
        return false;
    }

    public String toShortString() { return "[" + left + "," + top + "][" + right + "," + bottom + "]"; }
    public String flattenToString() { return left + " " + top + " " + right + " " + bottom; }
    public static Rect unflattenFromString(String str) {
        if (str == null) return null;
        String[] parts = str.trim().split("\\s+");
        if (parts.length != 4) return null;
        try { return new Rect(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])); }
        catch (NumberFormatException e) { return null; }
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Rect)) return false;
        Rect r = (Rect) obj; return left == r.left && top == r.top && right == r.right && bottom == r.bottom;
    }
    @Override public int hashCode() { return ((left * 31 + top) * 31 + right) * 31 + bottom; }
    @Override public String toString() { return "Rect(" + left + ", " + top + " - " + right + ", " + bottom + ")"; }

    @Override public int describeContents() { return 0; }
    @Override public void writeToParcel(Parcel out, int flags) {
        out.writeInt(left); out.writeInt(top); out.writeInt(right); out.writeInt(bottom);
    }
    public void readFromParcel(Parcel in) { left = in.readInt(); top = in.readInt(); right = in.readInt(); bottom = in.readInt(); }

    public static final Parcelable.Creator<Rect> CREATOR = new Parcelable.Creator<Rect>() {
        @Override public Rect createFromParcel(Parcel in) {
            return new Rect(in.readInt(), in.readInt(), in.readInt(), in.readInt());
        }
        @Override public Rect[] newArray(int size) { return new Rect[size]; }
    };
}
