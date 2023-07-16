package io.hamal.lib.kua

import io.hamal.lib.kua.value.NamedFuncValue

fun LuaState.register(namedJavaFunction: NamedFuncValue) {
    pushFuncValue(namedJavaFunction.func)
    setGlobal(namedJavaFunction.name)
}


fun LuaState.register(
    moduleName: String,
    namedJavaFunctions: List<NamedFuncValue>,
    global: Boolean
) {
//    check()
    createTable(0, namedJavaFunctions.size)
    for (i in namedJavaFunctions.indices) {
        val name: String = namedJavaFunctions[i].name
        pushFuncValue(namedJavaFunctions[i].func)
        setField(-2, name)
    }
////    getSubTable(REGISTRYINDEX, "_LOADED")
//    getSubTable(1, "_LOADED")
//
//    push(-2)
//    setField(-2, moduleName)
//    pop(1)
////    if (global) {
////        rawGet(REGISTRYINDEX, LuaState.RIDX_GLOBALS)
//        rawGetI(1, 2)
//        push(-2)
//        setField(-2, moduleName)
//        pop(1)
////    }
}

fun main() {
//    System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/kua/libkua.so")
    FixedPathLoader.load()
    val s = LuaState()

    val sbox = LuaSandbox(s)

//    sbox.state.register(
//        "test", listOf(
//            NamedKuaFunc("hello", TestFunc())
//        ), true
//    )

//    sbox.state.register(NamedFuncValue("log_info", TestFunc()))
//
//    sbox.runCode(CodeValue("""
//          local module = {
//                invoke = log_info
//            }
//
//            log_info("I create a log record for ya")
//
//            module.invoke("test")
//
//    """.trimIndent()))

    println( sbox.state.createTable(1, 1))
    println(sbox.stack.size())
    println(sbox.state.type(1))


//    println(sbox.stack.size())
//    sbox.state.call(0, 0)


//    println(sbox.stack.size())
//    println(sbox.stack.pushBoolean(true))


//    sbox.state.pushFunc(TestFunc())
//    sbox.state.call(0,0)
//
//    sbox.state.pushAny(1)
//    sbox.state.createTable(0, 0)

//    sbox.state.pushFunc(TestFunc())
//    sbox.state.call(0,0)

//    println(sbox.stack.toBoolean(1))
//    println(sbox.stack.size())


//    println(sbox.stack.size())


//    sbox.state.pushNumber(2.0)
//    println(sbox.stack.toBoolean(1))

}
