#ifndef KUA_ERROR_H
#define KUA_ERROR_H

/**
 * Creates a new error table and push it on top of the stack
 * error = {
 *      message = "some error message",
 *      __metatable = {
*           __type_id = 9,
 *          __typename = "error",
 *      }
 * }
 */

#define KUA_BUILTIN_ERROR    "__error__"

int
builtin_error_create(lua_State *L, char const *message);

void
builtin_error_register(lua_State *L);

#endif //KUA_ERROR_H
