package android.os;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Nova in-process ServiceManager.
 *
 * Replaces the real ServiceManager which talks to /dev/binder via a kernel
 * context manager.  Here every service is a plain Java object in the same JVM.
 * Service stubs are registered at Nova bootstrap time via addService(); the
 * framework calls getService() exactly as it would against the real kernel
 * registry, but dispatch stays entirely in-process.
 */
public final class ServiceManager {

    private static final ConcurrentHashMap<String, IBinder> sServices =
            new ConcurrentHashMap<>();

    private ServiceManager() {}

    /** Register a local service stub under the given name. */
    public static void addService(String name, IBinder service) {
        if (name != null && service != null) {
            sServices.put(name, service);
        }
    }

    /** Look up a service by name.  Returns null if not yet registered. */
    public static IBinder getService(String name) {
        return name != null ? sServices.get(name) : null;
    }

    /** Same as getService — no blocking in the in-process model. */
    public static IBinder waitForService(String name) {
        return getService(name);
    }

    /** Same as getService — no blocking in the in-process model. */
    public static IBinder waitForDeclaredService(String name) {
        return getService(name);
    }

    /** Returns true if the named service is registered. */
    public static boolean isDeclared(String name) {
        return name != null && sServices.containsKey(name);
    }

    /** Remove a service (used in tests / cleanup). */
    public static void removeService(String name) {
        if (name != null) sServices.remove(name);
    }

    /** Returns a snapshot of all registered service names. */
    public static String[] listServices() {
        return sServices.keySet().toArray(new String[0]);
    }
}
