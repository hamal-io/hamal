#ifndef KUA_CHECK_H
#define KUA_CHECK_H

#include "kua_common.h"

enum check_result {
    CHECK_RESULT_OK,
    CHECK_RESULT_ERROR
};

/**
 * Checks whether a given index is inside of bounds. If not throw an IllegalArgumentException and return with 1
 * @param env
 * @param L
 * @param idx
 * @return CHECK_RESULT_OK if check passed otherwise CHECK_RESULT_ERROR
 */
enum check_result
check_index(JNIEnv *env, lua_State *L, int idx);

/**
 * Checks the stack for overflow. If so throw StackOverflowError
 * @param env
 * @param L
 * @return CHECK_RESULT_OK if check passed otherwise CHECK_RESULT_ERROR
 */
enum check_result
check_stack(JNIEnv *env, lua_State *L);

/**
 * Checks whether the type at given index matches the expectations
 * @param env
 * @param L
 * @return CHECK_RESULT_OK if check passed otherwise CHECK_RESULT_ERROR
 */
enum check_result
check_type_at(JNIEnv *env, lua_State *L, int idx, int expected_type);

#endif //KUA_CHECK_H
