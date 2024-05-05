#include <lua.h>
#include <lauxlib.h>
#include "lbaselib.h"

#include "kua_jni_error.h"
#include "kua_builtin_base.h"

#define luai_likely(x)        (__builtin_expect(((x) != 0), 1))
#define luai_unlikely(x)    (__builtin_expect(((x) != 0), 0))

static int
kua_assert(lua_State* L)
{
	if (luai_likely(lua_toboolean(L, 1)))  /* condition is true? */
		return lua_gettop(L);  /* return all arguments */
	else
	{  /* error */
		luaL_checkany(L, 1);  /* there must be a condition */
		lua_remove(L, 1);  /* remove it */
		lua_pushliteral(L, "assertion failed!");  /* default message */
		lua_settop(L, 1);  /* leave only message (default if no other one) */

		lua_Debug ar;
		lua_getstack(L, 1, &ar);
		lua_getinfo(L, "nSl", &ar);
		int line = ar.currentline;

		char buffer[50];
		snprintf(buffer, 50, "Line %d: assertion failed!", line);
		throw_error_assert(buffer);
		return lua_error(L);
	}
}

static int
kua_error_internal(lua_State* L)
{
	char const* error_c_str = lua_tostring(L, -1);
	throw_error_internal(error_c_str);
	return lua_error(L);
}

static int
kua_error_not_found(lua_State* L)
{
	char const* error_c_str = lua_tostring(L, -1);
	throw_error_not_found(error_c_str);
	return lua_error(L);
}

static int
kua_error_illegal_argument(lua_State* L)
{
	char const* error_c_str = lua_tostring(L, -1);
	throw_error_illegal_argument(error_c_str);
	return lua_error(L);
}

static int
kua_error_invalid_state(lua_State* L)
{
	char const* error_c_str = lua_tostring(L, -1);
	throw_error_invalid_state(error_c_str);
	return lua_error(L);
}


static int
kua_type(lua_State* L)
{
	int t = lua_type(L, 1);
	luaL_argcheck(L, t != LUA_TNONE, 1, "value expected");
	enum type type = type_at(L, 1);
	lua_pushstring(L, typename(type));
	return 1;
}

static int
kua_tostring(lua_State* L)
{
	luaL_checkany(L, 1);
	luaL_tolstring(L, 1, NULL);
	return 1;
}


static const luaL_Reg base_funcs[] = {
		{ "assert",                     kua_assert },
		{ "__error_internal__",         kua_error_internal },
		{ "__error_not_found__",        kua_error_not_found },
		{ "__error_illegal_argument__", kua_error_illegal_argument },
		{ "__error_invalid_state__",    kua_error_invalid_state },
		{ "next",                       luaB_next },
		{ "pairs",                      luaB_pairs },
		{ "ipairs",                     luaB_ipairs },
		{ "print",                      luaB_print }, // FIXME replace with some logger
		{ "getmetatable",               luaB_getmetatable },
		{ "setmetatable",               luaB_setmetatable },
		{ "rawequal",                   luaB_rawequal },
		{ "rawlen",                     luaB_rawlen },
		{ "rawget",                     luaB_rawget },
		{ "rawset",                     luaB_rawset },
		{ "select",                     luaB_select },
		{ "type",                       kua_type },
		{ "tostring",                   kua_tostring },
		{ NULL, NULL }
};


int
builtin_base_register(lua_State* L)
{
	/* open lib into global table */
	lua_pushglobaltable(L);
	luaL_setfuncs(L, base_funcs, 0);
	/* set global _G */
	lua_pushvalue(L, -1);
	lua_setfield(L, -2, LUA_GNAME);
	return 1;
}