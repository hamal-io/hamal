package io.hamal.lib.kua.function

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.*
import kotlin.reflect.KClass

sealed interface FunctionOutputSchema<OUTPUT : FunctionOutput<*, *>> {
    val size: Int
    fun pushResult(ctx: FunctionContext, output: OUTPUT)
}

object FunctionOutput0Schema : FunctionOutputSchema<FunctionOutput0> {
    override val size = 0
    override fun pushResult(ctx: FunctionContext, output: FunctionOutput0) {}
}

data class FunctionOutput1Schema<ARG_1 : Type>(
    val arg1: KClass<ARG_1>
) : FunctionOutputSchema<FunctionOutput1<ARG_1>> {
    override val size = 1
    override fun pushResult(ctx: FunctionContext, output: FunctionOutput1<ARG_1>) {
        ctx.push(output.arg1 ?: NilType)
    }
}

data class FunctionOutput2Schema<ARG_1 : Type, ARG_2 : Type>(
    val arg1: KClass<ARG_1>,
    val arg2: KClass<ARG_2>
) : FunctionOutputSchema<FunctionOutput2<ARG_1, ARG_2>> {
    override val size = 2
    override fun pushResult(ctx: FunctionContext, output: FunctionOutput2<ARG_1, ARG_2>) {
        ctx.push(output.arg1 ?: NilType)
        ctx.push(output.arg2 ?: NilType)
    }
}

fun <VALUE : Type> FunctionContext.push(value: VALUE) = when (value) {
    is AnyType -> pushAny(value)
    is ArrayType -> StackTop(toProxyArray(value).index)
    is NilType -> pushNil()
    is NumberType -> pushNumber(value)
    is StringType -> pushString(value)
    is MapType -> StackTop(toProxyMap(value).index)
    is ErrorType -> pushError(value)
    is TableProxyArray -> pushTable(value)
    is TableProxyMap -> pushTable(value)
    else -> throw NotImplementedError("${value::class.simpleName} not implemented yet")
}