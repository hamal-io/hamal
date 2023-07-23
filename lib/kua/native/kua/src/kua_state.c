#include <lua.h>
#include "lualib.h"
#include <lauxlib.h>
#include <jni.h>

#include "kua_state.h"
#include "kua_jni.h"

#define UNUSED __attribute__((unused))
#define STATE_METHOD_NAME(method) Java_io_hamal_lib_kua_Bridge_##method

static jfieldID current_thread_id = NULL;
JNIEnv *dep_current_env = NULL;

static struct jni_ref current_jni_ref = {};

inline struct jni_ref
jni_ref(void) {
    return current_jni_ref;
}


lua_State *
state_from_thread(JNIEnv *env, jobject K) {
    return (lua_State *) (uintptr_t) (*env)->GetLongField(env, K, current_thread_id);
}

static void state_to_thread(JNIEnv *env, jobject K, lua_State *L) {
    (*env)->SetLongField(env, K, current_thread_id, (jlong) (uintptr_t) L);
}

static int setglobal_protected(lua_State *L) {
    lua_setglobal(L, (const char *) lua_touserdata(L, 1));
    return 0;
}

JNIEXPORT void JNICALL
STATE_METHOD_NAME(setGlobal)(JNIEnv *env, jobject K, jstring name) {
//    lua_State *L;
    const char *nativeString = (*env)->GetStringUTFChars(env, name, 0);


    dep_current_env = env;
    lua_State *L = state_from_thread(env, K);

//    if (checkstack(L, JNLUA_MINSTACK)
//        && checknelems(L, 1)
//        && (setglobal_name = getstringchars(name))) {
    lua_pushcfunction(L, setglobal_protected);
    lua_insert(L, -2);
    lua_pushlightuserdata(L, (void *) nativeString);
    lua_insert(L, -2);
    lua_pcall(L, 2, 0, 0);
//    }
//    if (setglobal_name) {
//        releasestringchars(name, setglobal_name);
//    }
}


#include "kua_call.h"


static jclass
load_class(JNIEnv *env, const char *className) {
    jclass clazz;
    clazz = (*env)->FindClass(env, className);
    if (!clazz) {
        return NULL;
    }
    return (*env)->NewGlobalRef(env, clazz);
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

static void
setup_references(JNIEnv *env) {
    //FIXME throw error / panic

    //@formatter:off
    current_jni_ref.illegal_argument_exception_class = load_class(env, "java/lang/IllegalArgumentException");
    current_jni_ref.illegal_state_exception_class = load_class(env, "java/lang/IllegalStateException");
    current_jni_ref.error_class = load_class(env, "java/lang/Error");

    current_jni_ref.kua_error_class = load_class(env, "io/hamal/lib/kua/KuaError");
    current_jni_ref.kua_error_ctor_id =  (*env)->GetMethodID(env,  current_jni_ref.kua_error_class ,"<init>","(Ljava/lang/String;Ljava/lang/Throwable;)V");

    jclass kua_func_class = load_class(env, "io/hamal/lib/kua/function/FunctionValue");
    current_jni_ref.invoked_by_lua_method_id = (*env)->GetMethodID(env, kua_func_class, "invokedByLua","(Lio/hamal/lib/kua/Bridge;)I");
    //@formatter:on
}

#include "kua_check.h"

static int
cleanup_k_object(lua_State *L) {
    JNIEnv *env = current_env();
    if (!env) {
        return LUA_OK;
    }

    if (check_type_at(L, 1, USER_DATA_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    jobject obj = *(jobject *) lua_touserdata(L, 1);
    (*env)->DeleteGlobalRef(env, obj);
    return LUA_OK;
}


JNIEXPORT void JNICALL
STATE_METHOD_NAME(initConnection)(JNIEnv *env, jobject K) {
    setup_references(env);

    dep_current_env = env;
    lua_State *L;
    L = luaL_newstate();
    lua_pushlightuserdata(L, (void *) K);

    jobject *ref;
    jobject newstate_obj = (jobject) lua_touserdata(L, 1);
    lua_pop(L, 1);

    ref = lua_newuserdata(L, sizeof(jobject));
    lua_createtable(L, 0, 1);
    *ref = (*dep_current_env)->NewWeakGlobalRef(dep_current_env, newstate_obj);
    if (!*ref) {
        printf("JNI error: NewWeakGlobalRef() failed setting up Lua state");
        lua_error(L);
    }
    lua_setmetatable(L, -1);
    lua_setfield(L, LUA_REGISTRYINDEX, "__KState");


    luaL_newmetatable(L, "__KObject");
    lua_pushboolean(L, 0);
    lua_setfield(L, -2, "__metatable");

    lua_pushcclosure(L, cleanup_k_object, 0);
    lua_setfield(L, -2, "__gc");

    // FIXME gc should not be required --> use memory arena allocator and clean up properly when closing connection
    lua_gc(L, LUA_GCSTOP);

    luaL_openlibs(L); // FIXME replace with custom open libs to only import subset of libs/functions
    state_to_thread(env, K, L);
}


JNIEXPORT void JNICALL
STATE_METHOD_NAME(getGlobal)(JNIEnv *env, jobject K, jstring key) {
    const char *nativeString = (*env)->GetStringUTFChars(env, key, 0);
    lua_State *L = state_from_thread(env, K);
    lua_getglobal(L, (const char *) nativeString);
}



