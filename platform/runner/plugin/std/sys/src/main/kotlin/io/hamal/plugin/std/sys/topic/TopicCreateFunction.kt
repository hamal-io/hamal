package io.hamal.plugin.std.sys.topic

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTopicCreateRequest

class TopicCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            val res = sdk.topic.createTopic(
                arg1.findString("namespaceId")?.let { NamespaceId(SnowflakeId(it.stringValue)) }
                    ?: ctx[NamespaceId::class],
                ApiTopicCreateRequest(
                    name = TopicName(arg1.getString("name").stringValue),
                    type = TopicType.Namespace
                )
            )

            null to ctx.tableCreate(
                "request_id" to KuaString(res.requestId.value.value.toString(16)),
                "request_status" to KuaString(res.requestStatus.name),
                "id" to KuaString(res.id.value.value.toString(16)),
                "type" to KuaString(res.type.name),
                "workspace_id" to KuaString(res.workspaceId.value.value.toString(16)),
                "namespace_id" to KuaString(res.namespaceId.value.value.toString(16))
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}