package io.hamal.lib.kua

import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.Value

sealed interface FuncOutput {
    val size: Int
}

object FuncOutput0 : FuncOutput {
    override val size = 0
}

data class FuncOutput1<ARG_1 : Value>(
    val _1: ARG_1
) : FuncOutput {
    override val size = 1
}

data class FuncOutput2<ARG_1 : Value, ARG_2 : Value>(
    val _1: ARG_1,
    val _2: ARG_2
) : FuncOutput {
    override val size = 2
}


sealed interface FuncInput {
    val size: Int
}

object FuncInput0 : FuncInput {
    override val size = 0
}

data class FuncInput1<ARG_1 : Value>(
    val arg1: ARG_1
) : FuncInput {
    override val size = 1
}

interface FuncInputSchema

data class FuncInput1Schema<ARG_1 : Value>(
    val arg1Class: ARG_1
) : FuncInputSchema

data class FuncInput2<ARG_1 : Value, ARG_2 : Value>(
    val arg1: ARG_1,
    val arg2: ARG_2
) : FuncInput {
    override val size = 2
}

interface Func<INPUT : FuncInput, OUTPUT : FuncOutput> {
    operator fun invoke(ctx: Context, input: INPUT): OUTPUT
}

class Context


abstract class BaseFunc<INPUT : FuncInput, OUTPUT : FuncOutput>(
    val func: () -> INPUT
) : Func<INPUT, OUTPUT> {

    fun calledByLua(): Int {
        println("pretend this to be a lua call")
        val result: OUTPUT = invoke(Context(), func())
        println(result)
        return result.size
    }
}

class Magic : BaseFunc<FuncInput1<NumberValue>, FuncOutput2<NumberValue, NumberValue>>(
    func = { FuncInput1(NumberValue(23)) }
) {
    override fun invoke(ctx: Context, input: FuncInput1<NumberValue>): FuncOutput2<NumberValue, NumberValue> {
        input.arg1
        return FuncOutput2(input.arg1, input.arg1)
    }
}


fun main() {
    val result = Magic().calledByLua()
    println(result)
}