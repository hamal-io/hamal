#include "kua_common.h"
#include "kua_macro.h"


JNIEXPORT jint JNICALL
STATE_METHOD_NAME(integerWidth)(UNUSED JNIEnv *env, UNUSED jobject obj) {
    return sizeof(lua_Integer);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(versionNumber)(UNUSED JNIEnv *env, UNUSED jobject obj) {
    return (jint) LUA_VERSION_NUM;
}