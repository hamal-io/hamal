#include <lua.h>
#include <lauxlib.h>
#include <lualib.h>

#include "kua_builtin.h"
#include "kua_builtin_base.h"

static const luaL_Reg standard_builtin[] = {
        {LUA_GNAME,      kua_lib_base_register},
        {LUA_DBLIBNAME,  luaopen_debug},
        {LUA_TABLIBNAME, luaopen_table},
        {NULL, NULL}
};

void
kua_builtin_register(lua_State *L) {
    const luaL_Reg *lib;
    for (lib = standard_builtin; lib->func; lib++) {
        luaL_requiref(L, lib->name, lib->func, 1);
        lua_pop(L, 1);
    }
}