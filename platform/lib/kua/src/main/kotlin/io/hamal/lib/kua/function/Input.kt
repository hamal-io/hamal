package io.hamal.lib.kua.function

import io.hamal.lib.kua.type.KuaType


interface FunctionInput<SCHEMA : FunctionInputSchema<INPUT>, INPUT : FunctionInput<SCHEMA, INPUT>>

object FunctionInput0 : FunctionInput<FunctionInput0Schema, FunctionInput0>

data class FunctionInput1<ARG_1 : KuaType>(
    val arg1: ARG_1
) : FunctionInput<FunctionInput1Schema<ARG_1>, FunctionInput1<ARG_1>>

data class FunctionInput2<ARG_1 : KuaType, ARG_2 : KuaType>(
    val arg1: ARG_1,
    val arg2: ARG_2
) : FunctionInput<FunctionInput2Schema<ARG_1, ARG_2>, FunctionInput2<ARG_1, ARG_2>>
