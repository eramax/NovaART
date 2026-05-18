package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;

public class Point implements Parcelable {

    public int x;
    public int y;

    public Point() {}
    public Point(int x, int y) { this.x = x; this.y = y; }
    public Point(Point src) { set(src.x, src.y); }

    public void set(int x, int y) { this.x = x; this.y = y; }
    public boolean equals(int x, int y) { return this.x == x && this.y == y; }
    public void negate() { x = -x; y = -y; }
    public void offset(int dx, int dy) { x += dx; y += dy; }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Point)) return false;
        Point p = (Point) obj; return p.x == x && p.y == y;
    }
    @Override public int hashCode() { return x * 31 + y; }
    @Override public String toString() { return "Point(" + x + ", " + y + ")"; }

    @Override public int describeContents() { return 0; }
    @Override public void writeToParcel(Parcel out, int flags) { out.writeInt(x); out.writeInt(y); }
    public void readFromParcel(Parcel in) { x = in.readInt(); y = in.readInt(); }
    public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>() {
        @Override public Point createFromParcel(Parcel in) { return new Point(in.readInt(), in.readInt()); }
        @Override public Point[] newArray(int size) { return new Point[size]; }
    };
}
