#ifndef KUA_BUILTIN_DECIMAL_H
#define KUA_BUILTIN_DECIMAL_H

#define KUA_BUILTIN_DECIMAL    "__decimal__"

int
builtin_decimal_register(lua_State *L);

int
decimal_new(lua_State *L);

int
decimal_new_from_string(lua_State *L, char const* value);

int
decimal_as_string(lua_State *L, int idx);

#endif //KUA_BUILTIN_DECIMAL_H
