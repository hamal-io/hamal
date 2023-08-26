#include "lauxlib.h"
#include "kua_common.h"

#include "kua_jni_error.h"
#include "kua_check.h"

char const *
typename(enum type type) {
    switch (type) {
        case NIL_TYPE:
            return "nil";
        case BOOLEAN_TYPE:
            return "boolean";
        case LIGHT_USER_DATA_TYPE:
            return "pointer";
        case NUMBER_TYPE:
            return "number";
        case STRING_TYPE:
            return "string";
        case TABLE_TYPE:
            return "table";
        case FUNCTION_TYPE:
            return "function";
        case USER_DATA_TYPE:
            return "user_data";
        case THREAD_TYPE:
            return "thread";
        case ERROR_TYPE:
            return "error";
        case DECIMAL_TYPE:
            return "decimal";
        default:
            throw_illegal_state("typename not implemented");
    }
}

enum type
lua_type_at(lua_State *L, int idx) {
    if (check_argument(idx != 0, "Index must not be 0") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    return (enum type) lua_type(L, idx);
}

enum type
type_at(lua_State *L, int idx) {
    if (check_argument(idx != 0, "Index must not be 0") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_index(L, idx) == CHECK_RESULT_ERROR) return LUA_TNONE;
    idx = lua_absindex(L, idx);
    int result = lua_type_at(L, idx);
    if (result == TABLE_TYPE || result == USER_DATA_TYPE) {
        if (lua_getmetatable(L, idx)) {
            luaL_getmetafield(L, idx, "__type_id");
            if (lua_type(L, -1) == NIL_TYPE) {
                lua_pop(L, 2);
                return result;
            }

//            if (result == TABLE_TYPE) {
                if (lua_type(L, -1) == NUMBER_TYPE) {
                    result = lua_tonumber(L, -1);
                }
//                lua_pop(L, 1);
//            } else {
//                if (lua_type(L, -1) == NUMBER_TYPE) {
//                    result = lua_tonumber(L, -1);
//                }
////                lua_pop(L, 2);
//            }
            lua_pop(L, 2);
        }
    }
    return result;
}