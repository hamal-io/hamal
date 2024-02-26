package io.hamal.plugin.std.sys.topic

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.createTable
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTopicService

class TopicListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            null to ctx.createTable(
                sdk.topic.list(
                    ApiTopicService.TopicQuery(
                        namespaceIds = arg1.findArray("namespace_ids")
                            ?.asSequence()
                            ?.map { NamespaceId((it as KuaString).value) }
                            ?.toList()
                            ?: listOf(ctx[NamespaceId::class])
                    )
                ).map { topic ->
                    ctx.createTable(
                        "id" to KuaString(topic.id.value.value.toString(16)),
                        "name" to KuaString(topic.name.value),
                    )
                }
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}