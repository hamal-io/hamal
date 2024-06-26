package io.hamal.lib.kua.value

import io.hamal.lib.kua.Native
import io.hamal.lib.kua.StateImpl
import io.hamal.lib.kua.function.*

abstract class KuaFunction<
        INPUT_SCHEMA : FunctionInputSchema<INPUT>,
        INPUT : FunctionInput<INPUT_SCHEMA, INPUT>,
        OUTPUT_SCHEMA : FunctionOutputSchema<OUTPUT>,
        OUTPUT : FunctionOutput<OUTPUT_SCHEMA, OUTPUT>
        > : KuaValue {

    abstract val inputSchema: INPUT_SCHEMA
    abstract val outputSchema: OUTPUT_SCHEMA

    abstract operator fun invoke(ctx: FunctionContext, input: INPUT): OUTPUT

    fun invokedByLua(native: Native): Int {
        return StateImpl(native).let { state ->
            val ctx = FunctionContext(state)

            val input = inputSchema.createInput(ctx)
            val output = invoke(ctx, input)
            outputSchema.pushResult(ctx, output)
            output.size
        }
    }
}