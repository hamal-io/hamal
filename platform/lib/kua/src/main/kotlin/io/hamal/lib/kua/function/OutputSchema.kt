package io.hamal.lib.kua.function

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.table.TableProxy
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

data class FunctionOutput1Schema<ARG_1 : KuaType>(
    val arg1: KClass<ARG_1>
) : FunctionOutputSchema<FunctionOutput1<ARG_1>> {
    override val size = 1
    override fun pushResult(ctx: FunctionContext, output: FunctionOutput1<ARG_1>) {
        ctx.push(output.arg1 ?: KuaNil)
    }
}

data class FunctionOutput2Schema<ARG_1 : KuaType, ARG_2 : KuaType>(
    val arg1: KClass<ARG_1>,
    val arg2: KClass<ARG_2>
) : FunctionOutputSchema<FunctionOutput2<ARG_1, ARG_2>> {
    override val size = 2
    override fun pushResult(ctx: FunctionContext, output: FunctionOutput2<ARG_1, ARG_2>) {
        ctx.push(output.arg1 ?: KuaNil)
        ctx.push(output.arg2 ?: KuaNil)
    }
}

fun <VALUE : KuaType> FunctionContext.push(value: VALUE) = when (value) {
    is KuaAny -> pushAny(value)
    is KuaNil -> pushNil()
    is KuaNumber -> pushNumber(value)
    is KuaString -> pushString(value)
    is TableProxy -> pushTable(value)
    is KuaTable -> StackTop(toTableProxy(value).index)
    is KuaError -> pushError(value)
    else -> throw NotImplementedError("${value::class.simpleName} not implemented yet")
}
