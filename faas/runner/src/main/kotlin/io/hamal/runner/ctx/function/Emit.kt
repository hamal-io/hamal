package io.hamal.runner.ctx.function

import io.hamal.lib.domain.Event
import io.hamal.lib.kua.function.Function2In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.table.TableTypeMap
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.SerializableType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.TableType

class EmitFunction(
    val eventsCollector: MutableList<Event>
) : Function2In0Out<StringType, TableTypeMap>(
    FunctionInput2Schema(StringType::class, TableTypeMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType, arg2: TableTypeMap) {
        ctx.pushNil()

        val eventMap = mutableMapOf<StringType, SerializableType>()

        while (ctx.state.native.tableNext(arg2.index)) {
            val k = ctx.getStringValue(-2)
            val v = ctx.getAny(-1)
            when (val n = v.value) {
                is NumberType -> eventMap[k] = n
                is StringType -> eventMap[k] = n
                else -> TODO()
            }
            ctx.native.pop(1)
        }


        // FIXME make sure topic is set and string
        require(eventMap.containsKey(StringType("topic")))

        eventsCollector.add(Event(TableType(eventMap)))
    }
}