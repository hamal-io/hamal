#include "kua_jni_error.h"
#include "kua_jni.h"
#include "kua_state.h"

int
throw_error_assert(char const* message)
{
	JNIEnv* env = current_env();
	if (env != NULL)
	{
		return (*env)->ThrowNew(env, jni_ref().error_assertion_class, message);
	}
	return LUA_ERRERR;
}


int
throw_error_plugin(jthrowable throwable)
{
	JNIEnv* env = current_env();

	jthrowable kua_error = (*env)->NewObject(
			env,
			jni_ref().error_plugin_class,
			jni_ref().error_plugin_ctor_id,
			throwable
	);

	return (*env)->Throw(env, kua_error);
}

int
throw_error_internal(char const* message)
{
	JNIEnv* env = current_env();
	return (*env)->ThrowNew(env, jni_ref().error_internal_class, message);
}

int
throw_error_not_found(char const* message)
{
	JNIEnv* env = current_env();
	return (*env)->ThrowNew(env, jni_ref().error_not_found_class, message);
}

int
throw_error_illegal_argument(char const* message)
{
	JNIEnv* env = current_env();
	return (*env)->ThrowNew(env, jni_ref().error_illegal_argument_class, message);
}

int
throw_error_invalid_state(char const* message)
{
	JNIEnv* env = current_env();
	return (*env)->ThrowNew(env, jni_ref().error_illegal_state_class, message);
}

int
throw_error_decimal(char const* message)
{
	JNIEnv* env = current_env();
	return (*env)->ThrowNew(env, jni_ref().error_decimal_class, message);
}

int
throw(jthrowable throwable)
{
	JNIEnv* env = current_env();
	return (*env)->Throw(env, throwable);
}