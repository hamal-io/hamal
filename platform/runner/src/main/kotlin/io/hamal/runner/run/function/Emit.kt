package io.hamal.runner.run.function

import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.EventToSubmit
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.function.Function2In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.runner.run.RunnerContext

internal class EmitFunction(
    private val executionCtx: RunnerContext
) : Function2In0Out<StringType, MapType>(
    FunctionInput2Schema(StringType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType, arg2: MapType) {
        executionCtx.emit(EventToSubmit(TopicName(arg1.value), EventPayload(arg2)))
    }
}