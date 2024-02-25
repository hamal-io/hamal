package io.hamal.runner.run.function

import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaTable

internal object FailRunFunction : Function1In0Out<KuaTable.Map>(
    FunctionInput1Schema(KuaTable.Map::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable.Map) {
        throw ExitError(KuaNumber(1), arg1)
    }
}