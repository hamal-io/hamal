package io.hamal.lib.kua.value.function

import io.hamal.lib.kua.value.Value
import kotlin.reflect.KClass

sealed interface FunctionOutputSchema {
    val size: Int
}

object FunctionOutput0Schema : FunctionOutputSchema {
    override val size = 0
}

data class FunctionOutput1Schema<ARG_1 : Value>(
    val arg1: KClass<ARG_1>
) : FunctionOutputSchema {
    override val size = 1
}

data class FunctionOutput2Schema<ARG_1 : Value, ARG_2 : Value>(
    val arg1: KClass<ARG_1>,
    val arg2: KClass<ARG_2>
) : FunctionOutputSchema {
    override val size = 2
}