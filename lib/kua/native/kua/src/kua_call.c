#include <lua.h>
#include <lauxlib.h>
#include <jni.h>

#include "kua_call.h"
#include "kua_jni.h"
#include "kua_jni_error.h"
#include "kua_state.h"

static int
errorHandler(lua_State *L) {
    JNIEnv *env = current_env();
    jthrowable throwable = (*env)->ExceptionOccurred(env);
    if (throwable) {

        /**
         * Its an assertion error -> it does not need to be handled here
         */
        if ((*env)->IsInstanceOf(env, throwable, jni_ref().assertion_error_class) ||
            (*env)->IsInstanceOf(env, throwable, jni_ref().script_error_class)) {
            throw(throwable);
            return 0;
        }

        throw_extension_error(throwable);
        return 0;
    }

    char const *error_c_str = lua_tostring(L, -1);
    throw_script_error(error_c_str);
    return 1;
}

enum result
call(lua_State *L, int argsCount, int resultCount) {
    int error_handler_index = lua_absindex(L, -argsCount - 1);
    lua_pushcfunction(L, errorHandler);
    lua_insert(L, error_handler_index);
    int status = lua_pcall(L, argsCount, resultCount, error_handler_index);

    lua_remove(L, error_handler_index);
    if (status != LUA_OK) {
        return RESULT_ERROR;
    }
    return RESULT_OK;
}

static jobject
load_func(lua_State *L) {
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
    return *(jobject *) lua_touserdata(L, -1);
}


int
call_func_value(lua_State *L) {
    lua_getfield(L, LUA_REGISTRYINDEX, "__KState");
    if (!lua_isuserdata(L, -1)) {
        throw_error("Unable to obtain state");
        return lua_error(L);
    }
    jobject kstate = *(jobject *) lua_touserdata(L, -1);
    lua_pop(L, 1);

    lua_pushvalue(L, lua_upvalueindex(1));
    jobject func_to_call = load_func(L);
    lua_pop(L, 1);
    if (!func_to_call) {
        throw_illegal_state("Unable to load func value");
        return lua_error(L);
    }

    JNIEnv *env = current_env();
    int result = (*env)->CallIntMethod(env, func_to_call, jni_ref().invoked_by_lua_method_id, kstate);

    if ((*env)->ExceptionOccurred(env)) {
        return lua_error(L);
    }

    return result;
}


int
call_func_value_closure(lua_State *L) {
    JNIEnv *env = current_env();
    jobject *user_data = (jobject *) lua_newuserdata(L, sizeof(jobject));
    luaL_getmetatable(L, "__KObject");

    *user_data = (*env)->NewGlobalRef(env, (jobject) lua_touserdata(L, 1));
    if (!*user_data) {
        throw_error("Error: Failed to create global reference");
        lua_error(L);
    }
    lua_setmetatable(L, -2);

    lua_pushcclosure(L, call_func_value, 1);
    return 1;
}


