#include <jni.h>
#include <string>
#include <iostream>
extern "C"
jstring
Java_com_example_micah_cpsc3780_1ndk_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Please enter your username: ";
    return env->NewStringUTF(hello.c_str());
}