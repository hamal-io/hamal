package io.hamal.lib.kua.type

import io.hamal.lib.kua.ClosableState
import io.hamal.lib.kua.Native
import io.hamal.lib.kua.function.*

abstract class KuaFunction<
        INPUT_SCHEMA : FunctionInputSchema<INPUT>,
        INPUT : FunctionInput<INPUT_SCHEMA, INPUT>,
        OUTPUT_SCHEMA : FunctionOutputSchema<OUTPUT>,
        OUTPUT : FunctionOutput<OUTPUT_SCHEMA, OUTPUT>
        > : KuaType {

    abstract val inputSchema: INPUT_SCHEMA
    abstract val outputSchema: OUTPUT_SCHEMA

    abstract operator fun invoke(ctx: FunctionContext, input: INPUT): OUTPUT

    fun invokedByLua(native: Native): Int {
        val ctx = FunctionContext(ClosableState(native))

        val input = inputSchema.createInput(ctx)
        val output = invoke(ctx, input)
        outputSchema.pushResult(ctx, output)
        return output.size
    }

    override val type: KuaType.Type = KuaType.Type.Function
}