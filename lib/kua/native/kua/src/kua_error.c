#include <stdlib.h>
#include "kua_common.h"
#include "kua_macro.h"
#include "kua_error.h"

static jclass illegalargumentexception_class = NULL;

static jclass
referenceclass(JNIEnv *env, const char *className) {
    jclass clazz;
    clazz = (*env)->FindClass(env, className);
    if (!clazz) {
        return NULL;
    }
    return (*env)->NewGlobalRef(env, clazz);
}

jint
throw_illegal_argument(JNIEnv *env, char const *message) {
    if (!(illegalargumentexception_class = referenceclass(env, "java/lang/IllegalArgumentException"))) {
//        return JNLUA_JNIVERSION;
    }
    return (*env)->ThrowNew(env, illegalargumentexception_class, message);
}
