#include <stdlib.h>
#include "kua_common.h"
#include "kua_macro.h"
#include "kua_jni_error.h"
#include "kua_jni.h"

static jclass illegalargumentexception_class = NULL;
static jclass illegal_state_exception_class = NULL;
static jclass stackoverflowerror_class = NULL;

static jclass
referenceclass(JNIEnv *env, const char *className) {
    jclass clazz;
    clazz = (*env)->FindClass(env, className);
    if (!clazz) {
        return NULL;
    }
    return (*env)->NewGlobalRef(env, clazz);
}


int
throw_illegal_argument(char const *message) {
    JNIEnv *env = current_env();
    if (!(illegalargumentexception_class = referenceclass(env, "java/lang/IllegalArgumentException"))) {
//        return JNLUA_JNIVERSION;
    }
    return (*env)->ThrowNew(env, illegalargumentexception_class, message);
}


int
throw_illegal_state(char const *message) {
    JNIEnv *env = current_env();
    if (!(illegal_state_exception_class = referenceclass(env, "java/lang/IllegalStateException"))) {
//        return JNLUA_JNIVERSION;
    }
    return (*env)->ThrowNew(env, illegal_state_exception_class, message);
}
