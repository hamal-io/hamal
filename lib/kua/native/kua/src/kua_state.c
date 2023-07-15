#include "kua_common.h"
#include "kua_check.h"
#include "kua_error.h"
#include "kua_macro.h"
#include "lualib.h"

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
STATE_METHOD_NAME(size)(JNIEnv *env, jobject K) {
    lua_State *L = get_state(env, K);
//    if (checkstack(L, JNLUA_MINSTACK)) {
    return (jint) lua_gettop(L);
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushBoolean)(JNIEnv *env, jobject K, jboolean b) {
//    lua_State *L;

//    JNLUA_ENV(env);
//    lua_State *L = get_thread(env, K);
    lua_State *L = get_state(env, K);

//    if (checkstack(L, JNLUA_MINSTACK)) {
    lua_pushboolean(L, b);
//    }

//    return sizeof(lua_Integer);

    return (jint) lua_gettop(L);
}

/* Handles Lua errors. */
static int messagehandler (lua_State *L) {
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
    lua_State *L = get_state(env, K);

    const char *nativeString = (*env)->GetStringUTFChars(env, code, 0);
    // use your string
    int result = luaL_loadstring(L, nativeString);

    (*env)->ReleaseStringUTFChars(env, code, nativeString);

    return (jint) result;
}

JNIEXPORT void JNICALL
STATE_METHOD_NAME(call)(JNIEnv *env, jobject K, jint nargs, jint nresults) {
    lua_State *L;
    int index, status;

//    JNLUA_ENV(env);
    L = get_state(env, K);
//    if (checkarg(nargs >= 0, "illegal argument count")
//        && checknelems(L, nargs + 1)
//        && checkarg(nresults >= 0 || nresults == LUA_MULTRET, "illegal return count")
//        && (nresults == LUA_MULTRET || checkstack(L, nresults - (nargs + 1)))) {
    index = lua_absindex(L, -nargs - 1);
    lua_pushcfunction(L, messagehandler);
    lua_insert(L, index);
    status = lua_pcall(L, nargs, nresults, index);
    lua_remove(L, index);
//    if (status != LUA_OK) {
//        throw(L, status);
//    }

}


//static jclass illegalargumentexception_class = NULL;
//
//static jclass
//referenceclass(JNIEnv *env, const char *className) {
//    jclass clazz;
//    clazz = (*env)->FindClass(env, className);
//    if (!clazz) {
//        return NULL;
//    }
//    return (*env)->NewGlobalRef(env, clazz);
//}


JNIEXPORT jboolean JNICALL
STATE_METHOD_NAME(toBoolean)(JNIEnv *env, jobject K, jint idx) {
    lua_State *L = get_state(env, K);
    if (check_index(env, L, idx)) {
        return 0;
    }
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

    // load Lua base libraries (print / math / etc)
    luaL_openlibs(L);

//    lua_pushboolean(L, 0);

    set_thread(env, K, L);
    set_state(env, K, L);

//    Java_io_hamal_lib_kua_State_pushBoolean(env, K, 0);


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

