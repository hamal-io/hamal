#ifndef KUA_EXCEPTION_H
#define KUA_EXCEPTION_H

#include "kua_common.h"

jint
throw_illegal_argument(JNIEnv *env, char const *message);

jint
throw_illegal_state(JNIEnv *env, char const *message);

jint
throw_stack_overflow(JNIEnv *env, char const *message);

#endif //KUA_EXCEPTION_H
