package nova.internal;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class Launcher {
    private Launcher() {}

    public static void launch(String apkPath, String activityClass) throws Exception {
        String androidData = System.getenv("ANDROID_DATA");
        File optimizedDir = new File(androidData == null ? "." : androidData, "dex");
        if (!optimizedDir.exists() && !optimizedDir.mkdirs()) {
            throw new IllegalStateException("failed to create dex dir: " + optimizedDir);
        }

        System.out.println("[NovaLauncher] APK=" + apkPath);
        System.out.println("[NovaLauncher] Activity=" + activityClass);
        System.out.println("[NovaLauncher] OptimizedDir=" + optimizedDir.getAbsolutePath());

        ClassLoader parent = Launcher.class.getClassLoader();
        ClassLoader loader = createDexClassLoader(apkPath, optimizedDir.getAbsolutePath(), parent);
        Thread.currentThread().setContextClassLoader(loader);

        System.out.println("[NovaLauncher] Loader=" + loader.getClass().getName());

        Class<?> activityType = Class.forName(activityClass, true, loader);
        System.out.println("[NovaLauncher] Loaded=" + activityType.getName());
        if (activityType.getSuperclass() != null) {
            System.out.println("[NovaLauncher] Super=" + activityType.getSuperclass().getName());
        }

        Constructor<?> ctor = activityType.getDeclaredConstructor();
        ctor.setAccessible(true);
        Object instance = ctor.newInstance();
        System.out.println("[NovaLauncher] Instance=" + instance.getClass().getName());

        invokeLifecycle(activityType, instance, "onCreate",
                new Class<?>[] { Class.forName("android.os.Bundle") },
                new Object[] { null });
        invokeLifecycle(activityType, instance, "onResume", new Class<?>[0], new Object[0]);

        Method getContentView = activityType.getMethod("getContentView");
        Object contentView = getContentView.invoke(instance);
        if (contentView != null) {
            System.out.println("[NovaLauncher] ContentView=" + contentView.getClass().getName());
            tryInvoke(contentView, "novaSimulateSurfaceLifecycle",
                    new Class<?>[] { int.class, int.class },
                    new Object[] { 960, 540 });
        }
    }

    private static ClassLoader createDexClassLoader(String apkPath, String optimizedDir, ClassLoader parent)
            throws ReflectiveOperationException {
        Class<?> dexClassLoader = Class.forName("dalvik.system.DexClassLoader");
        Constructor<?> ctor = dexClassLoader.getConstructor(
                String.class, String.class, String.class, ClassLoader.class);
        return (ClassLoader) ctor.newInstance(apkPath, optimizedDir, null, parent);
    }

    private static void invokeLifecycle(Class<?> activityClass, Object instance, String methodName,
                                        Class<?>[] parameterTypes, Object[] args) throws Exception {
        Method method = activityClass.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        System.out.println("[NovaLauncher] Calling " + methodName);
        method.invoke(instance, args);
        System.out.println("[NovaLauncher] Completed " + methodName);
    }

    private static void tryInvoke(Object target, String methodName,
                                  Class<?>[] parameterTypes, Object[] args) throws Exception {
        Method method;
        try {
            method = target.getClass().getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            System.out.println("[NovaLauncher] No method " + methodName + " on " + target.getClass().getName());
            return;
        }
        System.out.println("[NovaLauncher] Calling " + methodName);
        method.invoke(target, args);
        System.out.println("[NovaLauncher] Completed " + methodName);
    }
}
