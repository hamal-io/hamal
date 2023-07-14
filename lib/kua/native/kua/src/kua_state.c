#include "kua_common.h"
#include "kua_macro.h"
#include <lauxlib.h>

static jfieldID current_state_id = NULL;
static jfieldID current_thread_id = NULL;

static lua_State *get_state(JNIEnv *env, jobject K) {
    return (lua_State *) (uintptr_t) (*env)->GetLongField(env, K, current_state_id);
}

static void set_state(JNIEnv *env, jobject K, lua_State *L) {
    (*env)->SetLongField(env, K, current_state_id, (jlong) (uintptr_t) L);
}

static void set_thread(JNIEnv *env, jobject K, lua_State *L) {
    (*env)->SetLongField(env, K, current_thread_id, (jlong) (uintptr_t) L);
}


static lua_State *controlled_newstate(jobject K) {
    lua_State *L = luaL_newstate();
    if (L) {
        jint total, used;
//        getluamemory(K, &total, &used);
//        if (total > 0) {
//            lua_setallocf(L, l_alloc_checked, L);
//        }
//        lua_atpanic(L, &panic);
    }
    return L;
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(top)(JNIEnv *env, jobject K) {
    lua_State *L = get_state(env, K);
//    if (checkstack(L, JNLUA_MINSTACK)) {
    return (jint) lua_gettop(L);
}

JNIEXPORT void JNICALL
STATE_METHOD_NAME(pushBoolean)(JNIEnv *env, jobject K, jboolean b) {
//    lua_State *L;

//    JNLUA_ENV(env);
//    lua_State *L = get_thread(env, K);
    lua_State *L = get_state(env, K);

//    if (checkstack(L, JNLUA_MINSTACK)) {
    lua_pushboolean(L, b);
//    }

//    return sizeof(lua_Integer);

//    return b;
}

JNIEXPORT jboolean JNICALL
STATE_METHOD_NAME(peekBoolean)(JNIEnv *env, jobject K, jint idx) {
//    lua_State *L;

//    JNLUA_ENV(env);
//    lua_State *L = get_thread(env, K);
    lua_State *L = get_state(env, K);

////    if (checkstack(L, JNLUA_MINSTACK)) {
//    lua_pushboolean(L, b);
//    }

//    return sizeof(lua_Integer);

    return (jboolean) lua_toboolean(L, idx);
}


JNIEXPORT void JNICALL
STATE_METHOD_NAME(init)(JNIEnv *env, jobject K) {
    lua_State *L;

//    /* Initialized? */
//    if (!initialized) {
//        return;
//    }
//
//    /* API version? */
//    if (apiversion != JNLUA_APIVERSION) {
//        return;
//    }
//
//    /* Create or attach to Lua state. */
//    JNLUA_ENV(env);


//    L = controlled_newstate(K);
    L = luaL_newstate();

//    lua_pushboolean(L, 0);

    set_thread(env, K, L);
    set_state(env, K, L);

    Java_io_hamal_lib_kua_State_pushBoolean(env, K, 0);


//    return (jboolean) lua_toboolean(L, 1);


//    L = !existing ? controlled_newstate(K) : (lua_State *) (uintptr_t) existing;
//    if (!L) {
//        return;
//    }
//
//    /* Setup Lua state. */
//    if (checkstack(L, JNLUA_MINSTACK)) {
//        lua_pushcfunction(L, newstate_protected);
//        lua_pushlightuserdata(L, (void*)K);
//        JNLUA_PCALL(L, 1, 1);
//    }
//    if ((*env)->ExceptionCheck(env)) {
//        if (!existing) {
//            lua_setallocf(L, l_alloc_unchecked, NULL);
//            setluamemoryused(K, 0);
//            lua_close(L);
//        }
//        return;
//    }

    /* Set the Lua state in the Java state. */

}

static lua_State *get_thread(JNIEnv *env, jobject K) {
    return (lua_State *) (uintptr_t) (*env)->GetLongField(env, K, current_thread_id);
}

