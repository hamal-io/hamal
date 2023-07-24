package io.hamal.lib.kua.function

import io.hamal.lib.kua.table.TableArrayProxyValue
import io.hamal.lib.kua.table.TableMapProxyValue
import io.hamal.lib.kua.value.AnyValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
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
        ctx.push(output.arg1)
    }
}

data class FunctionOutput2Schema<ARG_1 : Value, ARG_2 : Value>(
    val arg1: KClass<ARG_1>,
    val arg2: KClass<ARG_2>
) : FunctionOutputSchema<FunctionOutput2<ARG_1, ARG_2>> {
    override val size = 2
    override fun pushResult(ctx: FunctionContext, output: FunctionOutput2<ARG_1, ARG_2>) {
        ctx.push(output.arg1)
        ctx.push(output.arg2)
    }
}

fun <VALUE : Value> FunctionContext.push(value: VALUE) = when (value) {
    is NumberValue -> pushNumber(value)
    is StringValue -> pushString(value)
    is TableArrayProxyValue -> pushTop(value.index)
    is TableMapProxyValue -> pushTop(value.index)
    is AnyValue -> pushAny(value)
    else -> throw NotImplementedError("${value::class.simpleName} not implemented yet")
}
