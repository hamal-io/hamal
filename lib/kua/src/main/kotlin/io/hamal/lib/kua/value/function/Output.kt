package io.hamal.lib.kua.value.function

import io.hamal.lib.kua.value.Value

interface FunctionOutput<SCHEMA : FunctionOutputSchema<OUTPUT>, OUTPUT : FunctionOutput<SCHEMA, OUTPUT>> {
    val size: Int
}

object FunctionOutput0 : FunctionOutput<FunctionOutput0Schema, FunctionOutput0> {
    override val size = 0
}

data class FunctionOutput1<ARG_1 : Value>(
    val arg1: ARG_1
) : FunctionOutput<FunctionOutput1Schema<ARG_1>, FunctionOutput1<ARG_1>> {
    override val size = 1
}

data class FunctionOutput2<ARG_1 : Value, ARG_2 : Value>(
    val arg1: ARG_1,
    val arg2: ARG_2
) : FunctionOutput<FunctionOutput2Schema<ARG_1, ARG_2>, FunctionOutput2<ARG_1, ARG_2>> {
    override val size = 2
}

