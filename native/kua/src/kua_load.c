#include <lua.h>
#include <lauxlib.h>

#include "kua_jni_error.h"
#include "kua_load.h"

int
load_string(lua_State *L, char const *code) {
    int result = luaL_loadstring(L, code);
    if (result != LUA_OK) {
        char const *error_c_str = lua_tostring(L, -1);
        if (error_c_str == NULL) {
			throw_error_internal("Unknown error occurred");
        } else {
			throw_error_internal(error_c_str);
        }
    }
    return result;
}