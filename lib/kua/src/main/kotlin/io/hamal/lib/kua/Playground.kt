package io.hamal.lib.kua

import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.NamedFuncValue
import io.hamal.lib.kua.value.TestFunc


fun LuaState.register(
    moduleName: String,
    namedJavaFunctions: List<NamedFuncValue>
) {

    createTable(0, namedJavaFunctions.size)
    for (i in namedJavaFunctions.indices) {
        val name: String = namedJavaFunctions[i].name
        pushFuncValue(namedJavaFunctions[i].func)
        setTableField(1, name)
    }
    getSubTable(luaRegistryIndex(), "_LOADED")
//    pushValue(-2)
    push(-2)
    setTableField(-2, moduleName)
    pop(1)
//    if (global) {
//        rawGet(REGISTRYINDEX, LuaState.RIDX_GLOBALS)
        getTableRawIdx(luaRegistryIndex(), 2)
//        pushValue(-2)
        push(-2)
        setTableField(-2, moduleName)
        pop(1)
//    }

}

fun main() {
//    System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/kua/libkua.so")
    FixedPathLoader.load()
    val s = LuaState()

    val sbox = LuaSandbox(s)

    println(sbox.state.luaRegistryIndex())

//
//    sbox.state.createTable(1, 1)
//    sbox.state.pushString("value")
//    sbox.state.setTableField(1, "key")
//    sbox.state.getTableField(1, "key")
//    sbox.state.getTableField(1, "key")
//
//    val result = sbox.state.getTableLength(1)
//    println(result)

    sbox.state.register(
        "test", listOf(
            NamedFuncValue("log", TestFunc())
        )
    )

    sbox.runCode(
        CodeValue(
            """
        print(test)
        for k,v in pairs(test) do
          print(k,v)
        end
        test.log()
        test.log()
        test.log()
        test.log()
    """.trimIndent()
        )
    )

}
