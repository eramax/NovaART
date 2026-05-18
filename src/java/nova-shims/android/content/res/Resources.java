package android.content.res;

import android.util.DisplayMetrics;

public class Resources {
    private static final Resources SYSTEM = new Resources();

    private final AssetManager mAssets = new AssetManager();
    private final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    private final Configuration mConfiguration = new Configuration();

    public static Resources getSystem() {
        return SYSTEM;
    }

    public AssetManager getAssets() {
        return mAssets;
    }

    public boolean getBoolean(int id) {
        return false;
    }

    public int getColor(int id) {
        return id;
    }

    public Configuration getConfiguration() {
        return mConfiguration;
    }

    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    public int getInteger(int id) {
        return 0;
    }

    public String getString(int id) {
        return Integer.toString(id);
    }

    public CharSequence getText(int id) {
        return getString(id);
    }

    public XmlResourceParser getXml(int id) {
        return null;
    }

    public java.io.InputStream openRawResource(int id) {
        return openRawResource(id, null);
    }

    public java.io.InputStream openRawResource(int id, android.util.TypedValue value) {
        String apkPath = ResourceManager.getInstance().getApkPath();
        if (apkPath == null) return null;
        try {
            java.util.zip.ZipFile zip = new java.util.zip.ZipFile(apkPath);
            java.util.Enumeration<? extends java.util.zip.ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                java.util.zip.ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    String name = entry.getName();
                    if (name.startsWith("res/raw/") || name.startsWith("res/") && name.contains(Integer.toHexString(id))) {
                        final java.util.zip.ZipFile zipRef = zip;
                        final java.io.InputStream raw = zip.getInputStream(entry);
                        return new java.io.FilterInputStream(raw) {
                            @Override public void close() throws java.io.IOException {
                                super.close(); zipRef.close();
                            }
                        };
                    }
                }
            }
            zip.close();
        } catch (java.io.IOException e) {
            android.util.Log.e("NovaResources", "openRawResource failed: " + e);
        }
        return null;
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    public static class Theme {
        public void applyStyle(int resId, boolean force) {}
        public android.content.res.TypedArray obtainStyledAttributes(int[] attrs) { return null; }
        public android.content.res.TypedArray obtainStyledAttributes(int resId, int[] attrs) { return null; }
        public android.content.res.TypedArray obtainStyledAttributes(android.util.AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) { return null; }
        public Resources getResources() { return null; }
    }
}
