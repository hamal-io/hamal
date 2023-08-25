#include <stdio.h>
#include <lua.h>
#include "lauxlib.h"

#include "kua_common.h"
#include "kua_memory.h"

int main(void) {
    lua_State *L = luaL_newstate();

    luaL_newmetatable(L, "__metatable_error");
    lua_pushnumber(L, 20);
    lua_setfield(L, 1, "__type_id");
    lua_pushstring(L, "error");
    lua_setfield(L, 1, "__typename");
    lua_pop(L, 1);

    lua_createtable(L, 0, 0);
    lua_pushstring(L, "error message");
    lua_setfield(L, 1, "value");

    printf("top: %d\n", lua_gettop(L));

    luaL_getmetatable(L, "__metatable_error");
    printf("top: %d\n", lua_gettop(L));

    lua_setmetatable(L, 1);


    lua_close(L);
    return 0;
}