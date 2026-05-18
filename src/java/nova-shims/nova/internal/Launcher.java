package nova.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class Launcher {
    private static final String[] HOST_SUPPORT_LIBRARIES = {
            "libandroid.so",
            "liblog.so",
            "libgles3jni.so"
    };
    private static final String[][] HOST_COMPATIBILITY_LIBRARIES = {
            { "libc.so", "/lib/x86_64-linux-gnu/libc.so.6" },
            { "libm.so", "/lib/x86_64-linux-gnu/libm.so.6" },
            { "libdl.so", "/lib/x86_64-linux-gnu/libdl.so.2" }
    };
    private static final String[] GLESV2_CANDIDATES = {
            "/lib/x86_64-linux-gnu/libGLESv2.so.2",
            "/lib/x86_64-linux-gnu/libGLESv2.so",
            "/usr/lib/x86_64-linux-gnu/libGLESv2.so.2",
            "/usr/lib/x86_64-linux-gnu/libGLESv2.so",
            "/lib64/libGLESv2.so.2",
            "/lib64/libGLESv2.so",
            "/usr/lib64/libGLESv2.so.2",
            "/usr/lib64/libGLESv2.so"
    };

    private Launcher() {}

    public static void launch(String apkPath, String activityClass) throws Exception {
        String androidData = System.getenv("ANDROID_DATA");
        File optimizedDir = new File(androidData == null ? "." : androidData, "dex");
        if (!optimizedDir.exists() && !optimizedDir.mkdirs()) {
            throw new IllegalStateException("failed to create dex dir: " + optimizedDir);
        }
        File nativeLibDir = new File(optimizedDir, "native-libs");
        extractNativeLibraries(apkPath, nativeLibDir, "x86_64");

        System.out.println("[NovaLauncher] APK=" + apkPath);
        System.out.println("[NovaLauncher] Activity=" + activityClass);
        System.out.println("[NovaLauncher] OptimizedDir=" + optimizedDir.getAbsolutePath());
        System.out.println("[NovaLauncher] NativeLibDir=" + nativeLibDir.getAbsolutePath());

        ClassLoader parent = Launcher.class.getClassLoader();
        ClassLoader loader = createDexClassLoader(
                apkPath, optimizedDir.getAbsolutePath(), nativeLibDir.getAbsolutePath(), parent);
        Thread.currentThread().setContextClassLoader(loader);

        System.out.println("[NovaLauncher] Loader=" + loader.getClass().getName());
        logClass("android.app.Activity", loader);
        logClass("android.view.View", loader);
        logClass("android.view.SurfaceView", loader);
        logClass("android.opengl.GLSurfaceView", loader);
        logClass("com.android.gles3jni.GLES3JNILib", loader);

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
            logGlThreadState(contentView);
            tryInvoke(contentView, "novaAttachToWindow", new Class<?>[0], new Object[0]);
            tryInvoke(contentView, "novaSimulateSurfaceLifecycle",
                    new Class<?>[] { int.class, int.class },
                    new Object[] { 960, 540 });
            tryInvoke(contentView, "requestRender", new Class<?>[0], new Object[0]);
            logGlThreadState(contentView);
        }
    }

    private static ClassLoader createDexClassLoader(
            String apkPath, String optimizedDir, String nativeLibDir, ClassLoader parent)
            throws ReflectiveOperationException {
        Class<?> dexClassLoader = Class.forName("dalvik.system.DexClassLoader");
        Constructor<?> ctor = dexClassLoader.getConstructor(
                String.class, String.class, String.class, ClassLoader.class);
        return (ClassLoader) ctor.newInstance(apkPath, optimizedDir, nativeLibDir, parent);
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

    private static void logClass(String className, ClassLoader loader) {
        try {
            Class<?> type = Class.forName(className, false, loader);
            System.out.println("[NovaLauncher] Class " + className
                    + " loader=" + String.valueOf(type.getClassLoader()));
        } catch (ClassNotFoundException e) {
            System.out.println("[NovaLauncher] Missing class " + className);
        }
    }

    private static void logGlThreadState(Object contentView) {
        try {
            Class<?> glSurfaceView = Class.forName("android.opengl.GLSurfaceView");
            if (!glSurfaceView.isInstance(contentView)) {
                return;
            }
            Field field = glSurfaceView.getDeclaredField("mGLThread");
            field.setAccessible(true);
            Object glThread = field.get(contentView);
            if (glThread instanceof Thread) {
                Thread thread = (Thread) glThread;
                System.out.println("[NovaLauncher] GLThread name=" + thread.getName()
                        + " alive=" + thread.isAlive()
                        + " state=" + thread.getState());
            } else {
                System.out.println("[NovaLauncher] GLThread=" + String.valueOf(glThread));
            }
        } catch (ReflectiveOperationException e) {
            System.out.println("[NovaLauncher] Failed to inspect GLThread: " + e);
        }
    }

    private static void extractNativeLibraries(String apkPath, File outputDir, String abi) throws IOException {
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("failed to create native lib dir: " + outputDir);
        }

        try (ZipFile zip = new ZipFile(apkPath)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            String prefix = "lib/" + abi + "/";
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().startsWith(prefix) || !entry.getName().endsWith(".so")) {
                    continue;
                }
                File outFile = new File(outputDir, entry.getName().substring(prefix.length()));
                try (InputStream in = zip.getInputStream(entry);
                     FileOutputStream out = new FileOutputStream(outFile)) {
                    in.transferTo(out);
                }
                System.out.println("[NovaLauncher] Extracted " + outFile.getName());
            }
        }

        ensureHostSupportLibraries(outputDir);
        ensureGlesCompatibilityLibrary(outputDir);
    }

    private static void ensureHostSupportLibraries(File outputDir) throws IOException {
        File optimizedDir = outputDir.getParentFile();
        if (optimizedDir == null) {
            return;
        }
        File androidDataDir = optimizedDir.getParentFile();
        if (androidDataDir == null) {
            return;
        }
        File outputRoot = androidDataDir.getParentFile();
        if (outputRoot == null) {
            return;
        }

        File hostLibDir = new File(outputRoot, "lib");
        for (String libraryName : HOST_SUPPORT_LIBRARIES) {
            File source = new File(hostLibDir, libraryName);
            if (!source.isFile()) {
                continue;
            }
            File dest = new File(outputDir, libraryName);
            if ("libgles3jni.so".equals(libraryName)) {
                Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[NovaLauncher] Replaced libgles3jni.so with host build from " + source);
                continue;
            }
            if (dest.exists()) {
                continue;
            }
            copyOrLink(source.toPath(), dest.toPath(), "[NovaLauncher] Staged " + libraryName + " from ");
        }

        for (String[] compatibilityLibrary : HOST_COMPATIBILITY_LIBRARIES) {
            File dest = new File(outputDir, compatibilityLibrary[0]);
            if (dest.exists()) {
                continue;
            }
            Path source = Path.of(compatibilityLibrary[1]);
            if (!Files.isRegularFile(source)) {
                continue;
            }
            copyOrLink(source, dest.toPath(), "[NovaLauncher] Staged " + compatibilityLibrary[0] + " from ");
        }
    }

    private static void ensureGlesCompatibilityLibrary(File outputDir) throws IOException {
        File compatLib = new File(outputDir, "libGLESv3.so");
        if (compatLib.exists()) {
            return;
        }

        Path systemLib = findSystemGlesLibrary();
        if (systemLib == null) {
            System.out.println("[NovaLauncher] WARNING: no system libGLESv2 candidate found for libGLESv3.so");
            return;
        }

        copyOrLink(systemLib, compatLib.toPath(), "[NovaLauncher] Staged libGLESv3.so from ");
    }

    private static Path findSystemGlesLibrary() {
        for (String candidate : GLESV2_CANDIDATES) {
            Path path = Path.of(candidate);
            if (Files.isRegularFile(path)) {
                return path;
            }
        }
        return null;
    }

    private static void copyOrLink(Path source, Path dest, String messagePrefix) throws IOException {
        Files.deleteIfExists(dest);
        try {
            Files.createSymbolicLink(dest, source);
            System.out.println(messagePrefix + source);
        } catch (UnsupportedOperationException | SecurityException e) {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(messagePrefix + source);
        }
    }
}
