#include "kua_common.h"
#include "kua_check.h"
#include "kua_error.h"
#include "kua_macro.h"
#include "lualib.h"

#include <lauxlib.h>

//static jfieldID current_state_id = NULL;
static jfieldID current_thread_id = NULL;

lua_State *
state_from_thread(JNIEnv *env, jobject K) {
    return (lua_State *) (uintptr_t) (*env)->GetLongField(env, K, current_thread_id);
}

//static lua_State *state_from_thread(JNIEnv *env, jobject K) {
//    return (lua_State *) (uintptr_t) (*env)->GetLongField(env, K, current_thread_id);
//}

static void state_to_thread(JNIEnv *env, jobject K, lua_State *L) {
    (*env)->SetLongField(env, K, current_thread_id, (jlong) (uintptr_t) L);
}

//static void set_state(JNIEnv *env, jobject K, lua_State *L) {
//    (*env)->SetLongField(env, K, current_state_id, (jlong) (uintptr_t) L);
//}



JNIEXPORT jint JNICALL
STATE_METHOD_NAME(size)(JNIEnv *env, jobject K) {
    lua_State *L = state_from_thread(env, K);
//    if (checkstack(L, JNLUA_MINSTACK)) {
    return (jint) lua_gettop(L);
}


/* Handles Lua errors. */
static int messagehandler(lua_State *L) {
    int level, count;
    lua_Debug ar;
    jobjectArray luastacktrace;
    jstring name, source;
    jobject luastacktraceelement;
    jobject luaerror;
    jstring message;

    /* Count relevant stack frames */
    level = 1;
    count = 0;
//    while (lua_getstack(L, level, &ar)) {
//        lua_getinfo(L, "nSl", &ar);
//        if (isrelevant(&ar)) {
//            count++;
//        }
//        level++;
//    }
//
//    /* Create Lua stack trace as a Java LuaStackTraceElement[] */
//    luastacktrace = (*thread_env)->NewObjectArray(thread_env, count, luastacktraceelement_class, NULL);
//    if (!luastacktrace) {
//        return 1;
//    }
//    level = 1;
//    count = 0;
//    while (lua_getstack(L, level, &ar)) {
//        lua_getinfo(L, "nSl", &ar);
//        if (isrelevant(&ar)) {
//            name = ar.name ? (*thread_env)->NewStringUTF(thread_env, ar.name) : NULL;
//            source = ar.source ? (*thread_env)->NewStringUTF(thread_env, ar.source) : NULL;
//            luastacktraceelement = (*thread_env)->NewObject(thread_env, luastacktraceelement_class,	luastacktraceelement_id, name, source, ar.currentline);
//            if (!luastacktraceelement) {
//                return 1;
//            }
//            (*thread_env)->SetObjectArrayElement(thread_env, luastacktrace, count, luastacktraceelement);
//            if ((*thread_env)->ExceptionCheck(thread_env)) {
//                return 1;
//            }
//            count++;
//        }
//        level++;
//    }
//
//    /* Get or create the error object  */
//    luaerror = tojavaobject(L, -1, luaerror_class);
//    if (!luaerror) {
//        message = tostring(L, -1);
//        if (!(luaerror = (*thread_env)->NewObject(thread_env, luaerror_class, luaerror_id, message, NULL))) {
//            return 1;
//        }
//    }
//    (*thread_env)->CallVoidMethod(thread_env, luaerror, setluastacktrace_id, luastacktrace);
//
//    /* Replace error */
//    pushjavaobject(L, luaerror);
    return 1;
}


JNIEXPORT jint JNICALL
STATE_METHOD_NAME(loadString)(JNIEnv *env, jobject K, jstring code) {
//    lua_State *L;

//    JNLUA_ENV(env);
//    lua_State *L = get_thread(env, K);
    lua_State *L = state_from_thread(env, K);

    const char *nativeString = (*env)->GetStringUTFChars(env, code, 0);
    // use your string
    int result = luaL_loadstring(L, nativeString);

    (*env)->ReleaseStringUTFChars(env, code, nativeString);

    return (jint) result;
}

JNIEXPORT void JNICALL
STATE_METHOD_NAME(call)(JNIEnv *env, jobject K, jint argsCount, jint resultCount) {
    lua_State *L;
    int idx, status;

//    JNLUA_ENV(env);
    L = state_from_thread(env, K);
//    if (checkarg(argsCount >= 0, "illegal argument count")
//        && checknelems(L, argsCount + 1)
//        && checkarg(resultCount >= 0 || resultCount == LUA_MULTRET, "illegal return count")
//        && (resultCount == LUA_MULTRET || checkstack(L, resultCount - (argsCount + 1)))) {
    idx = lua_absindex(L, -argsCount - 1);
    lua_pushcfunction(L, messagehandler);
    lua_insert(L, idx);
    status = lua_pcall(L, argsCount, resultCount, idx);
    lua_remove(L, idx);
//    if (status != LUA_OK) {
//        throw(L, status);
//    }

}


JNIEXPORT jboolean JNICALL
STATE_METHOD_NAME(toBoolean)(JNIEnv *env, jobject K, jint idx) {
    lua_State *L = state_from_thread(env, K);

    if (check_index(env, L, idx) == CHECK_RESULT_ERROR) return 0;

    return (jboolean) lua_toboolean(L, idx);
}


JNIEXPORT void JNICALL
STATE_METHOD_NAME(init)(JNIEnv *env, jobject K) {
    lua_State *L;
    L = luaL_newstate();
    luaL_openlibs(L);
    state_to_thread(env, K, L);
}

