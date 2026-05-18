package android.opengl;

import javax.microedition.khronos.opengles.GL;

final class GLErrorWrapper implements GL {
    private final GL mDelegate;
    @SuppressWarnings("unused")
    private final int mConfigFlags;

    GLErrorWrapper(GL delegate, int configFlags) {
        mDelegate = delegate;
        mConfigFlags = configFlags;
    }

    GL unwrap() {
        return mDelegate;
    }
}
