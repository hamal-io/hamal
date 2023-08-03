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

static int
kua_error(lua_State *L) {
    int level = (int) luaL_optinteger(L, 2, 1);
    lua_settop(L, 1);
    if (lua_type(L, 1) == LUA_TSTRING && level > 0) {
        luaL_where(L, level);   /* add extra information */
        lua_pushvalue(L, 1);
        lua_concat(L, 2);
    }

    char const *error_c_str = lua_tostring(L, -1);
    throw_script_error(error_c_str);
    return lua_error(L);
}


static int
kua_type(lua_State *L) {
    int t = lua_type(L, 1);
    luaL_argcheck(L, t != LUA_TNONE, 1, "value expected");
    lua_pushstring(L, lua_typename(L, t));
    return 1;
}

static int
kua_tostring(lua_State *L) {
    luaL_checkany(L, 1);
    luaL_tolstring(L, 1, NULL);
    return 1;
}


static const luaL_Reg base_funcs[] = {
        {"assert", kua_assert},
        {"error", kua_error},
        {"next", luaB_next},
        {"pairs", luaB_pairs},
        {"ipairs",         luaB_ipairs},
        {"print", luaB_print}, // FIXME replace with some logger
        {"getmetatable", luaB_getmetatable},
        {"setmetatable", luaB_setmetatable},
        {"rawequal",       luaB_rawequal},
        {"rawlen",         luaB_rawlen},
        {"rawget",         luaB_rawget},
        {"rawset",         luaB_rawset},
        {"select",         luaB_select},
        {"type", kua_type},
        {"tostring", kua_tostring},
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