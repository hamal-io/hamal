#ifndef KUA_CALL_H
#define KUA_CALL_H

typedef struct lua_State lua_State;

enum result
call(lua_State *L, int argsCount, int resultCount);

int
call_func_value_closure(lua_State *L);

#endif //KUA_CALL_H
