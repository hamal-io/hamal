package io.hamal.runner.run.function

import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.function.Function2In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.toValueObject
import io.hamal.runner.run.RunnerContext

internal class EmitFunction(
    private val executionCtx: RunnerContext
) : Function2In0Out<ValueString, KuaTable>(FunctionInput2Schema(ValueString::class, KuaTable::class)) {

    override fun invoke(ctx: FunctionContext, arg1: ValueString, arg2: KuaTable) {
        executionCtx.emit(EventToSubmit(TopicName(arg1), EventPayload(arg2.toValueObject())))
    }
}