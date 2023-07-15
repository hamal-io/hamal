#include "kua_check.h"
#include "kua_error.h"

jint
check_index(JNIEnv *env, lua_State *L, int idx) {
    int top = lua_gettop(L);
    if (idx < 1 || idx > top) {
        throw_illegal_argument(env, "Index out of bounds");
        return 1;
    }
    return 0;
}