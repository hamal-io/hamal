package io.hamal.lib.kua.function

import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.*
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

data class FunctionInput1Schema<ARG_1 : KuaType>(
    val arg1Class: KClass<ARG_1>
) : FunctionInputSchema<FunctionInput1<ARG_1>> {
    override val size = 1
    override fun createInput(ctx: FunctionContext): FunctionInput1<ARG_1> {
        return FunctionInput1(
            arg1Class.extract(ctx, 1)
        )
    }
}

data class FunctionInput2Schema<ARG_1 : KuaType, ARG_2 : KuaType>(
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

fun <ARG : KuaType> KClass<ARG>.extract(ctx: FunctionContext, idx: Int): ARG {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        KuaAny::class -> ctx.getAny(idx) as ARG
        KuaArray::class -> ctx.getArrayType(idx) as ARG
        KuaMap::class -> ctx.getMapType(idx) as ARG
        KuaNumber::class -> ctx.getNumberType(idx) as ARG
        KuaString::class -> ctx.getStringType(idx) as ARG
        KuaType::class -> TODO() //FIXME loads the entire table from lua -- maybe some form of readonly table value and table value is interface?!
//        TableProxyMap::class -> DefaultTableProxy(idx, ctx.state, TableProxy.Mode.Map) as ARG
//        TableProxyArray::class -> DefaultTableProxy(idx, ctx.state, TableProxy.Mode.Array) as ARG
        TableProxyMap::class -> ctx.getTableMapProxy(idx) as ARG
        TableProxyArray::class -> ctx.getTableArrayProxy(idx) as ARG
        else -> TODO()
    }
}

