package io.hamal.runner.run.function

import io.hamal.lib.common.serialization.serde.SerdeNumber
import io.hamal.lib.common.value.Value
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.toValueObject

internal object FailRunFunction : Function1In0Out<Value>(
    FunctionInput1Schema(Value::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: Value) {
        when (arg1) {
            is ValueError -> throw ExitError(SerdeNumber(1), arg1 as ValueObject)
            is KuaTable -> throw ExitError(SerdeNumber(1), arg1.toValueObject())
            else -> TODO()
        }
    }
}