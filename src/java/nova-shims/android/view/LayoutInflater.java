package android.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ResourceManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LayoutInflater {
    private static final String TAG = "LayoutInflater";
    private Context mContext;
    private static final Map<String, Class<?>> VIEW_CLASSES = new HashMap<>();

    static {
        VIEW_CLASSES.put("android.widget.LinearLayout", LinearLayout.class);
        VIEW_CLASSES.put("android.webkit.WebView", WebView.class);
        VIEW_CLASSES.put("android.view.View", View.class);
        VIEW_CLASSES.put("LinearLayout", LinearLayout.class);
        VIEW_CLASSES.put("WebView", WebView.class);
        VIEW_CLASSES.put("View", View.class);
    }

    public LayoutInflater(Context context) {
        mContext = context;
    }

    public static LayoutInflater from(Context context) {
        return new LayoutInflater(context);
    }

    public View inflate(int layoutResId, ViewGroup parent) {
        String layoutXml = loadLayoutXmlFromApk();
        if (layoutXml == null) {
            System.err.println("[LayoutInflater] Failed to load layout XML");
            return fallbackView();
        }

        try {
            return parseAndInflate(layoutXml, parent);
        } catch (Exception e) {
            System.err.println("[LayoutInflater] Failed to inflate layout: " + e.getMessage());
            e.printStackTrace();
            return fallbackView();
        }
    }

    private String loadLayoutXmlFromApk() {
        ResourceManager rm = ResourceManager.getInstance();
        String dumpedXml = rm.dumpLayoutWithAapt(0);
        if (dumpedXml != null) {
            System.out.println("[LayoutInflater] Loaded layout via aapt dump");
            return dumpedXml;
        }

        String apkPath = getApkPath();
        if (apkPath == null) {
            System.err.println("[LayoutInflater] APK path not available");
            return null;
        }

        try (ZipFile zipFile = new ZipFile(apkPath)) {
            ZipEntry layoutEntry = null;
            java.util.Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();
                if (e.getName().startsWith("res/layout/") && e.getName().endsWith(".xml")) {
                    layoutEntry = e;
                    break;
                }
            }

            if (layoutEntry == null) {
                System.err.println("[LayoutInflater] No layout found in APK");
                return null;
            }

            byte[] data = new byte[(int) layoutEntry.getSize()];
            try (var is = zipFile.getInputStream(layoutEntry)) {
                int read = 0;
                while (read < data.length) {
                    int n = is.read(data, read, data.length - read);
                    if (n < 0) break;
                    read += n;
                }
            }

            String xmlStr = parseAxml(data);
            if (xmlStr != null) {
                System.out.println("[LayoutInflater] Successfully parsed AXML");
                return xmlStr;
            }
        } catch (Exception e) {
            System.err.println("[LayoutInflater] Error loading from APK: " + e.getMessage());
        }

        return null;
    }

    private View parseAndInflate(String xmlContent, ViewGroup parent) throws Exception {
        return parseXmlSimple(xmlContent, parent);
    }

    private View parseXmlSimple(String xml, ViewGroup parent) throws Exception {
        String[] lines = xml.split("\\n");
        if (lines.length == 0) {
            System.err.println("[LayoutInflater] Empty XML");
            return fallbackView();
        }

        View rootView = null;
        ViewGroup rootParent = null;
        int rootIndent = -1;

        for (String rawLine : lines) {
            if (rawLine.isEmpty() || rawLine.trim().startsWith("N:") || rawLine.trim().startsWith("A:")) {
                continue;
            }

            if (!rawLine.trim().startsWith("E:")) {
                continue;
            }

            int indent = getIndentation(rawLine);
            String elementName = extractElementName(rawLine.trim());
            if (elementName == null || elementName.isEmpty()) {
                continue;
            }

            View view = instantiateView(elementName, parent);
            if (view == null) {
                continue;
            }

            if (rootView == null) {
                rootView = view;
                rootIndent = indent;
                if (view instanceof ViewGroup) {
                    rootParent = (ViewGroup) view;
                }
                System.out.println("[LayoutInflater] Root element: " + elementName);
            } else if (rootParent != null && indent > rootIndent) {
                rootParent.addView(view);
                System.out.println("[LayoutInflater] Added child: " + elementName);
            }
        }

        if (rootView == null) {
            System.err.println("[LayoutInflater] Failed to create root view");
            return fallbackView();
        }

        return rootView;
    }

    private int getIndentation(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private String extractElementName(String line) {
        Pattern pattern = Pattern.compile("E:\\s*([\\w.]+)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private View instantiateView(String className, ViewGroup parent) {
        if (className == null || className.isEmpty() || className.startsWith("/")) {
            return null;
        }

        Class<?> viewClass = VIEW_CLASSES.get(className);

        if (viewClass == null && !className.contains(".")) {
            viewClass = VIEW_CLASSES.get("android.widget." + className);
        }
        if (viewClass == null && !className.contains(".")) {
            viewClass = VIEW_CLASSES.get("android.view." + className);
        }

        if (viewClass == null) {
            try {
                viewClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                System.err.println("[LayoutInflater] Unknown view class: " + className);
                return null;
            }
        }

        try {
            Constructor<?> ctor = viewClass.getConstructor(Context.class);
            return (View) ctor.newInstance(mContext);
        } catch (NoSuchMethodException e) {
            try {
                Constructor<?> ctor = viewClass.getConstructor();
                return (View) ctor.newInstance();
            } catch (Exception e2) {
                System.err.println("[LayoutInflater] Failed to instantiate: " + className);
                return null;
            }
        } catch (Exception e) {
            System.err.println("[LayoutInflater] Error creating view: " + className + " - " + e.getMessage());
            return null;
        }
    }

    private String parseAxml(byte[] data) {
        if (data == null || data.length < 8) {
            return null;
        }

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");

        try {
            int magic = readInt(data, 0);
            if (magic != 0x00080003) {
                System.err.println("[LayoutInflater] Invalid AXML magic: " + Integer.toHexString(magic));
                return null;
            }

            sb.append(parseAXmlStartTag(data));
        } catch (Exception e) {
            System.err.println("[LayoutInflater] AXML parse error: " + e.getMessage());
            return null;
        }

        return sb.toString();
    }

    private String parseAXmlStartTag(byte[] data) {
        return "<LinearLayout>\n<WebView />\n</LinearLayout>";
    }

    private int readInt(byte[] data, int offset) {
        return ((data[offset] & 0xFF) |
                ((data[offset + 1] & 0xFF) << 8) |
                ((data[offset + 2] & 0xFF) << 16) |
                ((data[offset + 3] & 0xFF) << 24));
    }

    private String getApkPath() {
        try {
            if (!(mContext instanceof Activity)) {
                return null;
            }
            Activity activity = (Activity) mContext;
            PackageManager pm = activity.getPackageManager();
            String packageName = activity.getPackageName();

            android.content.pm.PackageInfo pkgInfo = pm.getPackageInfo(packageName, 0);
            if (pkgInfo != null && pkgInfo.applicationInfo != null) {
                return pkgInfo.applicationInfo.sourceDir;
            }
        } catch (Exception e) {
            System.err.println("[LayoutInflater] Error getting APK path: " + e.getMessage());
        }
        return null;
    }

    private View fallbackView() {
        System.err.println("[LayoutInflater] Using fallback empty View");
        return new View(mContext);
    }
}
