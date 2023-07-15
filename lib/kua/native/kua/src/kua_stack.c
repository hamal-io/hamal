#include "kua_check.h"
#include "kua_macro.h"
#include "kua_state.h"

JNIEXPORT jint JNICALL
STATE_METHOD_NAME(pushBoolean)(JNIEnv *env, jobject K, jboolean b) {
    lua_State *L = state_from_thread(env, K);
    if (check_stack(env, L) == CHECK_RESULT_ERROR) return -1;
    lua_pushboolean(L, b);
    return (jint) lua_gettop(L);
}