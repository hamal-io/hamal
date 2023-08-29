/*
* lmpdecimal.c
* high-precision mathematical library for Lua based on mpdecimal
* Luiz Henrique de Figueiredo <lhf@tecgraf.puc-rio.br>
* 04 Jun 2021 21:39:05
* This code is hereby placed in the public domain and also under the MIT license
*/

#include "lmpdecimal.h"

#include "../include/mpdecimal.h"

#include "lua.h"
#include "lauxlib.h"
#include "../include/mycompat.h"

#define MYNAME        "mpdecimal"
#define MYVERSION    MYNAME " library for " LUA_VERSION " / June 2021 / "\
            "using mpdecimal " MPD_VERSION
#define MYTYPE        MYNAME " number"

static mpd_context_t *myctx = NULL;
static lua_State *LL = NULL;

static void traphandler(mpd_context_t *ctx) {
    char s[MPD_MAX_FLAG_STRING];
    mpd_snprint_flags(s, sizeof(s), ctx->status);
    luaL_error(LL, "(mpdecimal) %s", s);
}

static mpd_context_t *Pctx(lua_State *L) {
    LL = L;
    return myctx;
}

static mpd_t *Pnew(lua_State *L) {
    mpd_context_t *ctx = Pctx(L);
    mpd_t *x = mpd_new(ctx);
    luaL_boxpointer(L, x);
    luaL_setmetatable(L, MYTYPE);
    return x;
}

static mpd_t *Pget(lua_State *L, int i) {
    luaL_checkany(L, i);
    switch (lua_type(L, i)) {
        case LUA_TNUMBER:
        case LUA_TSTRING: {
            mpd_context_t *ctx = Pctx(L);
            mpd_t *x = Pnew(L);
            mpd_set_string(x, lua_tostring(L, i), ctx);
            lua_replace(L, i);
            return x;
        }
        default:
            return luaL_unboxpointer(L, i, MYTYPE);
    }
    return NULL;
}

static int Lnew(lua_State *L)            /** new(x) */
{
    Pget(L, 1);
    lua_settop(L, 1);
    return 1;
}

static int Pdo1(lua_State *L, void (*f)(mpd_t *z, const mpd_t *x, mpd_context_t *ctx)) {
    mpd_context_t *ctx = Pctx(L);
    mpd_t *x = Pget(L, 1);
    mpd_t *z = Pnew(L);
    f(z, x, ctx);
    return 1;
}

static int Pdo2(lua_State *L, void (*f)(mpd_t *z, const mpd_t *x, const mpd_t *y, mpd_context_t *ctx)) {
    mpd_context_t *ctx = Pctx(L);
    mpd_t *x = Pget(L, 1);
    mpd_t *y = Pget(L, 2);
    mpd_t *z = Pnew(L);
    f(z, x, y, ctx);
    return 1;
}

static int Lcontext(lua_State *L)                /** context(s) */
{
    static const char *const options[] = {
            "basic", "default", "max", "ieee32", "ieee64", "ieee128"};
    int op = luaL_checkoption(L, 1, "default", options);
    mpd_context_t *ctx = Pctx(L);
    switch (op) {
        case 0:
            mpd_basiccontext(ctx);
            break;
        case 1:
            mpd_defaultcontext(ctx);
            break;
        case 2:
            mpd_maxcontext(ctx);
            break;
        case 3:
            mpd_ieee_context(ctx, 32);
            break;
        case 4:
            mpd_ieee_context(ctx, 64);
            break;
        case 5:
            mpd_ieee_context(ctx, 128);
            break;
    }
    return 0;
}

static int Ldigits(lua_State *L)                /** digits([n]) */
{
    mpd_context_t *ctx = Pctx(L);
    int n = mpd_getprec(ctx);
    int p = luaL_optinteger(L, 1, n);
    if (!mpd_qsetprec(ctx, p)) mpd_addstatus_raise(ctx, MPD_Invalid_context);
    lua_pushinteger(L, n);
    return 1;
}

static int Lclass(lua_State *L)            /** class(x) */
{
    mpd_context_t *ctx = Pctx(L);
    mpd_t *x = Pget(L, 1);
    lua_pushstring(L, mpd_class(x, ctx));
    mpd_print(x);
    return 1;
}

static int Lisfinite(lua_State *L)        /** isfinite */
{
    mpd_t *x = Pget(L, 1);
    lua_pushboolean(L, mpd_isfinite(x));
    return 1;
}

static int Lisinf(lua_State *L)            /** isinf(x) */
{
    mpd_t *x = Pget(L, 1);
    lua_pushboolean(L, mpd_isinfinite(x));
    return 1;
}

static int Lisnan(lua_State *L)            /** isnan(x) */
{
    mpd_t *x = Pget(L, 1);
    lua_pushboolean(L, mpd_isnan(x));
    return 1;
}

static int Lisinteger(lua_State *L)        /** isinteger(x) */
{
    mpd_t *x = Pget(L, 1);
    lua_pushboolean(L, mpd_isinteger(x));
    return 1;
}

static int Lsign(lua_State *L)            /** sign(x) */
{
    mpd_t *x = Pget(L, 1);
    lua_pushinteger(L, mpd_iszero(x) ? 0 : mpd_arith_sign(x));
    return 1;
}

static int Pcompare(lua_State *L) {
    mpd_context_t *ctx = Pctx(L);
    mpd_t *x = Pget(L, 1);
    mpd_t *y = Pget(L, 2);
    return mpd_cmp(x, y, ctx);
}

static int Lcompare(lua_State *L)        /** compare(x,y) */
{
    lua_pushinteger(L, Pcompare(L));
    return 1;
}

static int Leq(lua_State *L) {
    lua_pushboolean(L, Pcompare(L) == 0);
    return 1;
}

static int Lle(lua_State *L) {
    lua_pushboolean(L, Pcompare(L) <= 0);
    return 1;
}

static int Llt(lua_State *L) {
    lua_pushboolean(L, Pcompare(L) < 0);
    return 1;
}

static int Lquotrem(lua_State *L)        /** quotrem(x,y) */
{
    mpd_context_t *ctx = Pctx(L);
    mpd_t *x = Pget(L, 1);
    mpd_t *y = Pget(L, 2);
    mpd_t *q = Pnew(L);
    mpd_t *r = Pnew(L);
    mpd_divmod(q, r, x, y, ctx);
    return 2;
}

static int Lscaleb(lua_State *L)        /** scaleb(x,y) */
{
    mpd_context_t *ctx = Pctx(L);
    mpd_t *x = Pget(L, 1);
    mpd_t *y = Pget(L, 2);
    mpd_t *z = Pnew(L);
    mpd_scaleb(z, x, y, ctx);
    return 1;
}

static int Lfma(lua_State *L)            /** fma(x,y,z) */
{
    mpd_context_t *ctx = Pctx(L);
    mpd_t *x = Pget(L, 1);
    mpd_t *y = Pget(L, 2);
    mpd_t *z = Pget(L, 3);
    mpd_t *w = Pnew(L);
    mpd_fma(w, x, y, z, ctx);
    return 1;
}

static int Lformat(lua_State *L)        /** format(x,f) */
{
    mpd_context_t *ctx = Pctx(L);
    mpd_t *x = Pget(L, 1);
    const char *f = luaL_checkstring(L, 2);
    char *s = mpd_format(x, f, ctx);
    lua_pushstring(L, s);
    free(s);
    return 1;
}

static int Ltostring(lua_State *L)        /** tostring(x) */
{
    mpd_t *x = Pget(L, 1);
    char *s = mpd_to_sci(x, 0);
    lua_pushstring(L, s);
    free(s);
    return 1;
}

static int Ltonumber(lua_State *L)        /** tonumber(x) */
{
    Ltostring(L);
    lua_pushnumber(L, lua_tonumber(L, -1));
    return 1;
}

static int Lgc(lua_State *L) {
    mpd_t *x = Pget(L, 1);
    mpd_del(x);
    lua_pushnil(L);
    lua_setmetatable(L, 1);
    return 0;
}

#define mpd_idiv    mpd_divint
#define mpd_invsqrt    mpd_invroot
#define mpd_mod        mpd_rem
#define mpd_neg        mpd_minus
#define mpd_next    mpd_next_plus
#define mpd_nextafter    mpd_next_toward
#define mpd_prev    mpd_next_minus
#define mpd_copysign    mpd_copy_sign

#define DO(d, f)    static int L##f(lua_State *L) { return d(L,mpd_##f); }
#define DO1(f)    DO(Pdo1,f)
#define DO2(f)    DO(Pdo2,f)

DO1(abs)                    /** abs(x) */
DO1(ceil)                    /** ceil(x) */
DO1(exp)                    /** exp(x) */
DO1(floor)                    /** floor(x) */
DO1(invsqrt)                    /** invsqrt(x) */
DO1(ln)                        /** ln(x) */
DO1(log10)                    /** log10(x) */
DO1(logb)                    /** logb(x) */
DO1(neg)                    /** neg(x) */
DO1(next)                    /** next(x) */
DO1(prev)                    /** prev(x) */
DO1(sqrt)                    /** sqrt(x) */
DO1(trunc)                    /** trunc(x) */
DO2(add)                    /** add(x,y) */
DO2(copysign)                    /** copysign(x,y) */
DO2(div)                    /** div(x,y) */
DO2(idiv)                    /** idiv(x,y) */
DO2(max)                    /** max(x,y) */
DO2(min)                    /** min(x,y) */
DO2(mod)                    /** mod(x,y) */
DO2(mul)                    /** mul(x,y) */
DO2(nextafter)                    /** nextafter(x) */
DO2(pow)                    /** pow(x) */
DO2(sub)                    /** sub(x,y) */

static const luaL_Reg R[] =
        {
                {"__add", Ladd},        /** __add(x,y) */
                {"__div", Ldiv},        /** __div(x,y) */
                {"__eq", Leq},        /** __eq(x,y) */
                {"__gc", Lgc},
                {"__idiv", Lidiv},        /** __idiv(x,y) */
                {"__le", Lle},        /** __le(x,y) */
                {"__lt", Llt},        /** __lt(x,y) */
                {"__mod", Lmod},        /** __mod(x,y) */
                {"__mul", Lmul},        /** __mul(x,y) */
                {"__pow", Lpow},        /** __pow(x,y) */
                {"__sub", Lsub},        /** __sub(x,y) */
                {"__tostring", Ltostring},        /** __tostring(x) */
                {"__unm", Lneg},        /** __unm(x) */
                {"log", Lln},        /** log(x) */
#define DECLARE(f)    { #f, L##f },
                DECLARE(abs)
                DECLARE(add)
                DECLARE(ceil)
                DECLARE(class)
                DECLARE(compare)
                DECLARE(context)
                DECLARE(copysign)
                DECLARE(digits)
                DECLARE(div)
                DECLARE(exp)
                DECLARE(floor)
                DECLARE(fma)
                DECLARE(format)
                DECLARE(idiv)
                DECLARE(invsqrt)
                DECLARE(isfinite)
                DECLARE(isinf)
                DECLARE(isinteger)
                DECLARE(isnan)
                DECLARE(ln)
                DECLARE(log10)
                DECLARE(logb)
                DECLARE(max)
                DECLARE(min)
                DECLARE(mod)
                DECLARE(mul)
                DECLARE(neg)
                DECLARE(new)
                DECLARE(next)
                DECLARE(nextafter)
                DECLARE(pow)
                DECLARE(prev)
                DECLARE(quotrem)
                DECLARE(scaleb)
                DECLARE(sign)
                DECLARE(sqrt)
                DECLARE(sub)
                DECLARE(tonumber)
                DECLARE(tostring)
                DECLARE(trunc)
                {NULL, NULL}
        };

LUALIB_API int luaopen_mpdecimal(lua_State *L) {
    static mpd_context_t ctx;
    myctx = &ctx;
    mpd_init(myctx, 30);
    mpd_traphandler = traphandler;
    luaL_newmetatable(L, MYTYPE);
    luaL_setfuncs(L, R, 0);
    lua_pushliteral(L, "version");            /** version */
    lua_pushliteral(L, MYVERSION);
    lua_settable(L, -3);
    lua_pushliteral(L, "__index");
    lua_pushvalue(L, -2);
    lua_settable(L, -3);
    return 1;
}
