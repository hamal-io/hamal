#ifndef KUA_STATE_H
#define KUA_STATE_H

#include "kua_common.h"

lua_State *
state_from_thread(JNIEnv *env, jobject K);



#endif //KUA_STATE_H
