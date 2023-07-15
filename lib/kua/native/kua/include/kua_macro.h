#ifndef KUA_MACRO_H
#define KUA_MACRO_H

#define UNUSED __attribute__((unused))
#define NO_RETURN __attribute__((noreturn))
#define STATE_METHOD_NAME(method) Java_io_hamal_lib_kua_LuaState_##method

#endif //KUA_MACRO_H
