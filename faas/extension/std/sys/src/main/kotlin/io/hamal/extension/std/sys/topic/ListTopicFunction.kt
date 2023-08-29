package io.hamal.extension.std.sys.topic

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.domain.ApiTopicList

class ListTopicFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In2Out<ErrorType, ArrayType>(
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
        val topicList = try {
            templateSupplier()
                .get("/v1/topics")
                .execute(ApiTopicList::class)
                .topics
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf<ApiTopicList.Topic>()
        }

        return null to ArrayType(
            topicList.mapIndexed { index, topic ->
                index to MapType(
                    mutableMapOf(
                        "id" to StringType(topic.id.value.value.toString(16)),
                        "name" to StringType(topic.name.value),
                    )
                )
            }.toMap().toMutableMap()
        )
    }
}