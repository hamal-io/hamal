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
check_argument(int condition, char const *error_message) {
    if (condition != 1) {
        throw_illegal_argument(error_message);
        return CHECK_RESULT_ERROR;
    }
    return CHECK_RESULT_OK;
}

enum check_result
check_stack_overflow(lua_State *L, int total) {
    if (lua_checkstack(L, total) == 0) {
        throw_illegal_argument("Prevented stack overflow");
        return CHECK_RESULT_ERROR;
    }
    return CHECK_RESULT_OK;
}

enum check_result
check_stack_underflow(lua_State *L, int total) {
    if (lua_gettop(L) - total < 0) {
        throw_illegal_argument("Prevented stack underflow");
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