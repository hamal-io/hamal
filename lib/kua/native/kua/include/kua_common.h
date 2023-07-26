#ifndef KUA_COMMON_H
#define KUA_COMMON_H

#include <lua.h>

enum type {
    NIL_TYPE = 0,
    BOOLEAN_TYPE = 1,
    LIGHT_USER_DATA_TYPE = 2,
    NUMBER_TYPE = 3,
    STRING_TYPE = 4,
    TABLE_TYPE = 5,
    FUNCTION_TYPE = 6,
    USER_DATA_TYPE = 7,
    THREAD_TYPE = 8,
    ERROR_TYPE = 20
};

enum result {
    RESULT_OK,
    RESULT_ERROR
};


char const*
typename(enum type type);

#endif //KUA_COMMON_H
