#include "kua_check.h"
#include "kua_reference.h"

#include "lauxlib.h"

int
reference_acquire(lua_State *L) {
    if (check_stack_underflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return luaL_ref(L, LUA_REGISTRYINDEX);
}

int
reference_push(lua_State *L, int ref) {
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_rawgeti(L, LUA_REGISTRYINDEX, ref);
}

void
reference_release(lua_State *L, int ref) {
    luaL_unref(L, LUA_REGISTRYINDEX, ref);
}
