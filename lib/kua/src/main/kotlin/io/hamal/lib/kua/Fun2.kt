package io.hamal.lib.kua

import io.hamal.lib.kua.value.Function1Param1Result
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.function.*

class Magic : Function1Param1Result<NumberValue, NumberValue>(
    FunctionInput1Schema(NumberValue::class),
    FunctionOutput1Schema(NumberValue::class)
) {
    override fun invoke(ctx: Context, input: FunctionInput1<NumberValue>): FunctionOutput1<NumberValue> {
        println("Returns ${input.arg1.value}")
        return FunctionOutput1(NumberValue(input.arg1.value))
    }
}


fun main() {
    FixedPathLoader.load()
    val result = Magic().invokedByLua(Bridge())
    println(result)
}