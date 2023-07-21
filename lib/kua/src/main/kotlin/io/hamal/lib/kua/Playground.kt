package io.hamal.lib.kua

import io.hamal.lib.kua.value.*
import io.hamal.lib.kua.value.function.FunctionContext
import io.hamal.lib.kua.value.function.FunctionInput2Schema
import io.hamal.lib.kua.value.function.FunctionOutput2Schema


fun main() {
    FixedPathLoader.load()

    Sandbox(FixedPathLoader).use { sb ->
        sb.register(
            ModuleValue(
                name = "test",
                namedFuncs = listOf(
                    NamedFunctionValue("call", ReturnFunc(sb.stack)),
                    NamedFunctionValue("recv", ReceiveFunc())
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

class ReturnFunc(val stack: Stack) : Function0In2Out<StringValue, StringValue>(
    FunctionOutput2Schema(StringValue::class, StringValue::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<StringValue, StringValue> {
        return Pair(
            StringValue("It works"),
            StringValue("It really does")
        )
    }
}

class ReceiveFunc : Function2In0Out<StringValue, StringValue>(
    FunctionInput2Schema(StringValue::class, StringValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringValue, arg2: StringValue) {
        println(arg1)
        println(arg2)
    }
}