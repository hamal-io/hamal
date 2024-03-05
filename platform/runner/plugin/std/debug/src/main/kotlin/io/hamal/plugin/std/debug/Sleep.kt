package io.hamal.plugin.std.debug

import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.KuaNumber

object SleepFunction : Function1In0Out<KuaNumber>(FunctionInput1Schema(KuaNumber::class)) {
    override fun invoke(ctx: FunctionContext, arg1: KuaNumber) {
        Thread.sleep(arg1.longValue)
    }
}