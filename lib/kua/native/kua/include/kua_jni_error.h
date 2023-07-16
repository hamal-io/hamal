#ifndef KUA_EXCEPTION_H
#define KUA_EXCEPTION_H

#include "kua_common.h"

int
throw_illegal_argument(char const *message);

int
throw_illegal_state(char const *message);

int
throw_stack_overflow(char const *message);

#endif //KUA_EXCEPTION_H
