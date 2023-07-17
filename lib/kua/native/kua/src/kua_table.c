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
table_len(lua_State *L, int idx) {
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    int counter = 0;
    lua_pushnil(L);
    while (lua_next(L, idx) != 0) {
        counter++;
        lua_pop(L, 1);
    }
    return counter;
}

int
table_set(lua_State *L, int idx, char const *key) {
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_setfield(L, idx, key);
    return table_len(L, idx);
}

int
table_raw_set(lua_State *L, int idx) {
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_rawset(L, idx);
    return table_len(L, idx);
}

int
table_raw_set_idx(lua_State *L, int stack_idx, int table_idx) {
    if (check_type_at(L, stack_idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_rawseti(L, stack_idx, table_idx);
    return table_len(L, stack_idx);
}

int
table_get(lua_State *L, int idx, char const *key) {
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_getfield(L, idx, key);
}

int
table_raw_get(lua_State *L, int idx) {
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_rawget(L, idx);
}

int
table_raw_get_idx(lua_State *L, int stack_idx, int table_idx) {
    if (check_type_at(L, stack_idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_rawgeti(L, stack_idx, table_idx);
}
