#include <lua.h>
#include "lauxlib.h"

#include "kua_check.h"
#include "kua_builtin_error.h"

void
builtin_error_register(lua_State *L) {
    luaL_newmetatable(L, KUA_BUILTIN_ERROR);
    lua_pushnumber(L, ERROR_TYPE);
    lua_setfield(L, 1, "__type_id");
    lua_pushstring(L, "error");
    lua_setfield(L, 1, "__typename");
    lua_pop(L, 1);
}


int
builtin_error_create(lua_State *L, char const *message) {
    if (check_stack_overflow(L, 2) == CHECK_RESULT_ERROR) return LUA_TNONE;

    lua_createtable(L, 0, 2);
    lua_pushstring(L, message);
    lua_setfield(L, -2, "message");

    luaL_setmetatable(L, KUA_BUILTIN_ERROR);
    return LUA_OK;
}