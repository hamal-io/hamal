#include <jni.h>

#include "kua_info.h"
#include "kua_stack.h"
#include "kua_state.h"
#include "kua_table.h"
#include "kua_kotlin.h"


#define UNUSED __attribute__((unused))
#define STATE_METHOD_NAME(method) Java_io_hamal_lib_kua_LuaState_##method

JNIEnv *current_jni_env = NULL;

JNIEnv *
current_env() {
    return current_jni_env;
}

#define ENV_AND_STATE  current_jni_env = env; lua_State *L = state_from_thread(env, K);

static char const *
to_raw_string(jstring j_str) {
    return (*current_jni_env)->GetStringUTFChars(current_jni_env, j_str, 0);
}

void
release_raw_string(jstring j_str, char const *str) {
    (*current_jni_env)->ReleaseStringUTFChars(current_jni_env, j_str, str);
}


//+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+[INFO]-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

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
STATE_METHOD_NAME(luaRegistryIndex)(UNUSED JNIEnv *env, UNUSED jobject K) {
    ENV_AND_STATE
    return (jint) lua_registry_index();
}

//+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+[STACK]-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(type)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return (jint) type(L, idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(top)(JNIEnv *env, jobject K) {
    ENV_AND_STATE
    return (jint) top(L);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(push)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return (jint) push(L, idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pop)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return (jint) pop(L, idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushNil)(JNIEnv *env, jobject K) {
    ENV_AND_STATE
    return (jint) push_nil(L);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushBoolean)(JNIEnv *env, jobject K, jboolean value) {
    ENV_AND_STATE
    return (jint) push_boolean(L, value);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushNumber)(JNIEnv *env, jobject K, jdouble value) {
    ENV_AND_STATE
    return (jint) push_number(L, value);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushString)(JNIEnv *env, jobject K, jstring value) {
    ENV_AND_STATE
    char const *lua_string = to_raw_string(value);
    jint result = push_string(L, lua_string);
    release_raw_string(value, lua_string);
    return result;
}

JNIEXPORT jboolean JNICALL
STATE_METHOD_NAME(toBoolean)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return to_boolean(L, idx);
}

JNIEXPORT jdouble JNICALL
STATE_METHOD_NAME(toNumber)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return (jdouble) to_number(L, idx);
}


JNIEXPORT jstring JNICALL
STATE_METHOD_NAME(toString)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    char const *str = to_string(L, idx);
    return (*env)->NewStringUTF(env, str);
}

//+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+[TABLE]-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
JNIEXPORT jint JNICALL
STATE_METHOD_NAME(createTable)(JNIEnv *env, jobject K, jint arrayCount, jint recordsCount) {
    ENV_AND_STATE
    return table_create(L, arrayCount, recordsCount);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(setTableField)(JNIEnv *env, jobject K, jint idx, jstring key) {
    ENV_AND_STATE
    char const *table_key = to_raw_string(key);
    int result = table_set(L, idx, table_key);
    release_raw_string(key, table_key);
    return (jint) result;
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(setTableRaw)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return table_raw_set(L, idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(setTableRawIdx)(JNIEnv *env, jobject K, jint stack_idx, jint table_idx) {
    ENV_AND_STATE
    return table_raw_set_idx(L, stack_idx, table_idx);
}


JNIEXPORT jint JNICALL
STATE_METHOD_NAME(getTableField)(JNIEnv *env, jobject K, jint idx, jstring key) {
    ENV_AND_STATE
    char const *table_key = to_raw_string(key);
    int result = table_get(L, idx, table_key);
    release_raw_string(key, table_key);
    return (jint) result;
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(getTableRaw)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return (jint) table_raw_get(L, idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(getTableRawIdx)(JNIEnv *env, jobject K, jint stack_idx, jint table_idx) {
    ENV_AND_STATE
    return (jint) table_raw_get_idx(L, stack_idx, table_idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(getTableLength)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return (jint) table_len(L, idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(getSubTable)(JNIEnv *env, jobject K, jint idx, jstring key) {
    ENV_AND_STATE
    char const *table_key = to_raw_string(key);
    int result = table_get_sub_table(L, idx, table_key);
    release_raw_string(key, table_key);
    return (jint) result;
}


//+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+[INVOKE]-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

JNIEXPORT void JNICALL
STATE_METHOD_NAME(call)(JNIEnv *env, jobject K, jint argsCount, jint resultCount) {
    ENV_AND_STATE
    call(L, argsCount, resultCount);
}
