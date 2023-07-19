package io.hamal.lib.kua

import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.FuncValue
import io.hamal.lib.kua.value.ModuleValue
import io.hamal.lib.kua.value.NamedFuncValue


fun main() {
//    System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/kua/libkua.so")
    FixedPathLoader.load()

    Sandbox(FixedPathLoader).use { sb ->
        //
//    sbox.state.createTable(1, 1)
//    sbox.state.pushString("value")
//    sbox.state.setTableField(1, "key")
//    sbox.state.getTableField(1, "key")
//    sbox.state.getTableField(1, "key")
//
//    val result = sbox.state.getTableLength(1)
//    println(result)

        sb.register(
            ModuleValue(
                name = "test",
                namedFuncs = listOf(
                    NamedFuncValue("call", ReturnFunc(sb.stack)),
                    NamedFuncValue("recv", ReceiveFunc())
                )
            )
        )

        sb.runCode(
            CodeValue(
                """
      local x, y = test.call()
      print("result:", x, y)
      test.recv(x, y)
    """.trimIndent()
            )
        )
    }


}


class ReturnFunc(val stack: Stack) : FuncValue() {
    override fun invokedByLua(state: State): Int {
        println("Return Func")
//        println(stack.size())
        state.pushString("WORKS")
        state.pushString("WORKS2")

        println(state.top())
//        println(stack.state.type(-2))
        return 2
    }
}

class ReceiveFunc(): FuncValue(){
    override fun invokedByLua(state: State): Int {
        println(state.top())
        println(state.type(-1))
        println(state.type(-2))
        return 0
    }

}