package android.content.res;

import java.io.FileInputStream;
import java.io.IOException;

public class AssetFileDescriptor implements java.io.Closeable {

    public static final long UNKNOWN_LENGTH = -1;

    private final FileInputStream mFis;
    private final long mStartOffset;
    private final long mLength;

    public AssetFileDescriptor(FileInputStream fis, long startOffset, long length) {
        mFis = fis;
        mStartOffset = startOffset;
        mLength = length;
    }

    public FileInputStream createInputStream() throws IOException { return mFis; }
    public java.io.FileDescriptor getFileDescriptor() {
        try { return mFis != null ? mFis.getFD() : null; }
        catch (java.io.IOException e) { return null; }
    }
    public long getStartOffset() { return mStartOffset; }
    public long getLength() { return mLength; }
    public long getDeclaredLength() { return mLength; }
    public android.os.ParcelFileDescriptor getParcelFileDescriptor() { return null; }

    @Override public void close() throws IOException { if (mFis != null) mFis.close(); }
}
