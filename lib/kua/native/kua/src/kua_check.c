#include "kua_check.h"
#include "kua_error.h"

enum check_result
check_index(JNIEnv *env, lua_State *L, int idx) {
    int top = lua_gettop(L);
    if (idx < 1 || idx > top) {
        throw_illegal_argument(env, "Index out of bounds");
        return CHECK_RESULT_ERROR;
    }
    return CHECK_RESULT_OK;
}

enum check_result
check_stack(JNIEnv *env, lua_State *L) {
    if (lua_checkstack(L, 1) == 0) {
        throw_stack_overflow(env, "StackOverflow - Its all part of the process");
        return CHECK_RESULT_ERROR;
    }
    return CHECK_RESULT_OK;
}
