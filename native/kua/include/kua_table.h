#ifndef KUA_TABLE_H
#define KUA_TABLE_H

#include "kua_common.h"

int
table_create(lua_State *L, int arrayCount, int recordsCount);

int
table_len(lua_State *L, int idx);

int
table_get(lua_State *L, int idx);

int
table_field_set(lua_State *L, int idx, char const *key);

int
table_raw_set(lua_State *L, int idx);

int
table_raw_set_idx(lua_State *L, int stack_idx, int table_idx);

int
table_field_get(lua_State *L, int idx, char const *key);

int
table_raw_get(lua_State *L, int idx);

int
table_raw_get_idx(lua_State *L, int stack_idx, int table_idx);

int
table_get_sub_table(lua_State *L, int idx, char const *key);

int
table_next(lua_State *L, int idx);

int
table_append(lua_State *L, int idx);

#endif //KUA_TABLE_H
