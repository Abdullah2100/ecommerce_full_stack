#include <jni.h>
#include <string.h>
#include <stdlib.h>
#include <android/log.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_eccomerce_1app_util_Secrets_getBaseUrl(
        JNIEnv* env,
        jobject /* this */
) {
    return env->NewStringUTF("http://10.0.2.2:5077/api");
//    return env->NewStringUTF("http://192.168.1.49:5077/api");
}