package android.view;

import android.view.IWindow;
import android.view.InputChannel;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.view.WindowRelayoutResult;

/** Minimal NovaART window session shape for local in-process dispatch. */
interface IWindowSession {
    int addToDisplay(IWindow window, in WindowManager.LayoutParams attrs,
            int viewVisibility, int layerStackId, int requestedVisibleTypes,
            out InputChannel outInputChannel, out WindowRelayoutResult result);

    void remove(IBinder clientToken);

    int relayout(IWindow window, in WindowManager.LayoutParams attrs,
            int requestedWidth, int requestedHeight, int viewVisibility,
            int flags, int seq, int lastSyncSeqId,
            out WindowRelayoutResult outRelayoutResult, out SurfaceControl outSurface);

    oneway void finishDrawing(IWindow window,
            in SurfaceControl.Transaction postDrawTransaction, int seqId);
}
