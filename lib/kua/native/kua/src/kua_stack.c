#include <lua.h>

#include "kua_check.h"
#include "kua_error.h"
#include "kua_stack.h"
#include "kua_call.h"

int
type(lua_State *L, int idx) {
    if (check_argument(idx != 0, "Index must not be 0") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    int result = lua_type(L, idx);
    if (result == TABLE_TYPE) {
        lua_getmetatable(L, idx);
        if (lua_type(L, -1) == NIL_TYPE) {
            pop(L, 2);
            return TABLE_TYPE;
        }
        lua_getfield(L, -1, "__type");
        result = TABLE_TYPE;
        if (lua_type(L, -1) == NUMBER_TYPE) {
            result = lua_tonumber(L, -1);
        }
        pop(L, 1);
        return result;
    }
    return result;
}

int
top(lua_State *L) {
    return lua_gettop(L);
}

int
abs_index(lua_State *L, int idx) {
    if (check_argument(idx != 0, "Index must not be 0") == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_absindex(L, idx);
}

void
set_top(lua_State *L, int idx) {
    if (check_stack_overflow(L, idx) == CHECK_RESULT_ERROR) return;
    lua_settop(L, idx);
}

int
push_top(lua_State *L, int idx) {
    if (check_argument(idx != 0, "Index must not be 0") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushvalue(L, idx);
    return top(L);
}

int
pop(lua_State *L, int total) {
    if (check_argument(total > 0, "Total must be positive (>0)") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_underflow(L, total) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pop(L, total);
    return top(L);
}

int
push_nil(lua_State *L) {
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushnil(L);
    return top(L);
}

int
push_error(lua_State *L, char const *message) {
    if (check_stack_overflow(L, 2) == CHECK_RESULT_ERROR) return LUA_TNONE;
    error_create(L, message);
    return top(L);
}

int
push_boolean(lua_State *L, int value) {
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushboolean(L, value);
    return top(L);
}

int
push_number(lua_State *L, double value) {
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushnumber(L, value);
    return top(L);
}

int
push_string(lua_State *L, char const *value) {
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushstring(L, value);
    return top(L);
}

int
push_func_value(lua_State *L, void *func) {
    //FIXME figure required stack size
    if (check_stack_overflow(L, 3) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushcfunction(L, call_func_value_closure);
    lua_pushlightuserdata(L, func);
    lua_pcall(L, 1, 1, 0);
    return top(L);
}

char const *
to_error(lua_State *L, int idx) {
    if (check_argument(idx != 0, "Index must not be 0") == CHECK_RESULT_ERROR) return NULL;
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return NULL;
    if (check_type_at(L, idx, STRING_TYPE) == CHECK_RESULT_ERROR) return NULL;
    return lua_tostring(L, idx);
}

int
to_boolean(lua_State *L, int idx) {
    if (check_argument(idx != 0, "Index must not be 0") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, idx, BOOLEAN_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_toboolean(L, idx);
}


double
to_number(lua_State *L, int idx) {
    if (check_argument(idx != 0, "Index must not be 0") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, idx, NUMBER_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_tonumber(L, idx);
}

char const *
to_string(lua_State *L, int idx) {
    if (check_argument(idx != 0, "Index must not be 0") == CHECK_RESULT_ERROR) return NULL;
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return NULL;
    if (check_type_at(L, idx, STRING_TYPE) == CHECK_RESULT_ERROR) return NULL;
    return lua_tostring(L, idx);
}