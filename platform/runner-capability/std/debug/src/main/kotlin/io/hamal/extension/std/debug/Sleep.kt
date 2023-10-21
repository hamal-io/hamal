package io.hamal.extension.std.debug

import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.NumberType

object SleepFunction : Function1In0Out<NumberType>(FunctionInput1Schema(NumberType::class)) {
    override fun invoke(ctx: FunctionContext, arg1: NumberType) {
        Thread.sleep(arg1.value.toLong())
    }
}