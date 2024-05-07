#include <lua.h>
#include <lauxlib.h>
#include <jni.h>

#include "kua_check.h"
#include "kua_jni.h"
#include "kua_builtin.h"
#include "kua_state.h"
#include "kua_memory.h"


static struct jni_ref current_jni_ref = {};

inline struct jni_ref
jni_ref(void)
{
	return current_jni_ref;
}


static jfieldID current_thread_id = NULL;

lua_State*
current_state(JNIEnv* env, jobject K)
{
	return (lua_State*)(uintptr_t)(*env)->GetLongField(env, K, current_thread_id);
}

static void state_to_thread(JNIEnv* env, jobject K, lua_State* L)
{
	(*env)->SetLongField(env, K, current_thread_id, (jlong)(uintptr_t)L);
}

#include "kua_call.h"


static jclass
load_class(JNIEnv* env, const char* className)
{
	jclass clazz = (*env)->FindClass(env, className);
	if (!clazz)
	{
		//FIXME PANIC
		return NULL;
	}
	return (*env)->NewGlobalRef(env, clazz);
}


static void
setup_references(JNIEnv* env)
{
	//FIXME throw error / panic

	//@formatter:off
    current_jni_ref.error_plugin_class = load_class(env, "io/hamal/lib/kua/ErrorPlugin");
    current_jni_ref.error_plugin_ctor_id =  (*env)->GetMethodID(env,  current_jni_ref.error_plugin_class ,"<init>","(Ljava/lang/Throwable;)V");

    current_jni_ref.error_decimal_class = load_class(env, "io/hamal/lib/kua/ErrorDecimal");
    current_jni_ref.error_decimal_ctor_id =  (*env)->GetMethodID(env,  current_jni_ref.error_decimal_class ,"<init>","(Ljava/lang/String;)V");

	current_jni_ref.error_internal_class = load_class(env, "io/hamal/lib/kua/ErrorInternal");
	current_jni_ref.error_internal_ctor_id =  (*env)->GetMethodID(env,  current_jni_ref.error_internal_class ,"<init>","(Ljava/lang/String;)V");

	current_jni_ref.error_not_found_class = load_class(env, "io/hamal/lib/kua/ErrorNotFound");
	current_jni_ref.error_not_found_ctor_id =  (*env)->GetMethodID(env,  current_jni_ref.error_not_found_class ,"<init>","(Ljava/lang/String;)V");

	current_jni_ref.error_illegal_argument_class = load_class(env, "io/hamal/lib/kua/ErrorIllegalArgument");
	current_jni_ref.error_illegal_argument_ctor_id =  (*env)->GetMethodID(env,  current_jni_ref.error_illegal_argument_class ,"<init>","(Ljava/lang/String;)V");

	current_jni_ref.error_illegal_state_class = load_class(env, "io/hamal/lib/kua/ErrorIllegalState");
	current_jni_ref.error_illegal_state_ctor_id =  (*env)->GetMethodID(env,  current_jni_ref.error_illegal_state_class ,"<init>","(Ljava/lang/String;)V");

    current_jni_ref.error_assertion_class = load_class(env, "io/hamal/lib/kua/ErrorAssertion");

    current_jni_ref.kua_func_class = load_class(env, "io/hamal/lib/kua/value/KuaFunction");
    current_jni_ref.invoked_by_lua_method_id = (*env)->GetMethodID(env, current_jni_ref.kua_func_class, "invokedByLua","(Lio/hamal/lib/kua/Native;)I");
    //@formatter:on
}


static int
cleanup_k_object(lua_State* L)
{
	JNIEnv* env = current_env();
	if (!env)
	{
		return LUA_OK;
	}

	if (check_type_at(L, 1, USER_DATA_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
	jobject obj = *(jobject*)lua_touserdata(L, 1);
	(*env)->DeleteGlobalRef(env, obj);
	return LUA_OK;
}


void
init_connection(JNIEnv* env, jobject K)
{
	setup_references(env);


	lua_State* L;
	L = luaL_newstate();

//	lua_setallocf(L, memory_arena_reallocate, NULL);

	lua_pushlightuserdata(L, (void*)K);

	jobject* ref;
	jobject newstate_obj = (jobject)lua_touserdata(L, 1);
	lua_pop(L, 1);

	ref = lua_newuserdata(L, sizeof(jobject));
	lua_createtable(L, 0, 1);
	*ref = (*env)->NewWeakGlobalRef(env, newstate_obj);
	if (!*ref)
	{
		printf("JNI error: NewWeakGlobalRef() failed setting up Lua state");
		lua_error(L);
	}
	lua_setmetatable(L, -1);
	lua_setfield(L, LUA_REGISTRYINDEX, "__KState");


	luaL_newmetatable(L, "__KObject");
	lua_pushboolean(L, 0);
	lua_setfield(L, -2, "__metatable");

	lua_pushcclosure(L, cleanup_k_object, 0);
	lua_setfield(L, -2, "__close");
	lua_pop(L, 1);

	// FIXME gc should not be required --> use memory default_arena allocator and clean up properly when closing connection
	lua_gc(L, LUA_GCSTOP);

	builtin_register(L);

	state_to_thread(env, K, L);
}

static void unload_jni_ref(JNIEnv* env)
{
	// FIXME
	(*env)->DeleteGlobalRef(env, current_jni_ref.error_plugin_class);
	(*env)->DeleteGlobalRef(env, current_jni_ref.kua_func_class);
}

void
close_connection(lua_State* L)
{
	// FIXME should become a separate function
//    JNIEnv *env = current_env();
//    // removes state reference
//    lua_getfield(L, LUA_REGISTRYINDEX, "__KState");
//    jobject obj = *(jobject *) lua_touserdata(L, -1);
//    if (obj) {
//        (*env)->DeleteWeakGlobalRef(env, obj);
//    }
//
////    unload_jni_ref(env);
//
//    // FIXME remove/ unload loaded jni references
//    lua_close(L);
////    memory_arena_free();
}