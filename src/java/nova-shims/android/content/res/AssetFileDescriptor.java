package android.content.res;

import java.io.FileDescriptor;
import java.io.IOException;

public class AssetFileDescriptor implements java.io.Closeable {
    private final java.io.FileInputStream mStream;
    private final long mStartOffset;
    private final long mLength;

    public AssetFileDescriptor(java.io.FileInputStream stream, long startOffset, long length) {
        mStream = stream;
        mStartOffset = startOffset;
        mLength = length;
    }

    public FileDescriptor getFileDescriptor() {
        try {
            return mStream != null ? mStream.getFD() : null;
        } catch (IOException e) {
            return null;
        }
    }

    public long getStartOffset() { return mStartOffset; }
    public long getLength() { return mLength; }
    public long getDeclaredLength() { return mLength; }

    public java.io.FileInputStream createInputStream() throws IOException {
        return mStream;
    }

    @Override
    public void close() throws IOException {
        if (mStream != null) mStream.close();
    }
}
