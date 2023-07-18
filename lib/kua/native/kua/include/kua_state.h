#ifndef KUA_STATE_H
#define KUA_STATE_H

lua_State *
state_from_thread(JNIEnv *env, jobject K);

void
get_global(lua_State *L, char const *key);

struct jni_ref {
    jmethodID invoked_by_lua_method_id;
    jclass illegal_argument_exception_class;
    jclass illegal_state_exception_class;
    jclass error_class;
};

struct jni_ref
jni_ref(void);

#endif //KUA_STATE_H
