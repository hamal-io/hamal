package io.hamal.lib.kua.function

import io.hamal.lib.kua.table.DefaultTableProxy
import io.hamal.lib.kua.table.TableArray
import io.hamal.lib.kua.table.TableMap
import io.hamal.lib.kua.table.TableProxy
import io.hamal.lib.kua.type.AnyType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.Type
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

data class FunctionInput1Schema<ARG_1 : Type>(
    val arg1Class: KClass<ARG_1>
) : FunctionInputSchema<FunctionInput1<ARG_1>> {
    override val size = 1
    override fun createInput(ctx: FunctionContext): FunctionInput1<ARG_1> {
        return FunctionInput1(
            arg1Class.extract(ctx, 1)
        )
    }
}

data class FunctionInput2Schema<ARG_1 : Type, ARG_2 : Type>(
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

fun <ARG : Type> KClass<ARG>.extract(ctx: FunctionContext, idx: Int): ARG {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        AnyType::class -> ctx.getAny(idx) as ARG
        NumberType::class -> ctx.getNumberValue(idx) as ARG
        StringType::class -> ctx.getStringValue(idx) as ARG
        Type::class -> TODO() //FIXME loads the entire table from lua -- maybe some form of readonly table value and table value is interface?!
        TableMap::class -> DefaultTableProxy(idx, ctx.state, TableProxy.Type.Map) as ARG
        TableArray::class -> DefaultTableProxy(idx, ctx.state, TableProxy.Type.Array) as ARG
        else -> TODO()
    }
}

