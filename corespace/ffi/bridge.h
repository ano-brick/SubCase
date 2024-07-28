#include <android/log.h>
#include <malloc.h>

#define TAG "Go-Core"

typedef const char *c_string;

extern void log_info(char *msg);

extern void log_error(char *msg);

extern void log_warn(char *msg);

extern void log_debug(char *msg);

extern void log_verbose(char *msg);