package io.hamal.plugin.std.sys.trigger

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.createTable
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTriggerList
import io.hamal.lib.sdk.api.ApiTriggerService

class TriggerListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class), FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            null to ctx.createTable(sdk.trigger.list(
                ApiTriggerService.TriggerQuery(
                    namespaceIds = arg1.findArray("namespace_ids")
                        ?.asSequence()?.map { NamespaceId((it as KuaString).value) }?.toList()
                        ?: listOf(ctx[NamespaceId::class])

                )
            ).map { trigger ->
                when (trigger) {
                    is ApiTriggerList.FixedRate -> {
                        ctx.createTable(
                            "id" to KuaString(trigger.id.value.value.toString(16)),
                            "type" to KuaString("FixedRate"),
                            "name" to KuaString(trigger.name.value),
                            "namespace" to ctx.createTable(
                                "id" to KuaString(trigger.namespace.id.value.value.toString(16)),
                                "name" to KuaString(trigger.namespace.name.value)
                            ),
                            "func" to ctx.createTable(
                                "id" to KuaString(trigger.func.id.value.value.toString(16)),
                                "name" to KuaString(trigger.func.name.value)
                            ),
                            "duration" to KuaString(trigger.duration.value)
                        )
                    }

                    is ApiTriggerList.Event -> {
                        ctx.createTable(
                            "id" to KuaString(trigger.id.value.value.toString(16)),
                            "type" to KuaString("Event"),
                            "name" to KuaString(trigger.name.value),
                            "namespace" to ctx.createTable(
                                "id" to KuaString(trigger.namespace.id.value.value.toString(16)),
                                "name" to KuaString(trigger.namespace.name.value)
                            ),
                            "func" to ctx.createTable(
                                "id" to KuaString(trigger.func.id.value.value.toString(16)),
                                "name" to KuaString(trigger.func.name.value)
                            ),
                            "topic" to ctx.createTable(
                                "id" to KuaString(trigger.topic.id.value.value.toString(16)),
                                "name" to KuaString(trigger.topic.name.value)
                            ),
                        )
                    }

                    is ApiTriggerList.Hook -> {
                        ctx.createTable(
                            "id" to KuaString(trigger.id.value.value.toString(16)),
                            "type" to KuaString("Hook"),
                            "name" to KuaString(trigger.name.value),
                            "namespace" to ctx.createTable(
                                "id" to KuaString(trigger.namespace.id.value.value.toString(16)),
                                "name" to KuaString(trigger.namespace.name.value)
                            ),
                            "func" to ctx.createTable(
                                "id" to KuaString(trigger.func.id.value.value.toString(16)),
                                "name" to KuaString(trigger.func.name.value)
                            ),
                            "hook" to ctx.createTable(
                                "id" to KuaString(trigger.hook.id.value.value.toString(16)),
                                "name" to KuaString(trigger.hook.name.value),
                                "methods" to KuaString(trigger.hook.method.name)
                            ),
                        )
                    }

                    is ApiTriggerList.Cron -> ctx.createTable(
                        "id" to KuaString(trigger.id.value.value.toString(16)),
                        "type" to KuaString("Cron"),
                        "name" to KuaString(trigger.name.value),
                        "namespace" to ctx.createTable(
                            "id" to KuaString(trigger.namespace.id.value.value.toString(16)),
                            "name" to KuaString(trigger.namespace.name.value)
                        ),
                        "func" to ctx.createTable(
                            "id" to KuaString(trigger.func.id.value.value.toString(16)),
                            "name" to KuaString(trigger.func.name.value)
                        ),
                        "cron" to KuaString(trigger.cron.value)
                    )
                }
            })
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}