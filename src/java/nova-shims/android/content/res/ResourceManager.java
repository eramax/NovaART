package android.content.res;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourceManager {
    private static final String TAG = "ResourceManager";
    private static ResourceManager sInstance;
    private String mApkPath;
    private Map<Integer, byte[]> mLayoutCache = new HashMap<>();
    private Map<Integer, String> mResourceIdMap = new HashMap<>();

    private ResourceManager() {
    }

    public static synchronized ResourceManager getInstance() {
        if (sInstance == null) {
            sInstance = new ResourceManager();
        }
        return sInstance;
    }

    public synchronized void setApkPath(String apkPath) {
        mApkPath = apkPath;
        mLayoutCache.clear();
        mResourceIdMap.clear();
    }

    public String getApkPath() {
        return mApkPath;
    }

    public byte[] loadLayoutResource(int resourceId) {
        if (mApkPath == null) {
            System.err.println("[ResourceManager] APK path not set");
            return null;
        }

        if (mLayoutCache.containsKey(resourceId)) {
            return mLayoutCache.get(resourceId);
        }

        try (ZipFile zipFile = new ZipFile(mApkPath)) {
            String layoutName = "layout";
            String entryPath = "res/" + layoutName + "/";

            java.util.Enumeration<? extends ZipEntry> entries = zipFile.entries();
            ZipEntry layoutEntry = null;
            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();
                if (e.getName().startsWith(entryPath) && e.getName().endsWith(".xml")) {
                    layoutEntry = e;
                    break;
                }
            }

            if (layoutEntry == null) {
                System.err.println("[ResourceManager] No layouts found in APK");
                return null;
            }

            byte[] data = new byte[(int) layoutEntry.getSize()];
            try (java.io.InputStream is = zipFile.getInputStream(layoutEntry)) {
                is.read(data);
            }
            mLayoutCache.put(resourceId, data);
            return data;
        } catch (Exception e) {
            System.err.println("[ResourceManager] Failed to load layout: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String dumpLayoutWithAapt(int resourceId) {
        if (mApkPath == null) {
            return null;
        }

        try {
            String[] cmd = {"/usr/bin/aapt", "dump", "xmltree", mApkPath, "res/layout/activity_main.xml"};
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectError(ProcessBuilder.Redirect.PIPE);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString();
            }
        } catch (Exception e) {
            System.err.println("[ResourceManager] Failed to dump layout with aapt: " + e.getMessage());
        }
        return null;
    }
}
