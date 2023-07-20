package io.hamal.lib.kua


fun main() {
    FixedPathLoader.load()

    Sandbox(FixedPathLoader).use { sb ->
//        sb.register(
//            ModuleValue(
//                name = "test",
//                namedFuncs = listOf(
//                    NamedFuncValue("call", ReturnFunc(sb.stack)),
//                    NamedFuncValue("recv", ReceiveFunc())
//                )
//            )
//        )
//
//        sb.runCode(
//            CodeValue(
//                """
//      local x, y = test.call()
//      print("result:", x, y)
//      test.recv(x, y)
//    """.trimIndent()
//            )
//        )
    }


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