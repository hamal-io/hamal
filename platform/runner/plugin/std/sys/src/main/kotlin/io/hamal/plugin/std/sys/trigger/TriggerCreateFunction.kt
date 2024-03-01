package io.hamal.plugin.std.sys.trigger

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTriggerCreateReq

class TriggerCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            val res = sdk.trigger.create(
                arg1.findString("namespace_id")?.let { NamespaceId(SnowflakeId(it)) } ?: ctx[NamespaceId::class],
                ApiTriggerCreateReq(
                    type = TriggerType.valueOf(arg1.getString("type")),
                    funcId = FuncId(SnowflakeId(arg1.getString("func_id"))),
                    name = TriggerName(arg1.getString("name")),
                    inputs = TriggerInputs(),
                    duration = if (arg1.type("duration") == KuaString::class) {
                        TriggerDuration(arg1.getString("duration"))
                    } else {
                        null
                    },
                    topicId = if (arg1.type("topic_id") == KuaString::class) {
                        TopicId(SnowflakeId(arg1.getString("topic_id")))
                    } else {
                        null
                    },
                    hookId = if (arg1.type("hook_id") == KuaString::class) {
                        HookId(SnowflakeId(arg1.getString("hook_id")))
                    } else {
                        null
                    },
                    hookMethod = arg1.findString("hook_method")?.let(HookMethod::valueOf),
                    cron = if (arg1.type("cron") == KuaString::class) {
                        CronPattern(arg1.getString("cron"))
                    } else {
                        null
                    }
                )
            )

            null to ctx.tableCreate(
                "id" to KuaString(res.id.value.value.toString(16)),
                "status" to KuaString(res.status.name),
                "trigger_id" to KuaString(res.triggerId.value.value.toString(16)),
                "workspace_id" to KuaString(res.workspaceId.value.value.toString(16)),
                "namespace_id" to KuaString(res.namespaceId.value.value.toString(16))
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}