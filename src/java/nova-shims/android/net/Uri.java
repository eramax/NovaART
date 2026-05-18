package android.net;

public abstract class Uri {
    public static final Uri EMPTY = new Uri() {
        @Override public String toString() { return ""; }
        @Override public String getScheme() { return ""; }
        @Override public String getAuthority() { return null; }
        @Override public String getPath() { return ""; }
        @Override public String getQuery() { return null; }
        @Override public String getFragment() { return null; }
    };

    public static Uri parse(String uriString) {
        final String s = uriString != null ? uriString : "";
        return new Uri() {
            @Override public String toString() { return s; }
            @Override public String getScheme() { int i = s.indexOf(':'); return i >= 0 ? s.substring(0, i) : ""; }
            @Override public String getAuthority() { return null; }
            @Override public String getPath() { return s; }
            @Override public String getQuery() { return null; }
            @Override public String getFragment() { return null; }
        };
    }

    public abstract String getScheme();
    public abstract String getAuthority();
    public abstract String getPath();
    public abstract String getQuery();
    public abstract String getFragment();

    public String getLastPathSegment() {
        String path = getPath();
        if (path == null) return null;
        int i = path.lastIndexOf('/');
        return i >= 0 ? path.substring(i + 1) : path;
    }

    public Builder buildUpon() { return new Builder(this); }

    public static class Builder {
        private String mString;
        public Builder() {}
        public Builder(Uri uri) { mString = uri != null ? uri.toString() : ""; }
        public Builder scheme(String scheme) { return this; }
        public Builder authority(String authority) { return this; }
        public Builder path(String path) { return this; }
        public Builder query(String query) { return this; }
        public Builder fragment(String fragment) { return this; }
        public Builder appendPath(String path) { return this; }
        public Builder appendQueryParameter(String key, String value) { return this; }
        public Uri build() { return Uri.parse(mString != null ? mString : ""); }
    }
}
