#include "lmpdecimal.h"
#include "mpdecimal.h"

#include "lua.h"
#include "lauxlib.h"
#include "kua_memory.h"

#include "kua_jni_error.h"
#include "kua_builtin_decimal.h"


#define luaL_boxpointer(L, u) (*(void **)(lua_newuserdata(L, sizeof(void *))) = (u))

#define luaL_unboxpointer(L, i, t) *((void**)luaL_checkudata(L,i,t))

static void
trap_handler(mpd_context_t *ctx) {
    char err_msg[MPD_MAX_FLAG_STRING];
    mpd_snprint_flags(err_msg, sizeof(err_msg), ctx->status);
//    luaL_error(LL, "(mpdecimal) %err_msg", err_msg);
    throw_error(err_msg); //FIXME maybe own error class? DecimalError?!
}

static mpd_context_t *
current_context(lua_State *L) {
//    lua_getme
    printf("ctx\n");

}


static mpd_context_t *
mpdecimal_get_context(lua_State *L) {
    lua_getglobal(L, "__mpdecimal_ctx__");
    // FIXME check context
    return (mpd_context_t *) lua_touserdata(L, -1);
}

static mpd_t *
Pnew(lua_State *L) {
    mpd_context_t *ctx = mpdecimal_get_context(L);
    mpd_t *x = mpd_new(ctx);
    luaL_boxpointer(L, x);
    luaL_setmetatable(L, KUA_BUILTIN_DECIMAL);
    return x;
}


static mpd_t *
Pget(lua_State *L, int i) {
    luaL_checkany(L, i);
    switch (lua_type(L, i)) {
        case LUA_TNUMBER:
        case LUA_TSTRING: {
            mpd_context_t *ctx = mpdecimal_get_context(L);
            mpd_t *x = Pnew(L);
            mpd_set_string(x, lua_tostring(L, i), ctx);
            lua_replace(L, i);
            return x;
        }
        default:
            return luaL_unboxpointer(L, i, KUA_BUILTIN_DECIMAL);
    }
}


static int
decimal_new(lua_State *L) {

//    mpd_context_t *ctx = lua_newuserdata(L, sizeof(mpd_context_t));
//    ctx = lua_newuserdata(L, sizeof(mpd_context_t));
//    mpd_init(ctx, 30);

    mpd_context_t *ctx = mpdecimal_get_context(L);

    mpd_t *x = mpd_new(ctx);
    luaL_boxpointer(L, x);
    luaL_setmetatable(L, KUA_BUILTIN_DECIMAL);

    mpd_set_string(x, lua_tostring(L, 1), ctx);
    lua_replace(L, 1);


//    Pget(L, 1);
    lua_settop(L, 1);
    return 1;
}

static int
decimal_tostring(lua_State *L) {
    mpd_t *x = Pget(L, 1);
    char *s = mpd_to_sci(x, 0);
    lua_pushstring(L, s);
    free(s);
    return 1;
}

static const luaL_Reg R[] = {
        {"new",        decimal_new},
        {"__tostring", decimal_tostring},
        {NULL, NULL}
};


int
builtin_decimal_register(lua_State *L) {
    mpd_traphandler = trap_handler;

    mpd_context_t *ctx = lua_newuserdata(L, sizeof(mpd_context_t));
    mpd_init(ctx, 30);
    lua_setglobal(L, "__mpdecimal_ctx__");

//    lua_newtable(L);
//    lua_pushstring(L, "__ctx");
//    lua_pushlightuserdata(L, ctx);
//    lua_settable(L, -3);
//    lua_setglobal(L, KUA_BUILTIN_DECIMAL);
//
//    luaL_setfuncs(L, R, 0);
//    lua_pushliteral(L, "__index");
//    lua_pushvalue(L, -2);
//    lua_settable(L, -3);
//
//
//

//    current_context(L);


    luaL_newmetatable(L, KUA_BUILTIN_DECIMAL);
    luaL_setfuncs(L, R, 0);
    lua_pushliteral(L, "__index");
    lua_pushvalue(L, -2);
    lua_settable(L, -3);

    lua_pushstring(L, "__type_id");
    lua_pushnumber(L, 10);
    lua_settable(L, -3);

    lua_pushstring(L, "__typename");
    lua_pushstring(L, "decimal");
    lua_settable(L, -3);

    return 1;
}