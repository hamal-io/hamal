package io.hamal.lib.kua.function

import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.value.Value
import io.hamal.lib.value.ValueNil
import io.hamal.lib.value.ValueNumber
import io.hamal.lib.value.ValueString
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
        ctx.push(output.arg1 ?: ValueNil)
    }
}

data class FunctionOutput2Schema<ARG_1 : Value, ARG_2 : Value>(
    val arg1: KClass<ARG_1>,
    val arg2: KClass<ARG_2>
) : FunctionOutputSchema<FunctionOutput2<ARG_1, ARG_2>> {
    override val size = 2
    override fun pushResult(ctx: FunctionContext, output: FunctionOutput2<ARG_1, ARG_2>) {
        ctx.push(output.arg1 ?: ValueNil)
        ctx.push(output.arg2 ?: ValueNil)
    }
}

fun <VALUE : Value> FunctionContext.push(value: VALUE) = when (value) {
    is ValueNil -> nilPush()
    is ValueNumber -> numberPush(value)
    is ValueString -> stringPush(value)
    is KuaTable -> tablePush(value)
    is KuaError -> errorPush(value)
    else -> throw NotImplementedError("${value::class.simpleName} not implemented yet")
}
