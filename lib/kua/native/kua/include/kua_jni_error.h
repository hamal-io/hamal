#ifndef KUA_EXCEPTION_H
#define KUA_EXCEPTION_H

#include <jni.h>
#include "kua_common.h"

int
throw_illegal_argument(char const *message);

int
throw_illegal_state(char const *message);

int
throw_error(char const *message);

int
throw_assert_error(char const *message);

int
throw_kua_error(jstring message, jthrowable throwable);

#endif //KUA_EXCEPTION_H
