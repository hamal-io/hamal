#include "kua_common.h"
#include "kua_check.h"
#include "kua_jni_error.h"
#include "kua_macro.h"
#include "lualib.h"

#include <jni.h>
#include <lauxlib.h>


void
get_global(lua_State *L, char const *key) {

}


//static jfieldID current_state_id = NULL;
static jfieldID current_thread_id = NULL;
JNIEnv *dep_current_env = NULL;

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

#define KUA_OBJECT_NAME "kua.Object"
#define KUA_STATE_NAME "kua.KuaState"


static jmethodID invoke_id = 0;
static jclass kua_func_class = NULL;

static int getsubtable_protected(lua_State *L) {
    lua_pushboolean(L, luaL_getsubtable(L, 2, (const char *) lua_touserdata(L, 1)));
    return 2;
}

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(getSubTable)(JNIEnv *env, jobject K, jint index, jstring fname) {
    dep_current_env = env;
    lua_State *L = state_from_thread(env, K);
    jint getsubtable_result = 0;

//    JNLUA_ENV(env);
//    L = getluathread(K);
//    if (checkstack(L, JNLUA_MINSTACK)
//        && checkindex(L, index)
//        && (getsubtable_fname = getstringchars(fname))) {

    const char *nativeString = (*env)->GetStringUTFChars(env, fname, 0);

    index = lua_absindex(L, index);
    lua_pushcfunction(L, getsubtable_protected);
    lua_pushlightuserdata(L, (void *) nativeString);
    lua_pushvalue(L, index);
    lua_pcall(L, 2, 2, 0);
    getsubtable_result = (jint) lua_toboolean(L, -1);
    lua_pop(L, 1);
//    }
//    if (getsubtable_fname) {
//        releasestringchars(fname, getsubtable_fname);
//    }
    return getsubtable_result;
}


JNIEXPORT void JNICALL
STATE_METHOD_NAME(rawGet)(JNIEnv *env, jobject K, jint index) {
    dep_current_env = env;
    lua_State *L = state_from_thread(env, K);
//    if (checktype(L, index, LUA_TTABLE)) {
    lua_rawget(L, index);
//    }
}

JNIEXPORT void JNICALL
STATE_METHOD_NAME(rawGetI)(JNIEnv *env, jobject K, jint index, jint key) {
    dep_current_env = env;
    lua_State *L = state_from_thread(env, K);
//    if (checktype(L, index, LUA_TTABLE)) {
    lua_rawgeti(L, index, key);
//    }
}


/* lua_setglobal() */
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

static int createtable_protected(lua_State *L) {
    lua_createtable(L, lua_tointeger(L, 1), lua_tointeger(L, 2));
    return 1;
}


static jobject tojavaobject(lua_State *L, int index, jclass class) {
    int result;
    jobject object;

    if (!lua_isuserdata(L, index)) {
        return NULL;
    }
    if (!lua_getmetatable(L, index)) {
        return NULL;
    }
    luaL_getmetatable(L, KUA_OBJECT_NAME);
    result = lua_rawequal(L, -1, -2);
    lua_pop(L, 2);
    if (!result) {
        return NULL;
    }
    object = *(jobject *) lua_touserdata(L, index);
    if (class) {
        if (!(*dep_current_env)->IsInstanceOf(dep_current_env, object, class)) {
            return NULL;
        }
    }
    return object;
}


/* Calls a Java function. If an exception is reported, store it as the cause for later use. */
static int calljavafunction(lua_State *L) {
    jobject javastate, javafunction;
    lua_State *T;
    int nresults;
    jthrowable throwable;
    jstring where;
    jobject luaerror;

    /* Get Java state. */
    lua_getfield(L, LUA_REGISTRYINDEX, KUA_STATE_NAME);
    if (!lua_isuserdata(L, -1)) {
        /* Java state has been cleared as the Java VM was destroyed. Cannot call. */
        lua_pushliteral(L, "no Java state");
        throw_illegal_state("no state");
        return lua_error(L);
    }
//
    javastate = *(jobject *) lua_touserdata(L, -1);
//    javastate = NULL;
    lua_pop(L, 1);

    /* Get Java function object. */
    lua_pushvalue(L, lua_upvalueindex(1));
    javafunction = tojavaobject(L, -1, kua_func_class);
    lua_pop(L, 1);
    if (!javafunction) {
        /* Function was cleared from outside JNLua code. */
        lua_pushliteral(L, "no Java function");
        throw_illegal_state("no func");
        return lua_error(L);
    }

    /* Perform the call, handling coroutine situations. */
//    setyield(javastate, JNI_FALSE);
//    T = getluathread(javastate);
//    if (T == L) {

    nresults = (*dep_current_env)->CallIntMethod(dep_current_env, javafunction, invoke_id, javastate);
//    nresults = (*dep_current_env)->CallIntMethod(dep_current_env, javafunction, invoke_id);
//

//    } else {
//        setluathread(javastate, L);
//        nresults = (*dep_current_env)->CallIntMethod(dep_current_env, javafunction, invoke_id, javastate);
//        setluathread(javastate, T);
//    }

    /* Handle exception */
//    throwable = (*thread_env)->ExceptionOccurred(thread_env);
//    if (throwable) {
//        /* Push exception & clear */
//        luaL_where(L, 1);
//        where = tostring(L, -1);
//        luaerror = (*thread_env)->NewObject(thread_env, luaerror_class, luaerror_id, where, throwable);
//        if (luaerror) {
//            pushjavaobject(L, luaerror);
//        } else {
//            lua_pushliteral(L, "JNI error: NewObject() failed creating Lua error");
//        }
//        (*thread_env)->ExceptionClear(thread_env);
//
//        /* Error out */
//        return lua_error(L);
//    }

    /* Handle yield */
//    if (getyield(javastate)) {
//        if (nresults < 0 || nresults > lua_gettop(L)) {
//            lua_pushliteral(L, "illegal return count");
//            return lua_error(L);
//        }
//        if (L == getluastate(javastate)) {
//            lua_pushliteral(L, "not in a thread");
//            return lua_error(L);
//        }
//        return lua_yield(L, nresults);
//    }

    return nresults;
}

/* ---- Java objects and functions ---- */
/* Pushes a Java object on the stack. */
static void pushjavaobject(lua_State *L, jobject object) {
    jobject *user_data;

    user_data = (jobject *) lua_newuserdata(L, sizeof(jobject));
    luaL_getmetatable(L, KUA_OBJECT_NAME);
    *user_data = (*dep_current_env)->NewGlobalRef(dep_current_env, object);

    if (!*user_data) {
        throw_illegal_state("JNI error: NewGlobalRef() failed pushing Java object");
        lua_pushliteral(L, "JNI error: NewGlobalRef() failed pushing Java object");
        lua_error(L);
    }
    lua_setmetatable(L, -2);
}


/* lua_pushjavafunction() */
static int pushjavafunction_protected(lua_State *L) {
    pushjavaobject(L, (jobject) lua_touserdata(L, 1));
    lua_pushcclosure(L, calljavafunction, 1);
    return 1;
}

static jclass
referenceclass(JNIEnv *env, const char *className) {
    jclass clazz;
    clazz = (*env)->FindClass(env, className);
    if (!clazz) {
        return NULL;
    }
    return (*env)->NewGlobalRef(env, clazz);
}


JNIEXPORT void JNICALL
STATE_METHOD_NAME(pushFuncValue)(JNIEnv *env, jobject K, jobject f) {
//    lua_State *L;
    dep_current_env = env;


    lua_State *L = state_from_thread(env, K);
//    if (checkstack(L, JNLUA_MINSTACK)
//        && checknotnull(f)) {
    lua_pushcfunction(L, pushjavafunction_protected);
    lua_pushlightuserdata(L, (void *) f);
    lua_pcall(L, 1, 1, 0);
//    }
}

static int pushjavaobject_protected(lua_State *L) {
    pushjavaobject(L, (jobject) lua_touserdata(L, 1));
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
    lua_setfield(L, LUA_REGISTRYINDEX, KUA_STATE_NAME);

    /*
     * Create the meta table for Java objects and return it. Population will
     * be finished on the Java side.
     */
    luaL_newmetatable(L, KUA_OBJECT_NAME);
    lua_pushboolean(L, 0);
    lua_setfield(L, -2, "__metatable");
    lua_pushboolean(L, 0); /* non-weak global reference */
    lua_pushcclosure(L, gcjavaobject, 1);
    lua_setfield(L, -2, "__gc");
    return 1;
}


JNIEXPORT void JNICALL
STATE_METHOD_NAME(init)(JNIEnv *env, jobject K) {
    dep_current_env = env;
    lua_State *L;
    L = luaL_newstate();

    lua_pushcfunction(L, newstate_protected);
    lua_pushlightuserdata(L, (void *) K);
    lua_pcall(L, 1, 1, 0);
    lua_pop(L, 1);


    luaL_openlibs(L);
    state_to_thread(env, K, L);

    kua_func_class = referenceclass(env, "io/hamal/lib/kua/value/FuncValue");
    invoke_id = (*env)->GetMethodID(env, kua_func_class, "invokedByLua", "(Lio/hamal/lib/kua/LuaState;)I");
//    invoke_id = (*env)->GetMethodID(env, kua_func_class, "invoke", "()I");
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



