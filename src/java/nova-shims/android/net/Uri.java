package android.net;

public abstract class Uri {
    public static final Uri EMPTY = new Uri() {
        @Override public String toString() { return ""; }
        @Override public String getScheme() { return null; }
        @Override public String getPath() { return ""; }
    };

    public abstract String getScheme();
    public abstract String getPath();

    public static Uri parse(String uriString) {
        final String s = uriString;
        return new Uri() {
            @Override public String toString() { return s; }
            @Override public String getScheme() {
                int i = s.indexOf(':');
                return i > 0 ? s.substring(0, i) : null;
            }
            @Override public String getPath() {
                int q = s.indexOf('?');
                return q > 0 ? s.substring(0, q) : s;
            }
        };
    }

    public static Uri fromFile(java.io.File file) {
        return parse("file://" + file.getAbsolutePath());
    }

    public String getLastPathSegment() {
        String p = getPath();
        if (p == null) return null;
        int i = p.lastIndexOf('/');
        return i >= 0 ? p.substring(i + 1) : p;
    }
}
