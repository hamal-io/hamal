#include "kua_jni_error.h"
#include "kua_jni.h"
#include "kua_state.h"

int
throw_illegal_argument(char const *message) {
    JNIEnv *env = current_env();
    return (*env)->ThrowNew(env, jni_ref().illegal_state_exception_class, message);
}

int
throw_illegal_state(char const *message) {
    JNIEnv *env = current_env();
    return (*env)->ThrowNew(env, jni_ref().illegal_state_exception_class, message);
}

int
throw_error(char const *message) {
    JNIEnv *env = current_env();
    return (*env)->ThrowNew(env, jni_ref().error_class, message);
}