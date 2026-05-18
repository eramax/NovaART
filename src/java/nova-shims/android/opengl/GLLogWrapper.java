package android.opengl;

import java.io.Writer;
import javax.microedition.khronos.opengles.GL;

final class GLLogWrapper implements GL {
    private final GL mDelegate;
    @SuppressWarnings("unused")
    private final Writer mLog;
    @SuppressWarnings("unused")
    private final boolean mLogArgumentNames;

    GLLogWrapper(GL delegate, Writer log, boolean logArgumentNames) {
        mDelegate = delegate;
        mLog = log;
        mLogArgumentNames = logArgumentNames;
    }

    GL unwrap() {
        return mDelegate;
    }
}
