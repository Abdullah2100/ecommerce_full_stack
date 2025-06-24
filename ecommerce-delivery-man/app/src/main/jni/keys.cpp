#include <jni.h>
#include <android/log.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_e_1commerc_1delivery_1man_util_Secrets_getBaseUrl(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("http://10.0.2.2:5077/api");

}