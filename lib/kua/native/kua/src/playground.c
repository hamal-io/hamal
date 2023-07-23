#include <stdio.h>
#include <lua.h>
#include "lauxlib.h"

#include "kua_memory.h"

int main(void) {
    lua_State *L = luaL_newstate();
    memory_arena_init(10000);
    lua_setallocf(L, memory_arena_reallocate, L);

    lua_createtable(L, 0, 0);

//    jint total, used;
//    getluamemory(obj, &total, &used);

    lua_close(L);
    return 0;
}