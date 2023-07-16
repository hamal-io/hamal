#include <lua.h>

#include "kua_check.h"
#include "kua_stack.h"

int
type(lua_State *L, int idx) {
    if (check_argument(idx != 0, "Index must not be 0") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_type(L, idx);
}

int
top(lua_State *L) {
    return lua_gettop(L);
}

int
push(lua_State *L, int idx) {
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