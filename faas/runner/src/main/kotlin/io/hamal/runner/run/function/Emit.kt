package io.hamal.runner.extension.ctx.function

import io.hamal.lib.domain.Event
import io.hamal.lib.kua.function.Function2In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.*
import io.hamal.runner.run.RunnerSandboxContext

class EmitFunction(
    private val executionCtx: RunnerSandboxContext
) : Function2In0Out<StringType, TableProxyMap>(
    FunctionInput2Schema(StringType::class, TableProxyMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType, arg2: TableProxyMap) {
        ctx.pushNil()

        val eventMap = mutableMapOf<StringType, SerializableType>()

        while (ctx.state.native.tableNext(arg2.index)) {
            val k = ctx.getStringType(-2)
            val v = ctx.getAny(-1)
            when (val n = v.value) {
                is NilType -> eventMap[k] = n
                is NumberType -> eventMap[k] = n
                is StringType -> eventMap[k] = n
                is BooleanType -> eventMap[k] = n

                else -> TODO()
            }
            ctx.native.pop(1)
        }


        // FIXME make sure topic is set and string
        require(eventMap.containsKey(StringType("topic"))) { "Topic not present" }

        executionCtx.emit(Event(TableType(eventMap)))
    }
}