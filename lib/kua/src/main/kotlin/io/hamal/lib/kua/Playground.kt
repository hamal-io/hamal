package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.BuildDir


fun main() {
    NativeLoader.load(BuildDir)


    Sandbox().use { sb ->

//            val call = object : Function0In0Out() {
//                override fun invoke(ctx: FunctionContext) {
//                    println("CALLED")
//                }
//            }

//        sb.bridge.pushFunctionValue(call)
//        sb.bridge.call(0, 0)


//        val neverCalled = object : Function0In0Out() {
//            override fun invoke(ctx: FunctionContext) {
//                called = true
//            }
//
//            var called = false
//        }
//        val throwError = object : Function0In0Out() {
//            override fun invoke(ctx: FunctionContext) {
//                throw IllegalArgumentException("some illegal argument")
//            }
//        }
//        sb.register(
//            Extension(
//                name = "test",
//                functions = listOf(
//                    NamedFunctionValue("call", call)
////                    NamedFunctionValue("throw_error", throwError),
////                    NamedFunctionValue("never_called", neverCalled),
//                )
//            )
//        )


//
//            try {
//                sb.runCode(
//                    """
//for x=1,1000 do
//
//        local table = {code = "print('hello')"}
//        print(table)
//
//        -- local result = sys.adhoc(table)
//
//        local x = sys.list_execs()
//
//        for k,v in pairs(x) do
//            print(k,v)
//        end
//
//        print("id:", x[1].id)
//        print("status:", x[1].status)
//
//    end
//            """
//                )
//            } catch (t: Throwable) {
//                t.printStackTrace()
//            }
//        }
//
//        println("Never called: ${neverCalled.called}")
    }


}


