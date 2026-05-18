libpng = dependency('libpng', required: true)
glesv2_jni = dependency('glesv2')

jni_sources = files(
  'android_runtime.c',
  'core_jni_helpers.c',
  'android_os_SystemProperties.c',
  'android_os_SystemClock.c',
  'android_os_Binder.c',
  'android_os_Process.c',
  'android_view_KeyEvent.c',
  'android_view_MotionEvent.c',
  'android_graphics_Canvas.c',
  'android_graphics_Paint.c',
  'android_graphics_Bitmap.c',
  'android_graphics_BitmapFactory.c',
  'android_opengl_GLES20.c',
  'android_opengl_GLUtils.c',
  'com_android_internal_graphics_NativeUtils.c',
  'com_google_android_gles_jni_EGLImpl.c',
  'com_google_android_gles_jni_GLImpl.c',
  'nova_canvas_render.c',
)

jni_stubs_lib = static_library('novaart_jni',
  jni_sources,
  dependencies: [dl, jni_dep, libpng, glesv2_jni],
  include_directories: include_directories('..'),
  install: true)
