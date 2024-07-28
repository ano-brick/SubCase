#include <jni.h>
#include "libcore.h"
#include "trace.h"

JNIEXPORT void JNICALL
Java_ano_subcase_NativeBridge_nativeInit(JNIEnv *env, jobject thiz, jstring home) {
    TRACE_METHOD();

    const char *homeDir = (*env)->GetStringUTFChars(env, home, 0);
    coreInit(homeDir);

    (*env)->ReleaseStringUTFChars(env, home, homeDir);
}

JNIEXPORT void JNICALL
Java_ano_subcase_NativeBridge_nativeStartFrontend(JNIEnv *env, jobject thiz, jint port,
                                                     jboolean allowLan) {
    TRACE_METHOD();

    coreStartFrontend(port, allowLan);
}

JNIEXPORT void JNICALL
Java_ano_subcase_NativeBridge_nativeStopFrontend(JNIEnv *env, jobject thiz) {
    TRACE_METHOD();

    coreStopFrontend();
}

JNIEXPORT void JNICALL
Java_ano_subcase_NativeBridge_nativeStartBackend(JNIEnv *env, jobject thiz, jint port,
                                                     jboolean allowLan) {
    TRACE_METHOD();

    coreStartBackend(port, allowLan);
}

JNIEXPORT void JNICALL
Java_ano_subcase_NativeBridge_nativeStopBackend(JNIEnv *env, jobject thiz) {
    TRACE_METHOD();

    coreStopBackend();
}

JNIEXPORT void JNICALL
Java_ano_subcase_NativeBridge_nativeForceGc(JNIEnv *env, jobject thiz) {
    TRACE_METHOD();

    coreForceGC();
}