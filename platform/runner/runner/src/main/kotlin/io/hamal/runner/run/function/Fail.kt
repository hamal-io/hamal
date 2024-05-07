package io.hamal.runner.run.function

import io.hamal.lib.common.value.Value
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.kua.ExitFailure
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.getNumber
import io.hamal.lib.kua.value.getTable
import io.hamal.lib.kua.value.toValueObject

internal object FailRunFunction : Function1In0Out<Value>(
    FunctionInput1Schema(Value::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: Value) {
        when (arg1) {
            is ValueError ->
                throw ExitFailure(
                    statusCode = ValueNumber(500),
                    result = ValueObject.builder().set("message", arg1.stringValue).build()
                )

            is KuaTable ->
                throw ExitFailure(
                    statusCode = arg1.getNumber("status_code"),
                    result = arg1.getTable("result").toValueObject()
                )

            else -> TODO()
        }
    }
}