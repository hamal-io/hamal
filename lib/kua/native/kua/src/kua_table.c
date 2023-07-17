#include "kua_table.h"
#include "kua_check.h"

int
table_create(lua_State *L, int arrayCount, int recordsCount) {
    if (check_argument(arrayCount >= 0, "Array count must not be negative") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_argument(recordsCount >= 0, "Records count must not be negative") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_createtable(L, arrayCount, recordsCount);
    return lua_gettop(L);
}

int
table_raw_len(lua_State *L, int idx) {
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    int counter = 0;
    lua_pushnil(L);
    while (lua_next(L, 1) != 0) {
        counter++;
        lua_pop(L, 1);
    }
    return counter;
}

int
table_set(lua_State *L, int idx, char const *key) {
    //    if (checkstack(L, JNLUA_MINSTACK)
//        && checktype(L, index, LUA_TTABLE)
//        && (setfield_k = getstringchars(k))) {
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_setfield(L, idx, key);
    return 0; //FIXME table size
}

int
table_get(lua_State *L, int idx, char const *key) {
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_getfield(L, idx, key);
    return 0;
}
