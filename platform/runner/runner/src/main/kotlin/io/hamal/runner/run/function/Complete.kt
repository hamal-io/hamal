package io.hamal.runner.run.function

import io.hamal.lib.common.serialization.json.JsonNumber
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.toValueObject

internal object CompleteRunFunction : Function1In0Out<KuaTable>(FunctionInput1Schema(KuaTable::class)) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable) {
        throw ExitError(JsonNumber(0), arg1.toValueObject())
    }
}