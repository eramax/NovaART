package android.graphics;

public class Shader {
    public enum TileMode {
        CLAMP(0), REPEAT(1), MIRROR(2);
        final int nativeInt;
        TileMode(int n) { this.nativeInt = n; }
    }

    long mNativeInstance;

    public Shader() {}
}
