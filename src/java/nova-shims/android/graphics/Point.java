package android.graphics;

public class Point {
    public int x;
    public int y;

    public Point() {}
    public Point(int x, int y) { this.x = x; this.y = y; }

    public void set(int x, int y) { this.x = x; this.y = y; }
    public boolean equals(int x, int y) { return this.x == x && this.y == y; }

    @Override
    public String toString() { return "Point(" + x + ", " + y + ")"; }
}
