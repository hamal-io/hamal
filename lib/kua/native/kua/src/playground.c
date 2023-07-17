#include <stdio.h>
#include <lua.h>
#include "lauxlib.h"

int count(lua_State *L, int idx) {
    int counter = 0;
    lua_pushnil(L);
    while (lua_next(L, 1) != 0) {
        counter++;
        lua_pop(L, 1);
    }
    printf("%d\n", counter);
    return counter;
}

int main(void) {
    lua_State *L = luaL_newstate();

    lua_createtable(L, 0, 0);
    lua_pushnumber(L, 123);
    lua_setfield(L, 1, "test");

    count(L, 1);

    return 0;
}