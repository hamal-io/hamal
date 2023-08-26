#include "mpdecimal.h"

#include "lua.h"
#include "lauxlib.h"

#include "kua_check.h"
#include "kua_jni_error.h"
#include "kua_builtin_decimal.h"

/*
* based on lmpdecimal.c - https://web.tecgraf.puc-rio.br/~lhf/ftp/lua
* high-precision mathematical library for Lua based on mpdecimal
* Luiz Henrique de Figueiredo <lhf@tecgraf.puc-rio.br>
* 04 Jun 2021 21:39:05
* This code is hereby placed in the public domain and also under the MIT license
*/

#define luaL_boxpointer(L, u) (*(void **)(lua_newuserdata(L, sizeof(void *))) = (u))
#define luaL_unboxpointer(L, i, t) *((void**)luaL_checkudata(L,i,t))
#define MPDECIMAL_CTX_NAME "__mpdecimal_ctx__"

static void
trap_handler(mpd_context_t *ctx) {
    char err_msg[MPD_MAX_FLAG_STRING];
    mpd_snprint_flags(err_msg, sizeof(err_msg), ctx->status);
    throw_error(err_msg); //FIXME maybe own error class? DecimalError?!
}

static mpd_context_t global_ctx;

static mpd_t *
mpdecimal_new(lua_State *L) {
    mpd_t *x = mpd_new(&global_ctx);
    luaL_boxpointer(L, x);
    luaL_setmetatable(L, KUA_BUILTIN_DECIMAL);
    return x;
}

static mpd_t *
mpdecimal_get(lua_State *L, int idx) {
    luaL_checkany(L, idx);
    switch (lua_type(L, idx)) {
        case LUA_TNUMBER:
        case LUA_TSTRING: {
            mpd_t *x = mpdecimal_new(L);
            mpd_set_string(x, lua_tostring(L, idx), &global_ctx);
            lua_replace(L, idx);
            return x;
        }
        default:
            return luaL_unboxpointer(L, idx, KUA_BUILTIN_DECIMAL);
    }
}

static int
mpdecimal_function(lua_State *L, void (*fn)(mpd_t *z, const mpd_t *x, mpd_context_t *ctx)) {
    mpd_t *x = mpdecimal_get(L, 1);
    mpd_t *z = mpdecimal_new(L);
    fn(z, x, &global_ctx);
    return 1;
}

static int
mpdecimal_bifunction(lua_State *L, void (*fn)(mpd_t *z, const mpd_t *x, const mpd_t *y, mpd_context_t *ctx)) {
    mpd_t *x = mpdecimal_get(L, 1);
    mpd_t *y = mpdecimal_get(L, 2);
    mpd_t *z = mpdecimal_new(L);
    fn(z, x, y, &global_ctx);
    return 1;
}

int
decimal_new(lua_State *L) {
    mpdecimal_get(L, 1);
    luaL_setmetatable(L, KUA_BUILTIN_DECIMAL);
    lua_settop(L, 1);
    return 1;
}

int
decimal_new_from_string(lua_State *L, char const *value) {
    mpd_t *x = mpdecimal_new(L);
    mpd_set_string(x, value, &global_ctx);
    luaL_setmetatable(L, KUA_BUILTIN_DECIMAL);
    return 1;
}


int
decimal_as_string(lua_State *L, int idx) {
    mpd_t *x = mpdecimal_get(L, idx);
    char *s = mpd_to_sci(x, 0);
    lua_pushstring(L, s);
    free(s);
    return 1;
}

static int
decimal_tostring(lua_State *L) {
    return decimal_as_string(L, 1);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

#define DO(d, f)    static int decimal_##f(lua_State *L) { return d(L,mpd_##f); }
#define FUNCTION(f)    DO(mpdecimal_function,f)
#define BI_FUNCTION(f)    DO(mpdecimal_bifunction,f)

BI_FUNCTION(div)                    /** div(x,y) */

#undef DO
#undef FUNCTION
#undef BI_FUNCTION

static const luaL_Reg R[] = {
        {"new", decimal_new},
        {"__div", decimal_div},
        {"__tostring", decimal_tostring},
#define DECLARE(f)    { #f, decimal_##f },
        DECLARE(div)
        DECLARE(tostring)
        {NULL, NULL}
#undef DECLARE
};

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


int
builtin_decimal_register(lua_State *L) {
    mpd_ieee_context(&global_ctx, 128);

    luaL_newmetatable(L, KUA_BUILTIN_DECIMAL);
    luaL_setfuncs(L, R, 0);
    lua_pushliteral(L, "__index");
    lua_pushvalue(L, -2);
    lua_settable(L, -3);

    lua_pushstring(L, "__type_id");
    lua_pushnumber(L, DECIMAL_TYPE);
    lua_settable(L, -3);

    lua_pushstring(L, "__typename");
    lua_pushstring(L, "decimal");
    lua_settable(L, -3);

    return 1;
}