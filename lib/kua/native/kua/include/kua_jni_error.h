#ifndef KUA_EXCEPTION_H
#define KUA_EXCEPTION_H

#include "kua_common.h"

jint
dep_throw_illegal_argument(JNIEnv *env, char const *message);

int
throw_illegal_argument(char const *message);

jint
dep_throw_illegal_state(JNIEnv *env, char const *message);

jint
dep_throw_stack_overflow(JNIEnv *env, char const *message);

#endif //KUA_EXCEPTION_H
