package io.hamal.lib.kua.function

import io.hamal.lib.kua.value.KuaFunction
import io.hamal.lib.common.value.Value


abstract class Function0In0Out : KuaFunction<
        FunctionInput0Schema,
        FunctionInput0,
        FunctionOutput0Schema,
        FunctionOutput0
        >() {

    override val inputSchema: FunctionInput0Schema = FunctionInput0Schema
    override val outputSchema: FunctionOutput0Schema = FunctionOutput0Schema

    abstract fun invoke(ctx: FunctionContext)

    override fun invoke(ctx: FunctionContext, input: FunctionInput0): FunctionOutput0 {
        this.invoke(ctx)
        return FunctionOutput0
    }

}

abstract class Function1In0Out<INPUT_ARG_1 : Value>(
    override val inputSchema: FunctionInput1Schema<INPUT_ARG_1>
) : KuaFunction<
        FunctionInput1Schema<INPUT_ARG_1>,
        FunctionInput1<INPUT_ARG_1>,
        FunctionOutput0Schema,
        FunctionOutput0
        >() {
    override val outputSchema: FunctionOutput0Schema = FunctionOutput0Schema

    abstract fun invoke(ctx: FunctionContext, arg1: INPUT_ARG_1)

    override fun invoke(ctx: FunctionContext, input: FunctionInput1<INPUT_ARG_1>): FunctionOutput0 {
        invoke(ctx, input.arg1)
        return FunctionOutput0
    }
}


abstract class Function0In1Out<OUTPUT_ARG_1 : Value>(
    override val outputSchema: FunctionOutput1Schema<OUTPUT_ARG_1>,

    ) : KuaFunction<
        FunctionInput0Schema,
        FunctionInput0,
        FunctionOutput1Schema<OUTPUT_ARG_1>,
        FunctionOutput1<OUTPUT_ARG_1>
        >() {
    override val inputSchema: FunctionInput0Schema = FunctionInput0Schema

    abstract fun invoke(ctx: FunctionContext): OUTPUT_ARG_1?

    override fun invoke(ctx: FunctionContext, input: FunctionInput0): FunctionOutput1<OUTPUT_ARG_1> {
        return FunctionOutput1(invoke(ctx))
    }
}

abstract class Function0In2Out<
        OUTPUT_ARG_1 : Value,
        OUTPUT_ARG_2 : Value
        >(
    override val outputSchema: FunctionOutput2Schema<OUTPUT_ARG_1, OUTPUT_ARG_2>,
) : KuaFunction<
        FunctionInput0Schema,
        FunctionInput0,
        FunctionOutput2Schema<OUTPUT_ARG_1, OUTPUT_ARG_2>,
        FunctionOutput2<OUTPUT_ARG_1, OUTPUT_ARG_2>
        >() {
    override val inputSchema: FunctionInput0Schema = FunctionInput0Schema

    abstract fun invoke(ctx: FunctionContext): Pair<OUTPUT_ARG_1?, OUTPUT_ARG_2?>

    override fun invoke(ctx: FunctionContext, input: FunctionInput0): FunctionOutput2<OUTPUT_ARG_1, OUTPUT_ARG_2> {
        val result = invoke(ctx)
        return FunctionOutput2(result.first, result.second)
    }
}

abstract class Function1In1Out<
        INPUT_ARG_1 : Value,
        OUTPUT_ARG_1 : Value
        >(
    override val inputSchema: FunctionInput1Schema<INPUT_ARG_1>,
    override val outputSchema: FunctionOutput1Schema<OUTPUT_ARG_1>
) : KuaFunction<
        FunctionInput1Schema<INPUT_ARG_1>,
        FunctionInput1<INPUT_ARG_1>,
        FunctionOutput1Schema<OUTPUT_ARG_1>,
        FunctionOutput1<OUTPUT_ARG_1>
        >() {

    abstract fun invoke(ctx: FunctionContext, arg1: INPUT_ARG_1): OUTPUT_ARG_1?

    override fun invoke(ctx: FunctionContext, input: FunctionInput1<INPUT_ARG_1>): FunctionOutput1<OUTPUT_ARG_1> {
        val result = invoke(ctx, input.arg1)
        return FunctionOutput1(result)
    }
}

abstract class Function1In2Out<
        INPUT_ARG_1 : Value,
        OUTPUT_ARG_1 : Value,
        OUTPUT_ARG_2 : Value
        >(
    override val inputSchema: FunctionInput1Schema<INPUT_ARG_1>,
    override val outputSchema: FunctionOutput2Schema<OUTPUT_ARG_1, OUTPUT_ARG_2>
) : KuaFunction<
        FunctionInput1Schema<INPUT_ARG_1>,
        FunctionInput1<INPUT_ARG_1>,
        FunctionOutput2Schema<OUTPUT_ARG_1, OUTPUT_ARG_2>,
        FunctionOutput2<OUTPUT_ARG_1, OUTPUT_ARG_2>
        >() {

    abstract fun invoke(ctx: FunctionContext, arg1: INPUT_ARG_1): Pair<OUTPUT_ARG_1?, OUTPUT_ARG_2?>

    override fun invoke(
        ctx: FunctionContext,
        input: FunctionInput1<INPUT_ARG_1>
    ): FunctionOutput2<OUTPUT_ARG_1, OUTPUT_ARG_2> {
        val result = invoke(ctx, input.arg1)
        return FunctionOutput2(result.first, result.second)
    }
}

abstract class Function2In0Out<
        INPUT_ARG_1 : Value,
        INPUT_ARG_2 : Value
        >(
    override val inputSchema: FunctionInput2Schema<INPUT_ARG_1, INPUT_ARG_2>
) : KuaFunction<
        FunctionInput2Schema<INPUT_ARG_1, INPUT_ARG_2>,
        FunctionInput2<INPUT_ARG_1, INPUT_ARG_2>,
        FunctionOutput0Schema,
        FunctionOutput0
        >() {
    override val outputSchema = FunctionOutput0Schema

    abstract fun invoke(ctx: FunctionContext, arg1: INPUT_ARG_1, arg2: INPUT_ARG_2)

    override fun invoke(ctx: FunctionContext, input: FunctionInput2<INPUT_ARG_1, INPUT_ARG_2>): FunctionOutput0 {
        invoke(ctx, input.arg1, input.arg2)
        return FunctionOutput0
    }
}

abstract class Function2In1Out<
        INPUT_ARG_1 : Value,
        INPUT_ARG_2 : Value,
        OUTPUT_ARG_1 : Value,
        >(
    override val inputSchema: FunctionInput2Schema<INPUT_ARG_1, INPUT_ARG_2>,
    override val outputSchema: FunctionOutput1Schema<OUTPUT_ARG_1>

) : KuaFunction<
        FunctionInput2Schema<INPUT_ARG_1, INPUT_ARG_2>,
        FunctionInput2<INPUT_ARG_1, INPUT_ARG_2>,
        FunctionOutput1Schema<OUTPUT_ARG_1>,
        FunctionOutput1<OUTPUT_ARG_1>
        >() {

    abstract fun invoke(ctx: FunctionContext, arg1: INPUT_ARG_1, arg2: INPUT_ARG_2): OUTPUT_ARG_1?

    override fun invoke(
        ctx: FunctionContext,
        input: FunctionInput2<INPUT_ARG_1, INPUT_ARG_2>
    ): FunctionOutput1<OUTPUT_ARG_1> {
        val result = invoke(ctx, input.arg1, input.arg2)
        return FunctionOutput1(result)
    }
}


abstract class Function2In2Out<
        INPUT_ARG_1 : Value,
        INPUT_ARG_2 : Value,
        OUTPUT_ARG_1 : Value,
        OUTPUT_ARG_2 : Value
        >(
    override val inputSchema: FunctionInput2Schema<INPUT_ARG_1, INPUT_ARG_2>,
    override val outputSchema: FunctionOutput2Schema<OUTPUT_ARG_1, OUTPUT_ARG_2>

) : KuaFunction<
        FunctionInput2Schema<INPUT_ARG_1, INPUT_ARG_2>,
        FunctionInput2<INPUT_ARG_1, INPUT_ARG_2>,
        FunctionOutput2Schema<OUTPUT_ARG_1, OUTPUT_ARG_2>,
        FunctionOutput2<OUTPUT_ARG_1, OUTPUT_ARG_2>
        >() {

    abstract fun invoke(ctx: FunctionContext, arg1: INPUT_ARG_1, arg2: INPUT_ARG_2): Pair<OUTPUT_ARG_1?, OUTPUT_ARG_2?>

    override fun invoke(
        ctx: FunctionContext,
        input: FunctionInput2<INPUT_ARG_1, INPUT_ARG_2>
    ): FunctionOutput2<OUTPUT_ARG_1, OUTPUT_ARG_2> {
        val result = invoke(ctx, input.arg1, input.arg2)
        return FunctionOutput2(result.first, result.second)
    }
}
