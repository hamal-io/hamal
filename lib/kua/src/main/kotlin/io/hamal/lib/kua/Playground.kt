package io.hamal.lib.kua

import io.hamal.lib.kua.value.*
import io.hamal.lib.kua.value.function.Context
import io.hamal.lib.kua.value.function.FunctionInput0
import io.hamal.lib.kua.value.function.FunctionOutput1
import io.hamal.lib.kua.value.function.FunctionOutput1Schema


fun main() {
    FixedPathLoader.load()

    Sandbox(FixedPathLoader).use { sb ->
        sb.register(
            ModuleValue(
                name = "test",
                namedFuncs = listOf(
                    NamedFunctionValue("call", ReturnFunc(sb.stack)),
//                    NamedFuncValue("recv", ReceiveFunc())
                )
            )
        )

        sb.runCode(
            CodeValue(
                """
      local x, y = test.call()
      print("result:", x)
      -- test.recv(x, y)
    """.trimIndent()
            )
        )
    }


}

class ReturnFunc(val stack: Stack) : Function0Param1Result<StringValue>(
    FunctionOutput1Schema(StringValue::class)
) {
    override fun invoke(ctx: Context, input: FunctionInput0): FunctionOutput1<StringValue> {
        return FunctionOutput1(
            StringValue("It works")
        )
    }

//    override fun invokedByLua(bridge: Bridge): Int {
//        println("Return Func")
////        println(stack.size())
//        bridge.pushString("WORKS")
//        bridge.pushString("WORKS2")
//
//        println(bridge.top())
////        println(stack.state.type(-2))
//        return 2
//    }
}


//class ReturnFunc(val stack: Stack) : FuncValue() {
//    override fun invokedByLua(bridge: Bridge): Int {
//        println("Return Func")
////        println(stack.size())
//        bridge.pushString("WORKS")
//        bridge.pushString("WORKS2")
//
//        println(bridge.top())
////        println(stack.state.type(-2))
//        return 2
//    }
//}
//
//class ReceiveFunc(): FuncValue(){
//    override fun invokedByLua(bridge: Bridge): Int {
//        println(bridge.top())
//        println(bridge.type(-1))
//        println(bridge.type(-2))
//        return 0
//    }
//
//}