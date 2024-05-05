package io.hamal.runner.run.function

import io.hamal.lib.kua.ExitComplete
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.getNumber
import io.hamal.lib.kua.value.getTable
import io.hamal.lib.kua.value.toValueObject

internal object CompleteRunFunction : Function1In0Out<KuaTable>(FunctionInput1Schema(KuaTable::class)) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable) {
        throw ExitComplete(
            statusCode = arg1.getNumber("status_code"),
            result = arg1.getTable("result").toValueObject()
        )
    }
}