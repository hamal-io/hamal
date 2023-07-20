package io.hamal.lib.kua.value.function

import io.hamal.lib.kua.value.Value

interface FunctionOutput<SCHEMA : FunctionOutputSchema> {
    val size: Int
}

object FunctionOutput0 : FunctionOutput<FunctionOutput0Schema> {
    override val size = 0
}

data class FunctionOutput1<ARG_1 : Value>(
    val arg1: ARG_1
) : FunctionOutput<FunctionOutput1Schema<ARG_1>> {
    override val size = 1
}

