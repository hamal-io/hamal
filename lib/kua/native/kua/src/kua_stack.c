#include "kua_check.h"
#include "kua_macro.h"
#include "kua_state.h"

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(size)(JNIEnv *env, jobject K) {
    lua_State *L = state_from_thread(env, K);
    return (jint) lua_gettop(L);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(type)(JNIEnv *env, jobject K, jint idx) {
    lua_State *L = state_from_thread(env, K);
    if (check_index(env, L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return (jint) lua_type(L, idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushNil)(JNIEnv *env, jobject K) {
    lua_State *L = state_from_thread(env, K);
    if (check_stack(env, L) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushnil(L);
    return (jint) lua_gettop(L);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushBoolean)(JNIEnv *env, jobject K, jboolean b) {
    lua_State *L = state_from_thread(env, K);
    if (check_stack(env, L) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushboolean(L, b);
    return (jint) lua_gettop(L);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushNumber)(JNIEnv *env, jobject K, jdouble n) {
    lua_State *L = state_from_thread(env, K);
    if (check_stack(env, L) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushnumber(L, n);
    return (jint) lua_gettop(L);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushString)(JNIEnv *env, jobject K, jstring s) {
    lua_State *L = state_from_thread(env, K);
    if (check_stack(env, L) == CHECK_RESULT_ERROR) return LUA_TNONE;
    char const *lua_string = (*env)->GetStringUTFChars(env, s, 0);
    lua_pushstring(L, lua_string);
    (*env)->ReleaseStringUTFChars(env, s, lua_string);
    return (jint) lua_gettop(L);
}

JNIEXPORT jboolean JNICALL
STATE_METHOD_NAME(toBoolean)(JNIEnv *env, jobject K, jint idx) {
    lua_State *L = state_from_thread(env, K);
    if (check_index(env, L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    //FIXME check type
    return (jboolean) lua_toboolean(L, idx);
}

JNIEXPORT jdouble JNICALL
STATE_METHOD_NAME(toNumber)(JNIEnv *env, jobject K, jint idx) {
    lua_State *L = state_from_thread(env, K);
    if (check_index(env, L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    //FIXME check type
    return lua_tonumber(L, idx);
}


JNIEXPORT jstring JNICALL
STATE_METHOD_NAME(toString)(JNIEnv *env, jobject K, jint idx) {
    lua_State *L = state_from_thread(env, K);
    if (check_index(env, L, idx) == CHECK_RESULT_ERROR) return NULL;
    //FIXME check type
    char const *lua_string = lua_tostring(L, idx);
    return (*env)->NewStringUTF(env, lua_string);
}

