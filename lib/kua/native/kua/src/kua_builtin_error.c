#include <lua.h>
#include "lauxlib.h"

#include "kua_check.h"
#include "kua_builtin_error.h"

void
builtin_error_register(lua_State *L) {
    luaL_newmetatable(L, KUA_BUILTIN_ERROR);

    lua_pushstring(L, "__type_id");
    lua_pushnumber(L, 20);
    lua_settable(L, -3);

    lua_pushstring(L, "__typename");
    lua_pushstring(L, "error");
    lua_settable(L, -3);
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