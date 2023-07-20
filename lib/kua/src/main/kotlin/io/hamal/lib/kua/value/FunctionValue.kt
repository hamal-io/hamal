package io.hamal.lib.kua.value

import io.hamal.lib.kua.Bridge
import io.hamal.lib.kua.value.function.*

data class NamedFunctionValue<
        INPUT_SCHEMA : FunctionInputSchema<INPUT>,
        INPUT : FunctionInput<INPUT_SCHEMA, INPUT>,
        OUTPUT_SCHEMA : FunctionOutputSchema,
        OUTPUT : FunctionOutput<OUTPUT_SCHEMA>
        >
    (
    val name: String,
    val func: FunctionValue<INPUT_SCHEMA, INPUT, OUTPUT_SCHEMA, OUTPUT>
)

interface FunctionValue<
        INPUT_SCHEMA : FunctionInputSchema<INPUT>,
        INPUT : FunctionInput<INPUT_SCHEMA, INPUT>,
        OUTPUT_SCHEMA : FunctionOutputSchema,
        OUTPUT : FunctionOutput<OUTPUT_SCHEMA>
        > {

    val inputSchema: INPUT_SCHEMA
    val outputSchema: OUTPUT_SCHEMA

    operator fun invoke(ctx: Context, input: INPUT): OUTPUT

    fun invokedByLua(bridge: Bridge): Int {
        println("pretend this to be a lua call")
        val input = inputSchema.createInput()
        val result = invoke(Context(), input)
        // FIXME ensure result
        bridge.pushString("temp")
        return result.size
    }
}

abstract class Function0Param0Result : FunctionValue<
        FunctionInput0Schema,
        FunctionInput0,
        FunctionOutput0Schema,
        FunctionOutput0
        > {

    override val inputSchema: FunctionInput0Schema = FunctionInput0Schema
    override val outputSchema: FunctionOutput0Schema = FunctionOutput0Schema

    override fun invoke(ctx: Context, input: FunctionInput0): FunctionOutput0 {
        run(ctx, input)
        return FunctionOutput0
    }

    abstract fun run(ctx: Context, input: FunctionInput0)
}

abstract class Function1Param0Result<
        INPUT_ARG_1 : Value
        >(
    override val inputSchema: FunctionInput1Schema<INPUT_ARG_1>
) : FunctionValue<
        FunctionInput1Schema<INPUT_ARG_1>,
        FunctionInput1<INPUT_ARG_1>,
        FunctionOutput0Schema,
        FunctionOutput0
        > {
    override val outputSchema: FunctionOutput0Schema = FunctionOutput0Schema
}


abstract class Function0Param1Result<
        OUTPUT_ARG_1 : Value
        >(
    override val outputSchema: FunctionOutput1Schema<OUTPUT_ARG_1>,

    ) : FunctionValue<
        FunctionInput0Schema,
        FunctionInput0,
        FunctionOutput1Schema<OUTPUT_ARG_1>,
        FunctionOutput1<OUTPUT_ARG_1>
        > {
    override val inputSchema: FunctionInput0Schema = FunctionInput0Schema
}


abstract class Function1Param1Result<
        INPUT_ARG_1 : Value,
        OUTPUT_ARG_1 : Value
        >(
    override val inputSchema: FunctionInput1Schema<INPUT_ARG_1>,
    override val outputSchema: FunctionOutput1Schema<OUTPUT_ARG_1>
) : FunctionValue<
        FunctionInput1Schema<INPUT_ARG_1>,
        FunctionInput1<INPUT_ARG_1>,
        FunctionOutput1Schema<OUTPUT_ARG_1>,
        FunctionOutput1<OUTPUT_ARG_1>
        >

