#include "kua_common.h"
#include "kua_macro.h"

//static jfieldID current_thread_id = 0;
//
///* Returns the Lua thread from the Java state. */
//static lua_State *get_thread(JNIEnv *env, jobject java_state) {
//    return (lua_State *) (uintptr_t) (*env)->GetLongField(env, java_state, current_thread_id);
//}
//
//JNIEXPORT jboolean JNICALL
//STATE_METHOD_NAME(pushBoolean)(JNIEnv *env, jobject obj, jboolean b) {
////    lua_State *L;
//
////    JNLUA_ENV(env);
//    lua_State *L = get_thread(env, obj);
////    if (checkstack(L, JNLUA_MINSTACK)) {
//        lua_pushboolean(L, b);
////    }
//
////    return sizeof(lua_Integer);
//
//    return b;
//}
