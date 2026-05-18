package android.hardware.display;

import android.hardware.display.IDisplayManagerCallback;
import android.view.DisplayInfo;

/** Minimal NovaART display manager shape for local in-process dispatch. */
interface IDisplayManager {
    DisplayInfo getDisplayInfo(int displayId);
    void registerCallback(in IDisplayManagerCallback callback);
}
