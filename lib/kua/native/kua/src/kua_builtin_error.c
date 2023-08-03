#include <lua.h>
#include "lauxlib.h"

#include "kua_check.h"
#include "kua_builtin_error.h"

void
builtin_error_register_metable(lua_State *L) {
    luaL_newmetatable(L, "__metable_error");
    lua_pushnumber(L, 20);
    lua_setfield(L, 1, "__type");
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

    luaL_getmetatable(L, "__metable_error");
    lua_setmetatable(L, -2);
    return LUA_OK;
}