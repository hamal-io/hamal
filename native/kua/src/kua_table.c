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
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;

    int absIndex = lua_absindex(L, idx);

    int counter = 0;
    lua_pushnil(L);

    while (table_next(L, absIndex) > 0) {
        counter++;
        lua_pop(L, 1);
    }
    return counter;
}

int
table_get(lua_State *L, int idx) {
    if (check_argument(idx != 0, "Index must not be 0") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_absindex(L, idx);
}

int
table_field_set(lua_State *L, int idx, char const *key) {
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_setfield(L, idx, key);
    return table_len(L, idx);
}

int
table_raw_set(lua_State *L, int idx) {
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_rawset(L, idx);
    return table_len(L, idx);
}

int
table_raw_set_idx(lua_State *L, int stack_idx, int table_idx) {
    if (check_index(L, stack_idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, stack_idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_rawseti(L, stack_idx, table_idx);
    return table_len(L, stack_idx);
}

int
table_field_get(lua_State *L, int idx, char const *key) {
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_getfield(L, idx, key);
    return type_at(L, -1);
}

int
table_raw_get(lua_State *L, int idx) {
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_rawget(L, idx);
    return type_at(L, -1);
}

int
table_raw_get_idx(lua_State *L, int stack_idx, int table_idx) {
    if (check_index(L, stack_idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, stack_idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_rawgeti(L, stack_idx, table_idx);
    return type_at(L, -1);
}

int
table_get_sub_table(lua_State *L, int idx, char const *key) {
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    auto result = table_field_get(L, idx, key);
    if (check_type_at(L, -1, TABLE_TYPE) == CHECK_RESULT_ERROR) {
        lua_pop(L, 2);
        return LUA_TNONE;
    }
    return result;
}

int
table_next(lua_State *L, int idx) {
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return lua_next(L, idx);
}

int
table_append(lua_State *L, int idx) {
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_type_at(L, idx, TABLE_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    int len = table_len(L, idx) + 1;
    lua_rawseti(L, idx, len);
    return len;
}
