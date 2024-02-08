#ifndef KUA_CHECK_H
#define KUA_CHECK_H

#include "kua_common.h"

enum check_result {
    CHECK_RESULT_OK,
    CHECK_RESULT_ERROR
};


/**
 * Checks whether a given index is inside of bounds. If not throw an IllegalArgumentException and return with 1
 * @param L
 * @param idx
 * @return CHECK_RESULT_OK if check passed otherwise CHECK_RESULT_ERROR
 */
enum check_result
check_index(lua_State *L, int idx);

/**
 * Throws an IllegalArgumentException if the condition is false (!=1)
 * @param condition
 * @param error_message
 * @return
 */
enum check_result
check_argument(int condition, char const *error_message);

/**
 * Checks if stack has space for (total) more elements. If not throw StackOvernamespaceException
 * @param L
 * @param total
 * @return CHECK_RESULT_OK if check passed otherwise CHECK_RESULT_ERROR
 */
enum check_result
check_stack_overnamespace(lua_State *L, int total);

/**
 * Checks that stack has at least (total) more elements. If not throw StackUndernamespaceError
 * @param L
 * @param total
 * @return CHECK_RESULT_OK if check passed otherwise CHECK_RESULT_ERROR
 */
enum check_result
check_stack_undernamespace(lua_State *L, int total);


/**
 * Checks whether the type at given index matches the expectations
 * @param L
 * @param idx
 * @param expected_type
 * @return CHECK_RESULT_OK if check passed otherwise CHECK_RESULT_ERROR
 */
enum check_result
check_type_at(lua_State *L, int idx, enum type expected_type);

/**
 * Checks whether the type at given index matches the expectations
 * @param L
 * @param idx
 * @param expected_type
 * @return CHECK_RESULT_OK if check passed otherwise CHECK_RESULT_ERROR
 */
enum check_result
check_lua_type_at(lua_State *L, int idx, enum type expected_lua_type);

#endif //KUA_CHECK_H
