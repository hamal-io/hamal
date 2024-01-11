package io.hamal.runner.run.function

import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaNumber

internal object FailRunFunction : Function1In0Out<KuaMap>(
    FunctionInput1Schema(KuaMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaMap) {
        throw ExitError(KuaNumber(1), arg1)
    }
}