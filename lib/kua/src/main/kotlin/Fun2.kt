import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.Value
import kotlin.reflect.KClass

sealed interface FuncOutputSchema {
    val size: Int
}

object FuncOutput0Schema : FuncOutputSchema {
    override val size = 0
}

data class FuncOutput1Schema<ARG_1 : Value>(
    val _1: KClass<ARG_1>
) : FuncOutputSchema {
    override val size = 1
}

data class FuncOutput2Schema<ARG_1 : Value, ARG_2 : Value>(
    val _1: KClass<ARG_1>,
    val _2: KClass<ARG_2>
) : FuncOutputSchema {
    override val size = 2
}


sealed interface FuncInputSchema<INPUT : TestInput<*, *>> {
    val size: Int
    fun createInput(): INPUT
}


data class FuncInput1Schema<ARG_1 : Value>(
    val arg1Class: KClass<ARG_1>
) : FuncInputSchema<TestInput1<ARG_1>> {
    override val size = 1

    override fun createInput(): TestInput1<ARG_1> {
        return TestInput1(
            arg1Class.extract()
        )
    }
}

fun <ARG : Value> KClass<ARG>.extract(): ARG {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        NumberValue::class -> NumberValue(23.0) as ARG
        else -> TODO()
    }
}

//data class FuncInput2Schema<ARG_1 : Value, ARG_2 : Value>(
//    val arg1: ARG_1,
//    val arg2: ARG_2
//) : FuncInputSchema {
//    override val size = 2
//}

interface TestInput<SCHEMA : FuncInputSchema<INPUT>, INPUT : TestInput<SCHEMA, INPUT>>

data class TestInput1<ARG_1 : Value>(
    val arg1: ARG_1
) : TestInput<FuncInput1Schema<ARG_1>, TestInput1<ARG_1>>


interface TestOutput<SCHEMA : FuncOutputSchema> {
    val size: Int
}

data class TestOutput1<ARG_1 : Value>(
    val arg1: ARG_1
) : TestOutput<FuncOutput1Schema<ARG_1>> {
    override val size = 1
}


interface Func<
        INPUT_SCHEMA : FuncInputSchema<INPUT>,
        INPUT : TestInput<INPUT_SCHEMA, INPUT>,
        OUTPUT_SCHEMA : FuncOutputSchema,
        OUTPUT : TestOutput<OUTPUT_SCHEMA>> {
    operator fun invoke(ctx: Context, input: INPUT): OUTPUT

    val inputSchema: INPUT_SCHEMA

    fun calledByLua(): Int {
        println("pretend this to be a lua call")
        val input = inputSchema.createInput()
        val result = invoke(Context(), input)
        // FIXME ensure result
        return result.size
    }
}

class Context


abstract class NewFunc1Param1Result<
        INPUT_ARG_1 : Value,
        OUTPUT_ARG_1 : Value
        >(
    override val inputSchema: FuncInput1Schema<INPUT_ARG_1>,
    val outputSchema: FuncOutput1Schema<OUTPUT_ARG_1>
) : Func<
        FuncInput1Schema<INPUT_ARG_1>,
        TestInput1<INPUT_ARG_1>,
        FuncOutput1Schema<OUTPUT_ARG_1>,
        TestOutput1<OUTPUT_ARG_1>
        >


class Magic : NewFunc1Param1Result<NumberValue, NumberValue>(
    FuncInput1Schema(NumberValue::class),
    FuncOutput1Schema(NumberValue::class)
) {
    override fun invoke(ctx: Context, input: TestInput1<NumberValue>): TestOutput1<NumberValue> {
        println("Returns ${input.arg1.value}")
        return TestOutput1(NumberValue(input.arg1.value))
    }
}


fun main() {
    val result = Magic().calledByLua()
    println(result)
}