#include <lua.h>

#include "kua_kotlin.h"

static int
call_error_handler(lua_State *L) {
    //FIXME implement me
    return 0;
}

int
call(lua_State *L, int argsCount, int resultCount) {
//    if (checkarg(argsCount >= 0, "illegal argument count")
//        && checknelems(L, argsCount + 1)
//        && checkarg(resultCount >= 0 || resultCount == LUA_MULTRET, "illegal return count")
//        && (resultCount == LUA_MULTRET || checkstack(L, resultCount - (argsCount + 1)))) {
    int idx = lua_absindex(L, -argsCount - 1);
    lua_pushcfunction(L, call_error_handler);
    lua_insert(L, idx);
    int status = lua_pcall(L, argsCount, resultCount, idx);
    lua_remove(L, idx);
//    if (status != LUA_OK) {
//        throw(L, status);
//    }
    return status;
}
