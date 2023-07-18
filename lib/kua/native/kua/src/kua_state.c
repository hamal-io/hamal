#include <lua.h>
#include <jni.h>

#include "kua_state.h"


#include "kua_macro.h"
#include "lualib.h"

#include <lauxlib.h>


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
referenceclass(JNIEnv *env, const char *className) {
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


static int gcjavaobject(lua_State *L) {
//    jobject obj;
//
//    if (!dep_current_env) {
//        /* Environment has been cleared as the Java VM was destroyed. Nothing to do. */
//        return 0;
//    }
//    obj = *(jobject *) lua_touserdata(L, 1);
//    if (lua_toboolean(L, lua_upvalueindex(1))) {
//        (*dep_current_env)->DeleteWeakGlobalRef(dep_current_env, obj);
//    } else {
//        (*dep_current_env)->DeleteGlobalRef(dep_current_env, obj);
//    }
    return 0;
}


static int newstate_protected(lua_State *L) {
    jobject *ref;
    jobject newstate_obj = (jobject) lua_touserdata(L, 1);
    lua_pop(L, 1);

    /* Set the Java state in the Lua state. */
    ref = lua_newuserdata(L, sizeof(jobject));
    lua_createtable(L, 0, 1);
    lua_pushboolean(L, 1); /* weak global reference */
    lua_pushcclosure(L, gcjavaobject, 1);
    lua_setfield(L, -2, "__gc");
//    *ref = (*dep_current_env)->NewWeakGlobalRef(dep_current_env, newstate_obj);
    *ref = (*dep_current_env)->NewGlobalRef(dep_current_env, newstate_obj);
    if (!*ref) {
        lua_pushliteral(L, "JNI error: NewWeakGlobalRef() failed setting up Lua state");
        return lua_error(L);
    }
    lua_setmetatable(L, -2);
    lua_setfield(L, LUA_REGISTRYINDEX, "__KState");

    /*
     * Create the meta table for Java objects and return it. Population will
     * be finished on the Java side.
     */
    luaL_newmetatable(L, "__KObject");
    lua_pushboolean(L, 0);
    lua_setfield(L, -2, "__metatable");
    lua_pushboolean(L, 0); /* non-weak global reference */
    lua_pushcclosure(L, gcjavaobject, 1);
    lua_setfield(L, -2, "__gc");
    return 1;
}

static void
setup_references(JNIEnv *env) {
    //FIXME throw error / panic

    //@formatter:off
    current_jni_ref.illegal_argument_exception_class = referenceclass(env, "java/lang/IllegalArgumentException");
    current_jni_ref.illegal_state_exception_class = referenceclass(env, "java/lang/IllegalStateException");
    current_jni_ref.error_class = referenceclass(env, "java/lang/Error");

    jclass kua_func_class = referenceclass(env, "io/hamal/lib/kua/value/FuncValue");
    current_jni_ref.invoked_by_lua_method_id = (*env)->GetMethodID(env, kua_func_class, "invokedByLua","(Lio/hamal/lib/kua/LuaState;)I");
    //@formatter:on
}

JNIEXPORT void JNICALL
STATE_METHOD_NAME(init)(JNIEnv *env, jobject K) {
    setup_references(env);

    dep_current_env = env;
    lua_State *L;
    L = luaL_newstate();

    lua_pushcfunction(L, newstate_protected);
    lua_pushlightuserdata(L, (void *) K);
    lua_pcall(L, 1, 1, 0);
    lua_pop(L, 1);


    luaL_openlibs(L);
    state_to_thread(env, K, L);
}


JNIEXPORT void JNICALL
STATE_METHOD_NAME(getGlobal)(JNIEnv *env, jobject K, jstring key) {

    const char *nativeString = (*env)->GetStringUTFChars(env, key, 0);

    lua_State *L = state_from_thread(env, K);

//    lua_getglobal(L, (const char *) nativeString);

//    if (checkstack(L, JNLUA_MINSTACK)
//        && (getglobal_name = getstringchars(name))) {
//    lua_pushcfunction(L, getglobal_protected);
//    lua_pushlightuserdata(L, (void *) nativeString);
//    lua_pcall(L, 1, 1, 0);

    lua_getglobal(L, (const char *) nativeString);

//    }
//    lua_getglobal(L, (const char*)lua_touserdata(L, 1));

//    if (getglobal_name) {
//        releasestringchars(name, getglobal_name);
//    }
}



