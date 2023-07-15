#include <jni.h>

#include "kua_info.h"
#include "kua_stack.h"

#include "kua_state.h"

#define UNUSED __attribute__((unused))
#define STATE_METHOD_NAME(method) Java_io_hamal_lib_kua_LuaState_##method

JNIEnv *current_jni_env = NULL;

JNIEnv *
current_env() {
    return current_jni_env;
}

#define ENV_AND_STATE  current_jni_env = env; lua_State *L = state_from_thread(env, K);

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(luaIntegerWidth)(UNUSED JNIEnv *env, UNUSED jobject K) {
    ENV_AND_STATE
    return (jint) lua_integer_width();
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(luaVersionNumber)(UNUSED JNIEnv *env, UNUSED jobject K) {
    ENV_AND_STATE
    return (jint) lua_version_number();
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(type)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return (jint) type(L, idx);
}
