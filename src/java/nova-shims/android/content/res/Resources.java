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
        return new EmptyXmlResourceParser();
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }
}
