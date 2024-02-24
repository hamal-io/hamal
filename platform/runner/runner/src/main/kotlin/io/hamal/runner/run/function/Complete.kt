package io.hamal.runner.run.function

import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.KuaNumber

internal object CompleteRunFunction : Function1In0Out<KuaTable>(FunctionInput1Schema(KuaTable::class)) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable) {
        throw ExitError(KuaNumber(0), arg1)
    }
}