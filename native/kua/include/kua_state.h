#ifndef KUA_STATE_H
#define KUA_STATE_H

lua_State*
current_state(JNIEnv* env, jobject K);

void
get_global(lua_State* L, char const* key);

struct jni_ref
{
	jclass error_plugin_class;
	jmethodID error_plugin_ctor_id;

	jclass error_decimal_class;
	jmethodID error_decimal_ctor_id;

	jclass error_internal_class;
	jmethodID error_internal_ctor_id;

	jclass error_not_found_class;
	jmethodID error_not_found_ctor_id;

	jclass error_illegal_argument_class;
	jmethodID error_illegal_argument_ctor_id;

	jclass error_illegal_state_class;
	jmethodID error_illegal_state_ctor_id;


	jclass error_assertion_class;

	jclass kua_func_class;
	jmethodID invoked_by_lua_method_id;
};

struct jni_ref
jni_ref(void);

void
init_connection(JNIEnv* env, jobject K);

void
close_connection(lua_State* L);

#endif //KUA_STATE_H
