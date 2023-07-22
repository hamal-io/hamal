#include <lua.h>
#include <lauxlib.h>
#include <jni.h>

#include "kua_call.h"
#include "kua_jni.h"
#include "kua_jni_error.h"
#include "kua_state.h"

enum result
call(lua_State *L, int argsCount, int resultCount) {
    //FIXME ensure its actually a function
//    if (checkarg(argsCount >= 0, "illegal argument count")
//        && checknelems(L, argsCount + 1)
//        && checkarg(resultCount >= 0 || resultCount == LUA_MULTRET, "illegal return count")
//        && (resultCount == LUA_MULTRET || checkstack(L, resultCount - (argsCount + 1)))) {
    if (lua_pcall(L, argsCount, resultCount, 0) != LUA_OK) {
        //FIXME throw exception
//        luaL_error(L, "error running function `f': %s", lua_tostring(L, -1));
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

    jthrowable throwable;

    JNIEnv *env = current_env();
    int nresults = (*env)->CallIntMethod(env, func_to_call, jni_ref().invoked_by_lua_method_id, kstate);


//    throwable = (*env)->ExceptionOccurred(env);
//    if (throwable) {
//        printf("EXCEPTION occurred\n");
//        /* Push exception & clear */
////        luaL_where(L, 1);
////        where = tostring(L, -1);
////        luaerror = (*thread_env)->NewObject(thread_env, luaerror_class, luaerror_id, where, throwable);
////        if (luaerror) {
////            pushjavaobject(L, luaerror);
////        } else {
////            lua_pushliteral(L, "JNI error: NewObject() failed creating Lua error");
////        }
////        (*env)->ExceptionClear(env);
//
//        /* Error out */
//        return lua_error(L);
//    }

    return nresults;
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


