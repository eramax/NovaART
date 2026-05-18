#include <stdarg.h>
#include <stdio.h>

#include "compat/android/log.h"

int __android_log_print(int prio, const char *tag, const char *fmt, ...) {
    va_list args;
    FILE *stream = prio >= ANDROID_LOG_ERROR ? stderr : stdout;

    if (tag != NULL && tag[0] != '\0') {
        fprintf(stream, "[%s] ", tag);
    }

    va_start(args, fmt);
    vfprintf(stream, fmt, args);
    va_end(args);
    fflush(stream);

    return 0;
}
