package io.hamal.lib.kua.function

import io.hamal.lib.kua.value.Value
import kotlin.reflect.KClass

sealed interface FunctionOutputSchema<OUTPUT : FunctionOutput<*, *>> {
    val size: Int
    fun pushResult(ctx: FunctionContext, output: OUTPUT)
}

object FunctionOutput0Schema : FunctionOutputSchema<FunctionOutput0> {
    override val size = 0
    override fun pushResult(ctx: FunctionContext, output: FunctionOutput0) {}
}

data class FunctionOutput1Schema<ARG_1 : Value>(
    val arg1: KClass<ARG_1>
) : FunctionOutputSchema<FunctionOutput1<ARG_1>> {
    override val size = 1
    override fun pushResult(ctx: FunctionContext, output: FunctionOutput1<ARG_1>) {
        ctx.stack.push(output.arg1)
    }
}

data class FunctionOutput2Schema<ARG_1 : Value, ARG_2 : Value>(
    val arg1: KClass<ARG_1>,
    val arg2: KClass<ARG_2>
) : FunctionOutputSchema<FunctionOutput2<ARG_1, ARG_2>> {
    override val size = 2
    override fun pushResult(ctx: FunctionContext, output: FunctionOutput2<ARG_1, ARG_2>) {
        ctx.stack.push(output.arg1)
        ctx.stack.push(output.arg2)
    }
}
