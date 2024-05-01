package io.hamal.plugin.std.debug

import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.common.value.ValueNumber

object SleepFunction : Function1In0Out<ValueNumber>(FunctionInput1Schema(ValueNumber::class)) {
    override fun invoke(ctx: FunctionContext, arg1: ValueNumber) {
        Thread.sleep(arg1.longValue)
    }
}