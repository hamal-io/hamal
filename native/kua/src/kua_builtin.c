#include <lua.h>
#include <lauxlib.h>

#include "kua_builtin.h"
#include "kua_builtin_base.h"
#include "kua_builtin_decimal.h"
#include "kua_builtin_error.h"
#include "kua_builtin_table.h"
#include "lualib.h"

static const luaL_Reg standard_builtin[] = {
	{KUA_BUILTIN_GLOBAL, builtin_base_register},
	{KUA_BUILTIN_DECIMAL, builtin_decimal_register},
	{KUA_BUILTIN_TABLE, builtin_table_register},
	{LUA_MATHLIBNAME, luaopen_math},
	{NULL, NULL}
};

void
builtin_register (lua_State *L)
{
	builtin_error_register (L);

	const luaL_Reg *lib;
	for (lib = standard_builtin; lib->func; lib++)
		{
			luaL_requiref (L, lib->name, lib->func, 1);
			lua_pop(L, 1);
		}
}