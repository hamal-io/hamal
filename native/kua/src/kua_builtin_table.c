
#include "lua.h"
#include "lauxlib.h"

#include "kua_jni_error.h"
#include "kua_builtin_table.h"
#include "luaconf.h"

#define TAB_R    1            /* read */
#define TAB_W    2            /* write */
#define TAB_L    4            /* length */
#define TAB_RW    (TAB_R | TAB_W)        /* read/write */

#define aux_getn(L, n, w)    (checktab(L, n, (w) | TAB_L), luaL_len(L, n))

static int checkfield(lua_State* L, const char* key, int n)
{
	lua_pushstring(L, key);
	return (lua_rawget(L, -n) != LUA_TNIL);
}


/*
** Check that 'arg' either is a table or can behave like one (that is,
** has a metatable with the required metamethods)
*/
static void checktab(lua_State* L, int arg, int what)
{
	if (lua_type(L, arg) != LUA_TTABLE)
	{  /* is it not a table? */
		int n = 1;  /* number of elements to pop */
		if (lua_getmetatable(L, arg) &&  /* must have metatable */
			(!(what & TAB_R) || checkfield(L, "__index", ++n)) &&
			(!(what & TAB_W) || checkfield(L, "__newindex", ++n)) &&
			(!(what & TAB_L) || checkfield(L, "__len", ++n)))
		{
			lua_pop(L, n);  /* pop metatable and tested metamethods */
		}
		else
			luaL_checktype(L, arg, LUA_TTABLE);  /* force an error */
	}
}

static int
table_insert(lua_State* L)
{
	lua_Integer pos;  /* where to insert new element */
	lua_Integer e = aux_getn(L, 1, TAB_RW);
	e = luaL_intop(+, e, 1);  /* first empty element */
	switch (lua_gettop(L))
	{
	case 1:
		break;
	case 2:
	{  /* called with only 2 arguments */
		pos = e;  /* insert new element at the end */
		break;
	}
	case 3:
	{
		lua_Integer i;
		pos = luaL_checkinteger(L, 2);  /* 2nd argument is the position */
		/* check whether 'pos' is in [1, e] */
		luaL_argcheck(L, (lua_Unsigned)pos - 1u < (lua_Unsigned)e, 2, "position out of bounds");
		for (i = e; i > pos; i--)
		{  /* move up elements */
			lua_geti(L, 1, i - 1);
			lua_seti(L, 1, i);  /* t[i] = t[i - 1] */
		}
		break;
	}
	default:
	{
		return luaL_error(L, "wrong number of arguments to 'insert'");
	}
	}
	lua_seti(L, 1, pos);  /* t[pos] = v */
	return 0;
}


static int
table_pack(lua_State* L)
{
	int i;
	int n = lua_gettop(L);  /* number of elements to pack */
	lua_createtable(L, n, 1);  /* create result table */
	lua_insert(L, 1);  /* put it at index 1 */
	for (i = n; i >= 1; i--)  /* assign elements */
		lua_seti(L, 1, i);
	lua_pushinteger(L, n);
	lua_setfield(L, 1, "n");  /* t.n = number of elements */
	return 1;  /* return table */
}


static int
table_unpack(lua_State* L)
{
	lua_Unsigned n;
	lua_Integer i = luaL_optinteger(L, 2, 1);
	lua_Integer e = luaL_opt(L, luaL_checkinteger, 3, luaL_len(L, 1));
	if (i > e) return 0;  /* empty range */
	n = (lua_Unsigned)e - i;  /* number of elements minus 1 (avoid overflows) */
	if (luai_unlikely(n >= (unsigned int)INT_MAX || !lua_checkstack(L, (int)(++n))))
		return luaL_error(L, "too many results to unpack");
	for (; i < e; i++)
	{  /* push arg[i..e - 1] (to avoid overflows) */
		lua_geti(L, 1, i);
	}
	lua_geti(L, 1, e);  /* push last element */
	return (int)n;
}

static const luaL_Reg R[] = {
		{ "__insert", table_insert },
		{ "__pack", table_pack },
		{ "__unpack", table_unpack },

#define DECLARE(f)    { #f, table_##f },
		DECLARE(insert)
		{ NULL, NULL }
#undef DECLARE
};


int
builtin_table_register(lua_State* L)
{

	luaL_newmetatable(L, KUA_BUILTIN_TABLE);
	luaL_setfuncs(L, R, 0);
	lua_pushliteral(L, "__index");
	lua_pushvalue(L, -2);
	lua_settable(L, -3);

	return 1;
}