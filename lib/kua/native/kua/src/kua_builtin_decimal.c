#include "lmpdecimal.h"

int
kua_builtin_decimal_register(lua_State *L) {
    return luaopen_mpdecimal(L);
}