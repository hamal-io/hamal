#ifndef KUA_ERROR_H
#define KUA_ERROR_H

/**
 * Creates a new error table and push it on top of the stack
 * error = {
 *      message = "some error message",
 *      __metatable = {
*           __type = 9,
 *          __typename = "error",
 *      }
 * }
 */

int
error_create(lua_State *L, char const *message);

void
error_register_metable(lua_State *L);

#endif //KUA_ERROR_H
