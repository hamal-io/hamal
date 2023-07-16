#include "kua_check.h"
#include "kua_jni_error.h"

enum check_result
check_index(lua_State *L, int idx) {
    int top = lua_gettop(L);
    if (idx < 1 || idx > top) {
        throw_illegal_argument("Index out of bounds");
        return CHECK_RESULT_ERROR;
    }
    return CHECK_RESULT_OK;
}

enum check_result
check_stack(lua_State *L) {
    if (lua_checkstack(L, 1) == 0) {
        throw_stack_overflow("StackOverflow - Its all part of the process");
        return CHECK_RESULT_ERROR;
    }
    return CHECK_RESULT_OK;
}


enum check_result
check_type_at(lua_State *L, int idx, int expected_type) {
    int current_type = lua_type(L, idx);
    if (current_type != expected_type) {
        lua_pushstring(L, "Expected type to be ");
        lua_pushstring(L, lua_typename(L, expected_type));
        lua_pushstring(L, " but was ");
        lua_pushstring(L, lua_typename(L, current_type));

        lua_concat(L, 4);

        throw_illegal_state(lua_tostring(L, lua_gettop(L)));
        return CHECK_RESULT_ERROR;
    }
    return CHECK_RESULT_OK;
}