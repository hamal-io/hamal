#include <lua.h>
#include <lauxlib.h>
#include <jni.h>

#include "kua_check.h"
#include "kua_error.h"
#include "kua_jni.h"
#include "kua_builtin.h"
#include "kua_state.h"
#include "kua_memory.h"


static struct jni_ref current_jni_ref = {};

inline struct jni_ref
jni_ref(void) {
    return current_jni_ref;
}


static jfieldID current_thread_id = NULL;

lua_State *
current_state(JNIEnv *env, jobject K) {
    return (lua_State *) (uintptr_t) (*env)->GetLongField(env, K, current_thread_id);
}

static void state_to_thread(JNIEnv *env, jobject K, lua_State *L) {
    (*env)->SetLongField(env, K, current_thread_id, (jlong) (uintptr_t) L);
}

#include "kua_call.h"


static jclass
load_class(JNIEnv *env, const char *className) {
    jclass clazz = (*env)->FindClass(env, className);
    if (!clazz) {
        //FIXME PANIC
        return NULL;
    }
    return (*env)->NewGlobalRef(env, clazz);
}


static void
setup_references(JNIEnv *env) {
    //FIXME throw error / panic

    //@formatter:off
    current_jni_ref.illegal_argument_exception_class = load_class(env, "java/lang/IllegalArgumentException");
    current_jni_ref.illegal_state_exception_class = load_class(env, "java/lang/IllegalStateException");
    current_jni_ref.error_class = load_class(env, "java/lang/Error");

    current_jni_ref.extension_error_class = load_class(env, "io/hamal/lib/kua/ExtensionError");
    current_jni_ref.extension_error_ctor_id =  (*env)->GetMethodID(env,  current_jni_ref.extension_error_class ,"<init>","(Ljava/lang/String;Ljava/lang/Throwable;)V");

    current_jni_ref.script_error_class = load_class(env, "io/hamal/lib/kua/ScriptError");
    current_jni_ref.script_error_ctor_id =  (*env)->GetMethodID(env,  current_jni_ref.script_error_class ,"<init>","(Ljava/lang/String;)V");


    current_jni_ref.assertion_error_class = load_class(env, "io/hamal/lib/kua/AssertionError");

    current_jni_ref.kua_func_class = load_class(env, "io/hamal/lib/kua/function/FunctionValue");
    current_jni_ref.invoked_by_lua_method_id = (*env)->GetMethodID(env, current_jni_ref.kua_func_class, "invokedByLua","(Lio/hamal/lib/kua/Native;)I");
    //@formatter:on
}


static int
cleanup_k_object(lua_State *L) {
    JNIEnv *env = current_env();
    if (!env) {
        return LUA_OK;
    }

    if (check_type_at(L, 1, USER_DATA_TYPE) == CHECK_RESULT_ERROR) return LUA_TNONE;
    jobject obj = *(jobject *) lua_touserdata(L, 1);
    (*env)->DeleteGlobalRef(env, obj);
    return LUA_OK;
}


void
init_connection(JNIEnv *env, jobject K) {
    setup_references(env);


    lua_State *L;
    L = luaL_newstate();

    lua_setallocf(L, memory_arena_reallocate, NULL);

    lua_pushlightuserdata(L, (void *) K);

    jobject *ref;
    jobject newstate_obj = (jobject) lua_touserdata(L, 1);
    lua_pop(L, 1);

    ref = lua_newuserdata(L, sizeof(jobject));
    lua_createtable(L, 0, 1);
    *ref = (*env)->NewWeakGlobalRef(env, newstate_obj);
    if (!*ref) {
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

    error_register_metable(L);
    kua_builtin_register(L);

    state_to_thread(env, K, L);
}

static void unload_jni_ref(JNIEnv *env) {
    (*env)->DeleteGlobalRef(env, current_jni_ref.illegal_argument_exception_class);
    (*env)->DeleteGlobalRef(env, current_jni_ref.illegal_state_exception_class);
    (*env)->DeleteGlobalRef(env, current_jni_ref.error_class);
    (*env)->DeleteGlobalRef(env, current_jni_ref.extension_error_class);
    (*env)->DeleteGlobalRef(env, current_jni_ref.kua_func_class);
}

void
close_connection(lua_State *L) {
    // FIXME should become a separate function
    JNIEnv *env = current_env();
    // removes state reference
    lua_getfield(L, LUA_REGISTRYINDEX, "__KState");
    jobject obj = *(jobject *) lua_touserdata(L, -1);
    if (obj) {
        (*env)->DeleteWeakGlobalRef(env, obj);
    }

    unload_jni_ref(env);

    // FIXME remove/ unload loaded jni references
    lua_close(L);
    memory_arena_free();
}