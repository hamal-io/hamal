#include "kua_table.h"
#include "kua_check.h"

int
create_table(lua_State *L, int arrayCount, int recordsCount) {
    if (check_argument(arrayCount >= 0, "Array count must not be negative") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_argument(recordsCount >= 0, "Records count must not be negative") == CHECK_RESULT_ERROR) return LUA_TNONE;
    if (check_stack_overflow(L, 1) == CHECK_RESULT_ERROR) return LUA_TNONE;
    lua_createtable(L, arrayCount, recordsCount);
    return lua_gettop(L);
}