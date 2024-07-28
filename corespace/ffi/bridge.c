#include "bridge.h"

void log_info(char *msg) {
    __android_log_write(ANDROID_LOG_INFO, TAG, msg);

    free(msg);
}

void log_error(char *msg) {
    __android_log_write(ANDROID_LOG_ERROR, TAG, msg);

    free(msg);
}

void log_warn(char *msg) {
    __android_log_write(ANDROID_LOG_WARN, TAG, msg);

    free(msg);
}

void log_debug(char *msg) {
    __android_log_write(ANDROID_LOG_DEBUG, TAG, msg);

    free(msg);
}

void log_verbose(char *msg) {
    __android_log_write(ANDROID_LOG_VERBOSE, TAG, msg);

    free(msg);
}