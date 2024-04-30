package io.hamal.plugin.std.sys.topic

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.findTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTopicService
import io.hamal.lib.value.ValueString

class TopicListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            null to ctx.tableCreate(
                sdk.topic.list(
                    ApiTopicService.TopicQuery(
                        namespaceIds = arg1.findTable("namespace_ids")
                            ?.asList()
                            ?.map { NamespaceId((it as ValueString).stringValue) }
                            ?.toList()
                            ?: listOf(ctx[NamespaceId::class])
                    )
                ).map { topic ->
                    ctx.tableCreate(
                        "id" to ValueString(topic.id.value.value.toString(16)),
                        "name" to ValueString(topic.name.value),
                    )
                }
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}