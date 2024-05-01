package io.hamal.plugin.std.sys.trigger

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
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
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString

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
                    type = TriggerType.valueOf(arg1.getString("type").stringValue),
                    funcId = FuncId(SnowflakeId(arg1.getString("func_id").stringValue)),
                    name = TriggerName(arg1.getString("name").stringValue),
                    inputs = TriggerInputs(),
                    duration = if (arg1.type("duration") == ValueString::class) {
                        TriggerDuration(arg1.getString("duration").stringValue)
                    } else {
                        null
                    },
                    topicId = if (arg1.type("topic_id") == ValueString::class) {
                        TopicId(SnowflakeId(arg1.getString("topic_id").stringValue))
                    } else {
                        null
                    },
                    cron = if (arg1.type("cron") == ValueString::class) {
                        CronPattern(arg1.getString("cron").stringValue)
                    } else {
                        null
                    }
                )
            )

            null to ctx.tableCreate(
                "request_id" to ValueString(res.requestId.value.value.toString(16)),
                "request_status" to ValueString(res.requestStatus.name),
                "id" to ValueString(res.id.value.value.toString(16)),
                "workspace_id" to ValueString(res.workspaceId.value.value.toString(16)),
                "namespace_id" to ValueString(res.namespaceId.value.value.toString(16))
            )

        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}