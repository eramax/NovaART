#include <stddef.h>

/*
 * Minimal host-side shim for Android NDK consumers that only need the shared
 * object to exist at load time. Add exported functions here if later APKs
 * actually reference libandroid symbols.
 */
int novaart_host_libandroid_anchor(void) {
    return 0;
}
