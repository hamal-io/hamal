#include <lua.h>
#include <lauxlib.h>
#include <jni.h>

#include "kua_call.h"
#include "kua_jni.h"
#include "kua_jni_error.h"
#include "kua_state.h"


static int
call_error_handler(lua_State *L) {
    //FIXME implement me
    return 0;
}

enum result
call(lua_State *L, int argsCount, int resultCount) {
//    if (checkarg(argsCount >= 0, "illegal argument count")
//        && checknelems(L, argsCount + 1)
//        && checkarg(resultCount >= 0 || resultCount == LUA_MULTRET, "illegal return count")
//        && (resultCount == LUA_MULTRET || checkstack(L, resultCount - (argsCount + 1)))) {
    int idx = lua_absindex(L, -argsCount - 1);
    lua_pushcfunction(L, call_error_handler);
    lua_insert(L, idx);
    int status = lua_pcall(L, argsCount, resultCount, idx);
    lua_remove(L, idx);
//    if (status != LUA_OK) {
//        throw(L, status);
//    }
    return RESULT_OK;
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


static jobject
load_func_value(lua_State *L) {
    jobject object;

    if (!lua_isuserdata(L, -1)) {
        return NULL;
    }
    if (!lua_getmetatable(L, -1)) {
        return NULL;
    }

    luaL_getmetatable(L, "__KObject");
    int result = lua_rawequal(L, -1, -2);
    lua_pop(L, 2);
    if (!result) {
        return NULL;
    }
    object = *(jobject *) lua_touserdata(L, -1);
    lua_pop(L, 1);
    return object;
}

static enum result
load_kstate(lua_State *L) {
    lua_getfield(L, LUA_REGISTRYINDEX, "__KState");
    if (!lua_isuserdata(L, -1)) {
        throw_error("Unable to obtain state");
        return RESULT_ERROR;
    }
    lua_pushvalue(L, lua_upvalueindex(1));
    return RESULT_OK;
}


int
call_func_value(lua_State *L) {
    JNIEnv *env = current_env();
    if (load_kstate(L) == RESULT_ERROR) return lua_error(L);

    jobject func_value = load_func_value(L);
    if (!func_value) {
        throw_illegal_state("Unable to load func value");
        return lua_error(L);
    }


    return (*env)->CallIntMethod(env, func_value, jni_ref().invoked_by_lua_method_id);
}


int call_func_value_closure(lua_State *L) {
    JNIEnv *env = current_env();
    jobject *data = (jobject *) lua_newuserdata(L, sizeof(jobject));
    luaL_getmetatable(L, "__KObject");

    *data = (*env)->NewGlobalRef(env, (jobject) lua_touserdata(L, 1));
    if (data == NULL) {
        throw_error("Error: Failed to create global reference");
        return 0;
    }

    lua_setmetatable(L, -2);
    lua_pushcclosure(L, call_func_value, 1);
    return 1;
}
