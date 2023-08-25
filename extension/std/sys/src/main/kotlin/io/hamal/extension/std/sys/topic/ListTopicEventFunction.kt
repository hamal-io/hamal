package io.hamal.extension.std.sys.topic

import io.hamal.lib.domain.EventWithId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.domain.ApiTopicEventList

class ListTopicEventFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In2Out<StringType, ErrorType, ArrayType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, ArrayType?> {
        val eventList = try {
            templateSupplier()
                .get("/v1/topics/{topicId}/events")
                .path("topicId", arg1.value)
                .execute(ApiTopicEventList::class)
                .events
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf<EventWithId>()
        }

        return null to ArrayType(eventList.mapIndexed { index, event ->
            index to event.value
        }.toMap().toMutableMap())
    }
}