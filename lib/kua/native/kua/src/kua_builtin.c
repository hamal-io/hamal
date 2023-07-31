#include <lua.h>
#include <lauxlib.h>

#include "kua_builtin.h"
#include "kua_builtin_base.h"

static const luaL_Reg standard_libs[] = {
        {LUA_GNAME, kua_lib_base_open},
        {NULL, NULL}
};

void
kua_builtin_register(lua_State *L) {
    const luaL_Reg *lib;
    for (lib = standard_libs; lib->func; lib++) {
        luaL_requiref(L, lib->name, lib->func, 1);
        lua_pop(L, 1);
    }
}