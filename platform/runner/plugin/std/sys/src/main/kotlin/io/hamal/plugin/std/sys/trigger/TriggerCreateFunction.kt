package io.hamal.plugin.std.sys.trigger

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain._enum.TriggerTypes
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.FuncId.Companion.FuncId
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.TopicId.Companion.TopicId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.findString
import io.hamal.lib.kua.value.getString
import io.hamal.lib.kua.value.type
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTriggerCreateReq

class TriggerCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {
            val res = sdk.trigger.create(
                arg1.findString("namespace_id")?.let { NamespaceId(SnowflakeId(it.stringValue)) }
                    ?: ctx[NamespaceId::class],
                ApiTriggerCreateReq(
                    type = TriggerTypes.valueOf(arg1.getString("type").stringValue),
                    funcId = FuncId(SnowflakeId(arg1.getString("func_id").stringValue)),
                    name = TriggerName(arg1.getString("name")),
                    inputs = TriggerInputs(),
                    duration = if (arg1.type("duration") == ValueString::class) {
                        TriggerDuration(arg1.getString("duration"))
                    } else {
                        null
                    },
                    topicId = if (arg1.type("topic_id") == ValueString::class) {
                        TopicId(SnowflakeId(arg1.getString("topic_id").stringValue))
                    } else {
                        null
                    },
                    cron = if (arg1.type("cron") == ValueString::class) {
                        CronPattern(arg1.getString("cron"))
                    } else {
                        null
                    }
                )
            )

            null to ctx.tableCreate(
                "request_id" to ValueString(res.requestId.stringValue),
                "request_status" to ValueString(res.requestStatus.stringValue),
                "id" to ValueString(res.id.stringValue),
                "workspace_id" to ValueString(res.workspaceId.stringValue),
                "namespace_id" to ValueString(res.namespaceId.stringValue)
            )

        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}