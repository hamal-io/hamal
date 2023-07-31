#include <lua.h>
#include <lauxlib.h>
#include "lbaselib.h"

#include "kua_jni_error.h"
#include "kua_builtin_base.h"

#define luai_likely(x)        (__builtin_expect(((x) != 0), 1))
#define luai_unlikely(x)    (__builtin_expect(((x) != 0), 0))

static int
kua_assert(lua_State *L) {
    if (luai_likely(lua_toboolean(L, 1)))  /* condition is true? */
        return lua_gettop(L);  /* return all arguments */
    else {  /* error */
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
        throw_assert_error(buffer);
        return lua_error(L);
    }
}


static const luaL_Reg base_funcs[] = {
        {"assert", kua_assert},
        {"next",   luaB_next},
        {"pairs",  luaB_pairs},
        {"print",  luaB_print}, // FIXME replace with some logger
        {NULL, NULL}
};


int
kua_lib_base_register(lua_State *L) {
    /* open lib into global table */
    lua_pushglobaltable(L);
    luaL_setfuncs(L, base_funcs, 0);
    /* set global _G */
    lua_pushvalue(L, -1);
    lua_setfield(L, -2, LUA_GNAME);
    return 1;
}