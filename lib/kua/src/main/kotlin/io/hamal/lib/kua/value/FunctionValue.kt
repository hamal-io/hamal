package io.hamal.lib.kua.value

import io.hamal.lib.kua.Bridge
import io.hamal.lib.kua.value.function.*

data class NamedFunctionValue<
        INPUT_SCHEMA : FunctionInputSchema<INPUT>,
        INPUT : FunctionInput<INPUT_SCHEMA, INPUT>,
        OUTPUT_SCHEMA : FunctionOutputSchema<OUTPUT>,
        OUTPUT : FunctionOutput<OUTPUT_SCHEMA, OUTPUT>
        >
    (
    val name: String,
    val func: FunctionValue<INPUT_SCHEMA, INPUT, OUTPUT_SCHEMA, OUTPUT>
)

interface FunctionValue<
        INPUT_SCHEMA : FunctionInputSchema<INPUT>,
        INPUT : FunctionInput<INPUT_SCHEMA, INPUT>,
        OUTPUT_SCHEMA : FunctionOutputSchema<OUTPUT>,
        OUTPUT : FunctionOutput<OUTPUT_SCHEMA, OUTPUT>
        > {

    val inputSchema: INPUT_SCHEMA
    val outputSchema: OUTPUT_SCHEMA

    operator fun invoke(ctx: Context, input: INPUT): OUTPUT

    fun invokedByLua(bridge: Bridge): Int {
        val ctx = Context(bridge)

        val input = inputSchema.createInput(ctx)
        val output = invoke(ctx, input)
        outputSchema.pushResult(ctx, output)
        return output.size
    }
}

abstract class Function0In0Out : FunctionValue<
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

abstract class Function1In0Out<
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


abstract class Function0In1Out<
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

abstract class Function0In2Out<
        OUTPUT_ARG_1 : Value,
        OUTPUT_ARG_2 : Value
        >(
    override val outputSchema: FunctionOutput2Schema<OUTPUT_ARG_1, OUTPUT_ARG_2>,
    ) : FunctionValue<
        FunctionInput0Schema,
        FunctionInput0,
        FunctionOutput2Schema<OUTPUT_ARG_1, OUTPUT_ARG_2>,
        FunctionOutput2<OUTPUT_ARG_1, OUTPUT_ARG_2>
        > {
    override val inputSchema: FunctionInput0Schema = FunctionInput0Schema
}

abstract class Function1In1Out<
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

