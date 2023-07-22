package io.hamal.lib.kua.function

import io.hamal.lib.kua.table.TableArrayValue
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.table.TableProxy
import io.hamal.lib.kua.table.TableProxyContext
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.TableValue
import io.hamal.lib.kua.value.Value
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

fun <ARG : Value> KClass<ARG>.extract(ctx: FunctionContext, index: Int): ARG {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        NumberValue::class -> ctx.getNumberValue(index) as ARG
        StringValue::class -> ctx.getStringValue(index) as ARG
        TableValue::class -> TODO() //FIXME loads the entire table from lua -- maybe some form of readonly table value and table value is interface?!
        TableProxy::class -> TableProxy(TableProxyContext(index, ctx.state)) as ARG
        TableMapValue::class -> TableProxy(TableProxyContext(index, ctx.state)) as ARG
        TableArrayValue::class -> TableProxy(TableProxyContext(index, ctx.state)) as ARG
        else -> TODO()
    }
}

