package io.hamal.extension.std.sys.topic

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.domain.ApiTopicList

class ListTopicFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In2Out<ErrorType, TableProxyArray>(
    FunctionOutput2Schema(ErrorType::class, TableProxyArray::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, TableProxyArray?> {
        val topicList = try {
            templateSupplier()
                .get("/v1/topics")
                .execute(ApiTopicList::class)
                .topics
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf<ApiTopicList.Topic>()
        }

        return null to ctx.tableCreateArray().also { rs ->
            topicList.forEach { topic ->
                val inner = ctx.tableCreateMap(2)
                inner["id"] = topic.id
                inner["name"] = topic.name.value
                rs.append(inner)
            }
        }
    }
}