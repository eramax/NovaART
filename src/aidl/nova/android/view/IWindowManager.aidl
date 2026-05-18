package android.view;

import android.view.IWindowSession;
import android.view.IWindowSessionCallback;

/** Minimal NovaART window manager shape for local in-process dispatch. */
interface IWindowManager {
    IWindowSession openSession(in IWindowSessionCallback callback);
}
