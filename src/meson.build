project('novaart', 'c',
  version: '0.1.0',
  default_options: [
    'c_std=c11',
    'c_args=-Wall -Wextra -Werror=implicit-function-declaration',
  ])

# Dependencies
wayland_client = dependency('wayland-client')
wayland_egl = dependency('wayland-egl')
egl = dependency('egl')
glesv2 = dependency('glesv2')
xkbcommon = dependency('xkbcommon')
dl = meson.get_compiler('c').find_library('dl')
m = meson.get_compiler('c').find_library('m')

# JNI headers from JDK
jni_dep = dependency('jni', required: false)
if not jni_dep.found()
  javac_bin = find_program('javac', required: true)
  java_home_cmd = run_command([find_program('bash'), '-c',
    'dirname $(dirname $(readlink -f $(which javac)))'],
    check: true)
  java_home = java_home_cmd.stdout().strip()
  jni_dep = declare_dependency(
    include_directories: include_directories(
      java_home / 'include',
      java_home / 'include/linux',
    ))
endif

# Wayland protocols — xdg-shell
wayland_protocols = dependency('wayland-protocols')
wayland_scanner = find_program('wayland-scanner')

xdg_shell_xml = wayland_protocols.get_variable(pkgconfig: 'pkgdatadir') + '/stable/xdg-shell/xdg-shell.xml'

xdg_shell_client_protocol_h = custom_target('xdg-shell-client-protocol-h',
  output: 'xdg-shell-client-protocol.h',
  input: xdg_shell_xml,
  command: [wayland_scanner, 'client-header', '@INPUT@', '@OUTPUT@'])

xdg_shell_protocol_c = custom_target('xdg-shell-protocol-c',
  output: 'xdg-shell-protocol.c',
  input: xdg_shell_xml,
  command: [wayland_scanner, 'private-code', '@INPUT@', '@OUTPUT@'])

# Subdirectory: JNI stubs
subdir('jni')

# NovaART runtime library
novaart_sources = files(
  'main.c',
  'wayland.c',
  'art.c',
  'egl.c',
)

novaart_inc = include_directories('.')

executable('novaart',
  novaart_sources,
  xdg_shell_client_protocol_h,
  xdg_shell_protocol_c,
  link_with: jni_stubs_lib,
  dependencies: [
    wayland_client,
    wayland_egl,
    egl,
    glesv2,
    xkbcommon,
    dl,
    m,
    jni_dep,
  ],
  include_directories: novaart_inc,
  install: true,
  install_dir: 'bin')
