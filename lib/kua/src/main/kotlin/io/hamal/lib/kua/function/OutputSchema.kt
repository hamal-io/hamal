package io.hamal.lib.kua.function

import io.hamal.lib.kua.table.TableArrayValue
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.*
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
        ctx.push(output.arg1 ?: NilValue)
    }
}

data class FunctionOutput2Schema<ARG_1 : Value, ARG_2 : Value>(
    val arg1: KClass<ARG_1>,
    val arg2: KClass<ARG_2>
) : FunctionOutputSchema<FunctionOutput2<ARG_1, ARG_2>> {
    override val size = 2
    override fun pushResult(ctx: FunctionContext, output: FunctionOutput2<ARG_1, ARG_2>) {
        ctx.push(output.arg1 ?: NilValue)
        ctx.push(output.arg2 ?: NilValue)
    }
}

fun <VALUE : Value> FunctionContext.push(value: VALUE) = when (value) {
    is NilValue -> pushNil()
    is NumberValue -> pushNumber(value)
    is StringValue -> pushString(value)
    is TableArrayValue -> pushTop(value.index)
    is TableMapValue -> pushTop(value.index)
    is AnyValue -> pushAny(value)
    is ErrorValue -> pushError(value)
    else -> throw NotImplementedError("${value::class.simpleName} not implemented yet")
}
