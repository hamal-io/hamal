package io.hamal.plugin.std.sys.topic

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.findString
import io.hamal.lib.kua.value.getString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTopicCreateRequest

class TopicCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {
            val res = sdk.topic.createTopic(
                arg1.findString("namespaceId")?.let { NamespaceId(SnowflakeId(it.stringValue)) }
                    ?: ctx[NamespaceId::class],
                ApiTopicCreateRequest(
                    name = TopicName(arg1.getString("name")),
                    type = TopicType.Namespace
                )
            )

            null to ctx.tableCreate(
                "request_id" to ValueString(res.requestId.stringValue),
                "request_status" to ValueString(res.requestStatus.stringValue),
                "id" to ValueString(res.id.stringValue),
                "type" to ValueString(res.type.name),
                "workspace_id" to ValueString(res.workspaceId.stringValue),
                "namespace_id" to ValueString(res.namespaceId.stringValue)
            )

        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}