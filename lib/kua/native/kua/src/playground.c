#include <stdio.h>
#include <lua.h>
#include "lauxlib.h"

#include "kua_common.h"
#include "kua_memory.h"
#include "kua_builtin_error.h"

int main(void) {
    lua_State *L = luaL_newstate();

    luaL_newmetatable(L, KUA_BUILTIN_ERROR);
    lua_pushnumber(L, ERROR_TYPE);
    lua_setfield(L, 1, "__type_id");
    lua_pushstring(L, "error");
    lua_setfield(L, 1, "__typename");
    lua_pop(L, 1);

    printf("%d\n", lua_gettop(L));

    lua_createtable(L, 0, 2);
    lua_pushstring(L, "ERR");
    lua_setfield(L, -2, "message");

    luaL_setmetatable(L, KUA_BUILTIN_ERROR);

    printf("%d\n", lua_gettop(L));

    if (lua_getmetatable(L, 1)) {
        printf("%d\n", lua_gettop(L));
        luaL_getmetafield(L, 1, "__typename");
        printf("%d\n", lua_gettop(L));
//        printf("%d\n", lua_tonumber(L, 3));
        printf("%s\n", lua_tostring(L, -1));

    }

//    lua_pop(L, 1);
//
//    lua_createtable(L, 0, 0);
//    lua_pushstring(L, "error message");
//    lua_setfield(L, 1, "value");
//
//    printf("top: %d\n", lua_gettop(L));
//
//    luaL_getmetatable(L, KUA_BUILTIN_ERROR);
//    printf("top: %d\n", lua_gettop(L));
//
//    lua_setmetatable(L, 1);


    lua_close(L);
    return 0;
}