#include <lua.h>
#include <lauxlib.h>
#include <lualib.h>

#include "kua_builtin.h"
#include "kua_builtin_base.h"
#include "kua_builtin_decimal.h"
#include "kua_builtin_error.h"

static const luaL_Reg standard_builtin[] = {
        {LUA_GNAME,           builtin_base_register},
        {KUA_BUILTIN_DECIMAL, builtin_decimal_register},
        {LUA_TABLIBNAME,      luaopen_table},
        {NULL, NULL}
};

void
builtin_register(lua_State *L) {
    builtin_error_register(L);

    const luaL_Reg *lib;
    for (lib = standard_builtin; lib->func; lib++) {
        luaL_requiref(L, lib->name, lib->func, 1);
        lua_pop(L, 1);
    }
}