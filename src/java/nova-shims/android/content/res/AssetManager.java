package android.content.res;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AssetManager {
    private static final String TAG = "NovaAssetManager";
    private String mApkPath;

    public AssetManager() {}

    public void setApkPath(String apkPath) {
        mApkPath = apkPath;
    }

    public InputStream open(String fileName) throws IOException {
        return open(fileName, ACCESS_STREAMING);
    }

    public InputStream open(String fileName, int accessMode) throws IOException {
        String apkPath = mApkPath;
        if (apkPath == null) {
            apkPath = ResourceManager.getInstance().getApkPath();
        }
        if (apkPath == null) {
            throw new IOException("No APK path set for AssetManager");
        }

        ZipFile zip = new ZipFile(apkPath);
        ZipEntry entry = zip.getEntry("assets/" + fileName);
        if (entry == null) {
            entry = zip.getEntry(fileName);
        }
        if (entry == null) {
            zip.close();
            throw new IOException("Asset not found: " + fileName);
        }

        final ZipFile zipRef = zip;
        final InputStream rawStream = zip.getInputStream(entry);
        return new java.io.FilterInputStream(rawStream) {
            @Override
            public void close() throws IOException {
                super.close();
                zipRef.close();
            }
        };
    }

    public String[] list(String path) throws IOException {
        String apkPath = mApkPath;
        if (apkPath == null) apkPath = ResourceManager.getInstance().getApkPath();
        if (apkPath == null) return new String[0];

        String prefix = "assets/";
        if (path != null && !path.isEmpty()) prefix = "assets/" + path + "/";
        java.util.ArrayList<String> result = new java.util.ArrayList<>();
        java.util.HashSet<String> seen = new java.util.HashSet<>();

        try (ZipFile zip = new ZipFile(apkPath)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();
                String name = e.getName();
                if (name.startsWith(prefix)) {
                    String relative = name.substring(prefix.length());
                    int slash = relative.indexOf('/');
                    String item = slash > 0 ? relative.substring(0, slash) : relative;
                    if (!item.isEmpty() && seen.add(item)) result.add(item);
                }
            }
        }
        return result.toArray(new String[0]);
    }

    public AssetFileDescriptor openFd(String fileName) throws IOException {
        String apkPath = mApkPath;
        if (apkPath == null) apkPath = ResourceManager.getInstance().getApkPath();
        if (apkPath == null) throw new IOException("No APK path for openFd: " + fileName);

        try (ZipFile zip = new ZipFile(apkPath)) {
            ZipEntry entry = zip.getEntry("assets/" + fileName);
            if (entry == null) entry = zip.getEntry(fileName);
            if (entry == null) throw new IOException("Asset not found: " + fileName);

            java.io.File tmp = java.io.File.createTempFile("nova_asset_", null);
            tmp.deleteOnExit();
            try (java.io.InputStream in = zip.getInputStream(entry);
                 java.io.FileOutputStream out = new java.io.FileOutputStream(tmp)) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
            }
            java.io.FileInputStream fis = new java.io.FileInputStream(tmp);
            return new AssetFileDescriptor(fis, 0, tmp.length());
        }
    }

    public void close() {}

    public static final int ACCESS_UNKNOWN    = 0;
    public static final int ACCESS_RANDOM     = 1;
    public static final int ACCESS_STREAMING  = 2;
    public static final int ACCESS_BUFFER     = 3;
}
