package android.graphics;

import android.content.res.Resources;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;

public class BitmapFactory {
    private static final String TAG = "NovaBitmapFactory";

    public static class Options {
        public boolean   inScaled       = true;
        public boolean   inMutable      = false;
        public boolean   inPremultiplied = true;
        public boolean   inDither       = false;
        public int       inSampleSize   = 1;
        public int       inDensity      = 0;
        public int       inTargetDensity = 0;
        public int       inScreenDensity = 0;
        public Bitmap.Config inPreferredConfig = Bitmap.Config.ARGB_8888;
        public Bitmap    inBitmap       = null;
        public int       outWidth       = 0;
        public int       outHeight      = 0;
        public String    outMimeType    = null;
        public boolean   inJustDecodeBounds = false;
        public boolean   inPreferQualityOverSpeed = false;
    }

    private BitmapFactory() {}

    public static Bitmap decodeStream(InputStream is) {
        return decodeStream(is, null, null);
    }

    public static Bitmap decodeStream(InputStream is, Rect outPadding, Options opts) {
        if (is == null) return null;
        try {
            byte[] data = readAllBytes(is);
            return decodeByteArray(data, 0, data.length, opts);
        } catch (IOException e) {
            Log.e(TAG, "decodeStream failed: " + e);
            return null;
        }
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
        return decodeByteArray(data, offset, length, null);
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length, Options opts) {
        if (data == null || length <= 0) return null;
        boolean justBounds = opts != null && opts.inJustDecodeBounds;
        long handle = nativeDecodeBytes(data, offset, length, justBounds);
        if (handle == 0) {
            Log.w(TAG, "nativeDecodeBytes returned 0 for " + length + " bytes");
            return null;
        }
        Bitmap bmp = Bitmap.createFromNative(handle);
        if (opts != null && bmp != null) {
            opts.outWidth  = bmp.getWidth();
            opts.outHeight = bmp.getHeight();
        }
        return justBounds ? null : bmp;
    }

    public static Bitmap decodeResource(Resources res, int id) {
        return decodeResource(res, id, null);
    }

    public static Bitmap decodeResource(Resources res, int id, Options opts) {
        if (res == null) return null;
        try {
            InputStream is = res.openRawResource(id);
            if (is == null) return null;
            return decodeStream(is, null, opts);
        } catch (Exception e) {
            Log.e(TAG, "decodeResource failed for id=" + id + ": " + e);
            return null;
        }
    }

    public static Bitmap decodeFile(String pathName) {
        return decodeFile(pathName, null);
    }

    public static Bitmap decodeFile(String pathName, Options opts) {
        if (pathName == null) return null;
        try (java.io.FileInputStream fis = new java.io.FileInputStream(pathName)) {
            return decodeStream(fis, null, opts);
        } catch (IOException e) {
            Log.e(TAG, "decodeFile failed: " + pathName + ": " + e);
            return null;
        }
    }

    private static byte[] readAllBytes(InputStream is) throws IOException {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int n;
        while ((n = is.read(chunk)) != -1) buf.write(chunk, 0, n);
        return buf.toByteArray();
    }

    private static native long nativeDecodeBytes(byte[] data, int offset, int length, boolean justBounds);
}
