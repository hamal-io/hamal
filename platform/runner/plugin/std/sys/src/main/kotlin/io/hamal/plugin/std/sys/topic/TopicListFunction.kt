package io.hamal.plugin.std.sys.topic

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.toArray
import io.hamal.lib.kua.toMap
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTopicService

class TopicListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTableMap, KuaError, KuaTableArray>(
    FunctionInput1Schema(KuaTableMap::class),
    FunctionOutput2Schema(KuaError::class, KuaTableArray::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTableMap): Pair<KuaError?, KuaTableArray?> {
        return try {
            null to ctx.toArray(
                sdk.topic.list(
                    ApiTopicService.TopicQuery(
                        namespaceIds = arg1.findArray("namespace_ids")
                            ?.asSequence()
                            ?.map { NamespaceId((it as KuaString).value) }
                            ?.toList()
                            ?: listOf(ctx[NamespaceId::class])
                    )
                ).map { topic ->
                    ctx.toMap(
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