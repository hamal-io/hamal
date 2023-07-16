#include "kua_check.h"
#include "kua_stack.h"

int
type(lua_State *L, int idx) {
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_type(L, idx);
}

int
size(lua_State *L) {
    return lua_gettop(L);
}

int
push_nil(lua_State *L) {
    if (check_stack(L) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushnil(L);
    return size(L);
}

int
push_boolean(lua_State *L, int value) {
    if (check_stack(L) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushboolean(L, value);
    return size(L);
}

int
push_number(lua_State *L, double value) {
    if (check_stack(L) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushnumber(L, value);
    return size(L);
}

int
push_string(lua_State *L, char const *value) {
    if (check_stack(L) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_pushstring(L, value);
    return size(L);
}

int
to_boolean(lua_State *L, int idx) {
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, idx, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_toboolean(L, idx);
}

double
to_number(lua_State *L, int idx) {
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, idx, 3) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_tonumber(L, idx);
}

char const *
to_string(lua_State *L, int idx) {
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return NULL;
    if (check_type_at(L, idx, 4) == CHECK_RESULT_ERROR) return NULL;
    return lua_tostring(L, idx);
}