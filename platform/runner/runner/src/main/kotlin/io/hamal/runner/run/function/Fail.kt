package io.hamal.runner.run.function

import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaTableMap

internal object FailRunFunction : Function1In0Out<KuaTableMap>(
    FunctionInput1Schema(KuaTableMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTableMap) {
        throw ExitError(KuaNumber(1), arg1)
    }
}