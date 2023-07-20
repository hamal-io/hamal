package io.hamal.lib.kua.value.function

import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.Value
import kotlin.reflect.KClass

sealed interface FunctionInputSchema<INPUT : FunctionInput<*, *>> {
    val size: Int
    fun createInput(): INPUT
}

object FunctionInput0Schema : FunctionInputSchema<FunctionInput0> {
    override val size = 0
    override fun createInput(): FunctionInput0 {
        return FunctionInput0
    }
}

data class FunctionInput1Schema<ARG_1 : Value>(
    val arg1Class: KClass<ARG_1>
) : FunctionInputSchema<FunctionInput1<ARG_1>> {
    override val size = 1
    override fun createInput(): FunctionInput1<ARG_1> {
        return FunctionInput1(
            arg1Class.extract(1)
        )
    }
}

data class FunctionInput2Schema<ARG_1 : Value, ARG_2 : Value>(
    val arg1Class: KClass<ARG_1>,
    val arg2Class: KClass<ARG_2>
) : FunctionInputSchema<FunctionInput2<ARG_1, ARG_2>> {
    override val size = 1
    override fun createInput(): FunctionInput2<ARG_1, ARG_2> {
        return FunctionInput2(
            arg1Class.extract(1),
            arg2Class.extract(2)
        )
    }
}


fun <ARG : Value> KClass<ARG>.extract(index: Int): ARG {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        NumberValue::class -> NumberValue(index) as ARG
        else -> TODO()
    }
}
