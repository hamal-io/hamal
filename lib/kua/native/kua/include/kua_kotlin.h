#ifndef KUA_KOTLIN_H
#define KUA_KOTLIN_H

typedef struct lua_State lua_State;

int
call(lua_State *L,int argsCount, int resultCount);

#endif //KUA_KOTLIN_H
