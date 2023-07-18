package io.hamal.lib.kua

import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.NamedFuncValue
import io.hamal.lib.kua.value.TestFunc


fun main() {
//    System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/kua/libkua.so")
    FixedPathLoader.load()
    val s = State()

    val sbox = Sandbox(s)

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

    sbox.register(
        Module(
            name = "test",
            namedFuncs = listOf(
                NamedFuncValue("log", TestFunc())
            )
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
