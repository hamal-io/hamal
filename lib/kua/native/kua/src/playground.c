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

    lua_createtable(L, 0, 4);
    int tbl = lua_gettop(L);

    lua_pushstring(L, "key");
    lua_pushstring(L, "value");
    lua_rawset(L, tbl);
    printf("%d\n", lua_gettop(L));


    lua_pushstring(L, "key");
    lua_rawget(L, 1);
    printf("%d\n", lua_gettop(L));
    printf("%d\n", lua_type(L, 2));
    printf("%s\n", lua_tostring(L, 2));

//    lua_pushinteger(L, 23);
//    lua_pushstring(L, "context");
//    lua_rawset(L, tbl);
//
//    lua_pushstring(L, "value");
//    lua_pushinteger(L, 24);
//    lua_rawset(L, tbl);
//
//    lua_pushstring(L, "player");
//    lua_pushstring(L, "s->player");
//    lua_rawset(L, tbl);

//    count(L, 1);

    return 0;
}