package io.hamal.extension.std.sys.topic

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.ApiTopicEntryList

class ListTopicEntryFunction(
    private val httpTemplate: HttpTemplate
) : Function1In2Out<StringType, ErrorType, ArrayType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, ArrayType?> {
        val entries = try {
            httpTemplate
                .get("/v1/topics/{topicId}/entries")
                .path("topicId", arg1.value)
                .execute(ApiTopicEntryList::class)
                .entries
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf()
        }

        return null to ArrayType(entries.mapIndexed { index, entry ->
            index to entry.payload.value
        }.toMap().toMutableMap())
    }
}