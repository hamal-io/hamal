#include "lua.h"

int
lua_version_number() { return LUA_VERSION_NUM; }

int
lua_integer_width() { return sizeof(lua_Integer); }
