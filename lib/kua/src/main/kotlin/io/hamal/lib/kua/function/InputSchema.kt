package io.hamal.lib.kua.function

import io.hamal.lib.kua.table.TableArrayProxyValue
import io.hamal.lib.kua.table.TableMapProxyValue
import io.hamal.lib.kua.table.TableProxyValue
import io.hamal.lib.kua.table.TableProxyContext
import io.hamal.lib.kua.value.*
import kotlin.reflect.KClass

sealed interface FunctionInputSchema<INPUT : FunctionInput<*, *>> {
    val size: Int
    fun createInput(ctx: FunctionContext): INPUT
}

object FunctionInput0Schema : FunctionInputSchema<FunctionInput0> {
    override val size = 0
    override fun createInput(ctx: FunctionContext): FunctionInput0 {
        return FunctionInput0
    }
}

data class FunctionInput1Schema<ARG_1 : Value>(
    val arg1Class: KClass<ARG_1>
) : FunctionInputSchema<FunctionInput1<ARG_1>> {
    override val size = 1
    override fun createInput(ctx: FunctionContext): FunctionInput1<ARG_1> {
        return FunctionInput1(
            arg1Class.extract(ctx, 1)
        )
    }
}

data class FunctionInput2Schema<ARG_1 : Value, ARG_2 : Value>(
    val arg1Class: KClass<ARG_1>,
    val arg2Class: KClass<ARG_2>
) : FunctionInputSchema<FunctionInput2<ARG_1, ARG_2>> {
    override val size = 1
    override fun createInput(ctx: FunctionContext): FunctionInput2<ARG_1, ARG_2> {
        return FunctionInput2(
            arg1Class.extract(ctx, 1),
            arg2Class.extract(ctx, 2)
        )
    }
}

fun <ARG : Value> KClass<ARG>.extract(ctx: FunctionContext, idx: Int): ARG {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        AnyValue::class -> ctx.getAnyValue(idx) as ARG
        NumberValue::class -> ctx.getNumberValue(idx) as ARG
        StringValue::class -> ctx.getStringValue(idx) as ARG
        TableValue::class -> TODO() //FIXME loads the entire table from lua -- maybe some form of readonly table value and table value is interface?!
        TableProxyValue::class -> TableProxyValue(TableProxyContext(idx, ctx.state)) as ARG
        TableMapProxyValue::class -> TableProxyValue(TableProxyContext(idx, ctx.state)) as ARG
        TableArrayProxyValue::class -> TableProxyValue(TableProxyContext(idx, ctx.state)) as ARG
        else -> TODO()
    }
}

