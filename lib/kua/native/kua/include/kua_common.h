#ifndef KUA_COMMON_H
#define KUA_COMMON_H

#include <lua.h>

enum Type {
    NIL_TYPE = 0,
    BOOLEAN_TYPE = 1,
    LIGHT_USER_DATA_TYPE = 2,
    NUMBER_TYPE = 3,
    STRING_TYPE = 4,
    TABLE_TYPE = 5,
    FUNCTION_TYPE = 6,
    USER_DATA_TYPE = 7,
    THREAD_TYPE = 8
};

#endif //KUA_COMMON_H
