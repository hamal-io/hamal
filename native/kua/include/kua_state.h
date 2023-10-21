#ifndef KUA_STATE_H
#define KUA_STATE_H

lua_State *
current_state(JNIEnv *env, jobject K);

void
get_global(lua_State *L, char const *key);

struct jni_ref {
    jclass illegal_argument_exception_class;
    jclass illegal_state_exception_class;
    jclass error_class;
    jclass extension_error_class;
    jmethodID extension_error_ctor_id;
    jclass script_error_class;
    jmethodID script_error_ctor_id;
    jclass decimal_error_class;
    jmethodID decimal_error_ctor_id;
    jclass assertion_error_class;
    jclass kua_func_class;
    jmethodID invoked_by_lua_method_id;
};

struct jni_ref
jni_ref(void);

void
init_connection(JNIEnv *env, jobject K);

void
close_connection(lua_State *L);

#endif //KUA_STATE_H
