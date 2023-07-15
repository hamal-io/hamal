#include "kua_common.h"
#include "kua_check.h"
#include "kua_error.h"
#include "kua_macro.h"
#include "lualib.h"

#include <lauxlib.h>

//static jfieldID current_state_id = NULL;
static jfieldID current_thread_id = NULL;
JNIEnv *current_env = NULL;

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
        if (!(*current_env)->IsInstanceOf(current_env, object, class)) {
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
        throw_illegal_state(current_env, "no state");
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
        throw_illegal_state(current_env, "no func");
        return lua_error(L);
    }

    /* Perform the call, handling coroutine situations. */
//    setyield(javastate, JNI_FALSE);
//    T = getluathread(javastate);
//    if (T == L) {

    nresults = (*current_env)->CallIntMethod(current_env, javafunction, invoke_id, javastate);
//    nresults = (*current_env)->CallIntMethod(current_env, javafunction, invoke_id);
//

//    } else {
//        setluathread(javastate, L);
//        nresults = (*current_env)->CallIntMethod(current_env, javafunction, invoke_id, javastate);
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
    *user_data = (*current_env)->NewGlobalRef(current_env, object);

    if (!*user_data) {
        throw_illegal_state(current_env, "JNI error: NewGlobalRef() failed pushing Java object");
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
STATE_METHOD_NAME(pushFunc)(JNIEnv *env, jobject K, jobject f) {
//    lua_State *L;
    current_env = env;


    lua_State *L = state_from_thread(env, K);
//    if (checkstack(L, JNLUA_MINSTACK)
//        && checknotnull(f)) {
    lua_pushcfunction(L, pushjavafunction_protected);
    lua_pushlightuserdata(L, (void *) f);
    lua_pcall(L, 1, 1, 0);
//    }
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


static int gcjavaobject(lua_State *L) {
//    jobject obj;
//
//    if (!current_env) {
//        /* Environment has been cleared as the Java VM was destroyed. Nothing to do. */
//        return 0;
//    }
//    obj = *(jobject *) lua_touserdata(L, 1);
//    if (lua_toboolean(L, lua_upvalueindex(1))) {
//        (*current_env)->DeleteWeakGlobalRef(current_env, obj);
//    } else {
//        (*current_env)->DeleteGlobalRef(current_env, obj);
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
//    *ref = (*current_env)->NewWeakGlobalRef(current_env, newstate_obj);
    *ref = (*current_env)->NewGlobalRef(current_env, newstate_obj);
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
    current_env = env;
    lua_State *L;
    L = luaL_newstate();

    lua_pushcfunction(L, newstate_protected);
    lua_pushlightuserdata(L, (void *) K);
    lua_pcall(L, 1, 1, 0);
    lua_pop(L, 1);


    luaL_openlibs(L);
    state_to_thread(env, K, L);

    kua_func_class = referenceclass(env, "io/hamal/lib/kua/KuaFunc");
    invoke_id = (*env)->GetMethodID(env, kua_func_class, "invokedByLua", "(Lio/hamal/lib/kua/LuaState;)I");
//    invoke_id = (*env)->GetMethodID(env, kua_func_class, "invoke", "()I");
}

