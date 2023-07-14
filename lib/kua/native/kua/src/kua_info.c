#include "kua_macro.h"

#include "lua.h"
#include <jni.h>


JNIEXPORT jint JNICALL STATE_METHOD_NAME(integerWidth)(JNIEnv *env, jobject obj) {
    return sizeof(lua_Integer);
}