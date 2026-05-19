# **Nova — Comprehensive Plan v5**

### **Service Daemon · Android 16 SDK · Native IPC · Multi-App Ecosystem**

## **1\. Executive Summary and Service Architecture**

Nova is a Linux-native Android application runtime designed to operate without the overhead of containers, virtual machines, or complete Android system images. To successfully execute multiple applications, background services, and framework components simultaneously, Nova is architected as a background Linux service daemon—conceptually similar to ollama, k3s, or the Docker daemon.  
Instead of isolating each APK in a standalone vacuum, the nova-daemon runs natively on the host Linux machine, fulfilling the role of the Android SystemServer. This daemon initializes the core Android framework, manages the application lifecycle, and routes system resources. Android applications are launched as lightweight child processes connected to the daemon, allowing full interoperability, shared memory, and cross-process intents.

### **1.1 Binder IPC via Unix Domain Sockets**

Traditional Android architectures rely heavily on the custom binder Linux kernel module for Inter-Process Communication (IPC). Relying on this module introduces severe distribution fragmentation on desktop Linux.  
To achieve maximum compatibility with minimal complexity, Nova utilizes AOSP's native libbinder\_rpc implementation. This modern Android capability allows standard Binder IPC to be bootstrapped and routed entirely over standard Unix Domain Sockets. The nova-daemon acts as the RpcServer, and child APK processes connect as clients. This enables the unmodified AOSP Java framework to execute flawlessly, communicating over standard Binder APIs while remaining entirely unaware that the underlying transport layer utilizes local sockets rather than a kernel module.

## **2\. AOSP Soong Integration & Future-Proofing (Android 16\)**

Nova is strictly built as a first-class AOSP product to ensure seamless compatibility with future Android releases. Nova lives entirely within a vendor/nova/ overlay. AOSP source code is never forked.  
By leveraging the Soong build system (Android.bp), Nova seamlessly overrides target framework components at compilation time. When updating to Android 16 (API Level 36\) and beyond, Nova will continuously inherit upstream framework improvements without requiring massive patch rebasing.

### **2.1 Host-Driven Validation via Ravenwood**

To ensure Nova's framework modifications remain strictly compliant with Android 16 standards, the project adopts AOSP's new **Ravenwood** testing environment. Ravenwood is an officially-supported, lightweight unit testing environment for Android platform code that executes directly on the host machine.  
Instead of requiring an emulator or physical device for validation, Nova utilizes the atest \--host command. This executes frameworks/base core tests and native GoogleTests natively on the Linux host. This integration guarantees that Nova's Wayland, PipeWire, and Binder-over-Socket implementations comply perfectly with the Android Compatibility Test Suite (CTS) logic.

## **3\. Multi-App Ecosystem: microG & WebView**

Modern Android applications demand a rich ecosystem to function. By operating as a system daemon, Nova natively supports background services and web rendering components.

### **3.1 Google Play Services via microG**

Applications heavily rely on Google Play Services for push notifications, location services, and DRM. Nova supports the installation of **microG** (an open-source reimplementation of Play Services) as a background daemon process.  
To allow microG to function securely on Android 16 without modifying the core OS signature verification routines globally, Nova implements a targeted Signature Spoofing patch. We define a new privileged permission, FAKE\_PACKAGE\_SIGNATURE, within frameworks/base/core/res/AndroidManifest.xml. This permission is strictly restricted to system-privileged apps (specifically com.android.vending and com.google.android.gms signed with microG's release keys). This allows microG to seamlessly impersonate official Google services to applications over the libbinder\_rpc socket without compromising the overall security model of the runtime.

### **3.2 Chromium / CEF WebView Provider**

Virtually all modern apps and AAA games rely on android.webkit.WebView for authentication (OAuth) and web content rendering. Developing a custom shim for this is prohibitively complex and error-prone.  
Nova leverages Android 16's built-in provider mechanisms. By modifying the frameworks/base/core/res/res/xml/config\_webview\_packages.xml file within the Nova build overlay, we whitelist a standalone Chromium, Bromite, or CEF WebView APK. The framework natively parses this XML file via the WebViewUpdateService to detect available WebView implementations. When an application requests a WebView, it binds to this pre-installed provider, completely unblocking complex web-based UI components using standard AOSP logic.

## **4\. ARM64 Native Execution on x86\_64 Hosts**

Executing standard Android arm64-v8a native libraries (found in AAA games and complex apps) on an x86\_64 desktop requires a highly optimized translation layer.  
Previous methodologies mistakenly targeted emulators like FEX-Emu, which are fundamentally designed to translate x86 code to run on ARM64 hardware.1 For Nova's use case (ARM64 Android apps running on an x86\_64 Linux host), the runtime will utilize Intel's libhoudini or Google's libndk\_translation.2  
Through Bionic linker namespace manipulation (ld.config.txt), Nova intercepts dlopen calls for AArch64 ELF binaries. These are transparently routed through the dynamic binary translator, executing the ARM64 instructions as optimized x86\_64 machine code.2 This achieves near-native CPU execution speeds for demanding workloads without adding custom emulation maintenance complexity to the Nova codebase.

## **5\. Graphics, UI, and Multimedia**

### **5.1 Zero-Copy Graphics Pipeline**

Nova utilizes the gfxstream pipeline to serialize and forward Vulkan and OpenGL ES guest calls to the host Linux GPU.3 Because Nova runs natively on the host, gfxstream operates in a highly efficient "single-process" IPC mode, communicating directly with the underlying Linux drivers via direct C function calls rather than virtualized ring buffers.3  
To composite these frames onto the Wayland desktop, Nova implements a custom Android gralloc module. In Android 16, this module avoids deprecated ION heaps and allocates physical memory directly from Linux dma\_buf heaps.4 Applications render directly into these dma\_buf file descriptors, which Nova then passes to the Wayland compositor via the zwp\_linux\_dmabuf\_v1 protocol. This achieves zero-copy, 60+ FPS hardware acceleration.

### **5.2 Audio Routing**

Nova overrides standard Android audio boundaries (such as OpenSL ES and AAudio) by linking them directly to a shared libnova\_audio.so library. This library initializes a PipeWire asynchronous stream (pw\_stream), precisely mapping Android's internal buffer quantum to the Linux PipeWire server to guarantee low-latency, glitch-free audio playback and recording.

## **6\. Development Milestones & Execution Phases**

This roadmap structures the development into concrete, highly testable phases, heavily relying on the Android 16 toolchain.

* **Phase 1 — Daemon Foundation:**  
  * Develop the nova-daemon service.  
  * Wire libbinder\_rpc over Unix Domain Sockets.  
  * Ensure the Android 16 framework initializes via atest \--host using Ravenwood.  
* **Phase 2 — Graphics & Input:**  
  * Implement the dma\_buf backed gralloc.4  
  * Integrate gfxstream 3 bridging to Wayland.  
  * Map Wayland input events to Android MotionEvent dispatchers.  
* **Phase 3 — Ecosystem Bootstrapping:**  
  * Modify config\_webview\_packages.xml to support standard Chromium WebView.  
  * Integrate the FAKE\_PACKAGE\_SIGNATURE patch and deploy microG as a background service.  
* **Phase 4 — ARM64 Translation:**  
  * Integrate libhoudini or libndk\_translation 2 via the Bionic linker config to unblock native AAA games compiled for arm64-v8a.  
* **Phase 5 — Media & Polish:**  
  * Bridge MediaCodec to VA-API for hardware-accelerated video decoding.  
  * Finalize PipeWire audio mapping.

#### **Works cited**

1. FEX-Emu: Run x86 and x86-64 Apps on ARM64 Linux Devices \- OSTechNix, accessed May 19, 2026, [https://ostechnix.com/fex-emu-run-x86-and-x86-64-apps-on-arm64-linux-devices/](https://ostechnix.com/fex-emu-run-x86-and-x86-64-apps-on-arm64-linux-devices/)  
2. Let the x86 android emulator simulate the arm architecture system \- EEWorld, accessed May 19, 2026, [https://en.eeworld.com.cn/news/mcu/eic307554.html](https://en.eeworld.com.cn/news/mcu/eic307554.html)  
3. google/gfxstream \- GitHub, accessed May 19, 2026, [https://github.com/google/gfxstream](https://github.com/google/gfxstream)  
4. Transition from ION to DMA-BUF heaps (5.4 kernel only) | Android Open Source Project, accessed May 19, 2026, [https://source.android.com/docs/core/architecture/kernel/dma-buf-heaps](https://source.android.com/docs/core/architecture/kernel/dma-buf-heaps)