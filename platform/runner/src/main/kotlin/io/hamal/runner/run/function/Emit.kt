package io.hamal.runner.run.function

import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.EventToSubmit
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.function.Function2In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.runner.run.RunnerContext

internal class EmitFunction(
    private val executionCtx: RunnerContext
) : Function2In0Out<KuaString, KuaMap>(
    FunctionInput2Schema(KuaString::class, KuaMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString, arg2: KuaMap) {
        executionCtx.emit(EventToSubmit(TopicName(arg1.value), EventPayload(arg2)))
    }
}