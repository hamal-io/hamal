package io.hamal.lib.kua.function

import io.hamal.lib.kua.*
import io.hamal.lib.kua.type.*
import io.hamal.lib.kua.type.KuaError
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
        KuaBoolean::class -> ctx.booleanGet(idx) as ARG
        KuaError::class -> ctx.errorGet(idx) as ARG
        KuaNumber::class -> ctx.numberGet(idx) as ARG
        KuaString::class -> ctx.stringGet(idx) as ARG
        KuaTable::class -> ctx.tableGet(idx) as ARG
        KuaType::class -> ctx.get(idx) as ARG
        else -> TODO()
    }
}

