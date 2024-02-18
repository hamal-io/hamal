package io.hamal.runner.run.function

import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.EventToSubmit
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.toHotObject
import io.hamal.runner.run.RunnerContext

internal class EmitFunction(
    private val executionCtx: RunnerContext
) : Function1In0Out<KuaMap>(
    FunctionInput1Schema(KuaMap::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: KuaMap) {
        val topic = TopicName(arg1.getString("topic"))
        executionCtx.emit(EventToSubmit(topic, EventPayload(arg1.toHotObject())))
    }
}