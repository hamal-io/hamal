#ifndef KUA_STACK_H
#define KUA_STACK_H

#include "kua_common.h"

int
type(lua_State *L, int idx);

int
top(lua_State *L);

void
set_top(lua_State *L, int idx);

int
abs_index(lua_State *L, int idx);

int
push_top(lua_State *L, int idx);

int
pop(lua_State *L, int total);

int
push_nil(lua_State *L);

int
push_error(lua_State *L, char const *message);

int
push_boolean(lua_State *L, int value);

int
push_number(lua_State *L, double value);

int
push_string(lua_State *L, char const *value);

int
push_func_value(lua_State *L, void *func);

char const *
to_error(lua_State *L, int idx);

int
to_boolean(lua_State *L, int idx);

double
to_number(lua_State *L, int idx);

char const *
to_string(lua_State *L, int idx);


#endif //KUA_STACK_H
