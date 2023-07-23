#include <stdio.h>
#include <lua.h>
#include "lauxlib.h"

int count(lua_State *L, int idx) {
    int counter = 0;
    lua_pushnil(L);
    while (lua_next(L, idx) != 0) {
        counter++;
        lua_pop(L, 1);
    }
    printf("%d\n", counter);
    return counter;
}

int main(void) {
    lua_State *L = luaL_newstate();

    lua_close(L);
    return 0;
}