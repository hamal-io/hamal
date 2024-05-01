package io.hamal.runner.run.function

import io.hamal.lib.common.hot.HotNumber
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.toHotObject
import io.hamal.lib.common.value.Value
import io.hamal.lib.common.value.ValueError

internal object FailRunFunction : Function1In0Out<Value>(
    FunctionInput1Schema(Value::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: Value) {
        when (arg1) {
            is ValueError -> throw ExitError(HotNumber(1), arg1.toHotObject())
            is KuaTable -> throw ExitError(HotNumber(1), arg1.toHotObject())
            else -> TODO()
        }
    }
}