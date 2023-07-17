#ifndef KUA_STATE_H
#define KUA_STATE_H

#include "kua_common.h"

lua_State *
state_from_thread(JNIEnv *env, jobject K);

void
get_global(lua_State *L, char const *key);

#endif //KUA_STATE_H
