#ifndef KUA_REFERENCE_H
#define KUA_REFERENCE_H

#include "kua_common.h"

int
reference_acquire(lua_State *L);

int
reference_push(lua_State *L, int ref);

void
reference_release(lua_State *L, int ref);

#endif //KUA_REFERENCE_H
