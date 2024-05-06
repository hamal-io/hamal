#ifndef KUA_EXCEPTION_H
#define KUA_EXCEPTION_H

#include <jni.h>
#include "kua_common.h"


int
throw_error_assert(char const* message);

int
throw_error_plugin(jthrowable throwable);

int
throw_error_internal(char const* message);

int
throw_error_decimal(char const* message);

int
throw_error_not_found(char const* message);

int
throw_error_illegal_argument(char const* message);

int
throw_error_invalid_state(char const* message);


int
throw(jthrowable throwable);


#endif //KUA_EXCEPTION_H
