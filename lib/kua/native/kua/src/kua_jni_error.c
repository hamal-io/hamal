#include "kua_jni_error.h"
#include "kua_jni.h"
#include "kua_state.h"

int
throw_illegal_argument(char const *message) {
    JNIEnv *env = current_env();
    return (*env)->ThrowNew(env, jni_ref().illegal_argument_exception_class, message);
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

int
throw_assert_error(char const *message) {
    JNIEnv *env = current_env();
    return (*env)->ThrowNew(env, jni_ref().assertion_error_class, message);
}


int
throw_extension_error(jstring message, jthrowable throwable) {
    JNIEnv *env = current_env();

    jthrowable kua_error = (*env)->NewObject(
            env,
            jni_ref().extension_error_class,
            jni_ref().extension_error_ctor_id,
            message,
            throwable
    );

    return (*env)->Throw(env, kua_error);
}

int
throw_script_error(char const *message) {
    JNIEnv *env = current_env();
    return (*env)->ThrowNew(env, jni_ref().script_error_class, message);
}