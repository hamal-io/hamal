#include <lua.h>
#include <lauxlib.h>
#include <jni.h>

#include "kua_call.h"
#include "kua_info.h"
#include "kua_load.h"
#include "kua_stack.h"
#include "kua_state.h"
#include "kua_table.h"


#define UNUSED __attribute__((unused))
#define STATE_METHOD_NAME(method) Java_io_hamal_lib_kua_Native_##method

JNIEnv *current_jni_env = NULL;

JNIEnv *
current_env() {
    return current_jni_env;
}

#define ENV_AND_STATE  current_jni_env = env; lua_State *L = current_state(env, K);

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

JNIEXPORT void JNICALL
STATE_METHOD_NAME(setTop)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    set_top(L, idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(absIndex)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return abs_index(L, idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushTop)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return (jint) push_top(L, idx);
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
STATE_METHOD_NAME(pushDecimal)(JNIEnv *env, jobject K, jstring value) {
    ENV_AND_STATE
    char const *lua_string = to_raw_string(value);
    jint result = push_decimal(L, lua_string);
    release_raw_string(value, lua_string);
    return result;
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushError)(JNIEnv *env, jobject K, jstring value) {
    ENV_AND_STATE
    char const *lua_string = to_raw_string(value);
    jint result = push_error(L, lua_string);
    release_raw_string(value, lua_string);
    return result;
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

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushFunction)(JNIEnv *env, jobject K, jobject func) {
    ENV_AND_STATE
    return push_func(L, func);
}

JNIEXPORT jstring JNICALL
STATE_METHOD_NAME(toDecimalString)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    char const *str = to_decimal(L, idx);
    return (*env)->NewStringUTF(env, str);
}


JNIEXPORT jstring JNICALL
STATE_METHOD_NAME(toError)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    char const *str = to_string(L, idx);
    return (*env)->NewStringUTF(env, str);
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
STATE_METHOD_NAME(tableCreate)(JNIEnv *env, jobject K, jint arrayCount, jint recordsCount) {
    ENV_AND_STATE
    return table_create(L, arrayCount, recordsCount);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(tabletSetField)(JNIEnv *env, jobject K, jint idx, jstring key) {
    ENV_AND_STATE
    char const *table_key = to_raw_string(key);
    int result = table_set(L, idx, table_key);
    release_raw_string(key, table_key);
    return (jint) result;
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(tableAppend)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return table_append(L, idx);
}


JNIEXPORT jint JNICALL
STATE_METHOD_NAME(tableSetRaw)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return table_raw_set(L, idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(tableSetRawIdx)(JNIEnv *env, jobject K, jint stack_idx, jint table_idx) {
    ENV_AND_STATE
    return table_raw_set_idx(L, stack_idx, table_idx);
}


JNIEXPORT jint JNICALL
STATE_METHOD_NAME(tableGetField)(JNIEnv *env, jobject K, jint idx, jstring key) {
    ENV_AND_STATE
    char const *table_key = to_raw_string(key);
    int result = table_get(L, idx, table_key);
    release_raw_string(key, table_key);
    return (jint) result;
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(tableGetRaw)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return (jint) table_raw_get(L, idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(tableGetRawIdx)(JNIEnv *env, jobject K, jint stack_idx, jint table_idx) {
    ENV_AND_STATE
    return (jint) table_raw_get_idx(L, stack_idx, table_idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(tableGetLength)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return (jint) table_len(L, idx);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(tableGetSubTable)(JNIEnv *env, jobject K, jint idx, jstring key) {
    ENV_AND_STATE
    char const *table_key = to_raw_string(key);
    int result = table_get_sub_table(L, idx, table_key);
    release_raw_string(key, table_key);
    return (jint) result;
}

JNIEXPORT jboolean JNICALL
STATE_METHOD_NAME(tableNext)(JNIEnv *env, jobject K, jint idx) {
    ENV_AND_STATE
    return table_next(L, idx);
}

//+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+[INVOKE]-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

JNIEXPORT void JNICALL
STATE_METHOD_NAME(call)(JNIEnv *env, jobject K, jint argsCount, jint resultCount) {
    ENV_AND_STATE
    call(L, argsCount, resultCount);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(loadString)(JNIEnv *env, jobject K, jstring code) {
    ENV_AND_STATE
    const char *code_c_str = to_raw_string(code);
    int result = load_string(L, code_c_str);
    release_raw_string(code, code_c_str);
    return (jint) result;
}


//+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+[STATE]-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

JNIEXPORT void JNICALL
STATE_METHOD_NAME(initConnection)(JNIEnv *env, jobject K) {
    init_connection(env, K);
}


JNIEXPORT void JNICALL
STATE_METHOD_NAME(closeConnection)(JNIEnv *env, jobject K) {
    ENV_AND_STATE
    close_connection(L);
}


JNIEXPORT void JNICALL
STATE_METHOD_NAME(getGlobal)(JNIEnv *env, jobject K, jstring key) {
    const char *c_str = to_raw_string(key);
    lua_State *L = current_state(env, K);
    lua_getglobal(L, (const char *) c_str);
    release_raw_string(key, c_str);
}


JNIEXPORT void JNICALL
STATE_METHOD_NAME(setGlobal)(JNIEnv *env, jobject K, jstring name) {
    const char *c_str = to_raw_string(name);
    lua_State *L = current_state(env, K);
    //FIXME
//    if (checkstack(L, JNLUA_MINSTACK)
//        && checknelems(L, 1)
//        && (setglobal_name = getstringchars(name))) {
    lua_setglobal(L, c_str);
    release_raw_string(name, c_str);
}
