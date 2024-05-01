package io.hamal.plugin.std.sys.trigger

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.findTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTriggerList
import io.hamal.lib.sdk.api.ApiTriggerService

class TriggerListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class), FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {
            null to ctx.tableCreate(sdk.trigger.list(
                ApiTriggerService.TriggerQuery(
                    namespaceIds = arg1.findTable("namespace_ids")
                        ?.asList()?.map { NamespaceId((it as ValueString).stringValue) }?.toList()
                        ?: listOf(ctx[NamespaceId::class])

                )
            ).map { trigger ->
                when (trigger) {
                    is ApiTriggerList.FixedRate -> {
                        ctx.tableCreate(
                            "id" to ValueString(trigger.id.value.value.toString(16)),
                            "type" to ValueString("FixedRate"),
                            "name" to trigger.name,
                            "namespace" to ctx.tableCreate(
                                "id" to ValueString(trigger.namespace.id.value.value.toString(16)),
                                "name" to trigger.namespace.name
                            ),
                            "func" to ctx.tableCreate(
                                "id" to ValueString(trigger.func.id.value.value.toString(16)),
                                "name" to trigger.func.name
                            ),
                            "duration" to trigger.duration
                        )
                    }

                    is ApiTriggerList.Event -> {
                        ctx.tableCreate(
                            "id" to ValueString(trigger.id.value.value.toString(16)),
                            "type" to ValueString("Event"),
                            "name" to trigger.name,
                            "namespace" to ctx.tableCreate(
                                "id" to ValueString(trigger.namespace.id.value.value.toString(16)),
                                "name" to trigger.namespace.name
                            ),
                            "func" to ctx.tableCreate(
                                "id" to ValueString(trigger.func.id.value.value.toString(16)),
                                "name" to trigger.func.name
                            ),
                            "topic" to ctx.tableCreate(
                                "id" to ValueString(trigger.topic.id.value.value.toString(16)),
                                "name" to trigger.topic.name
                            ),
                        )
                    }

                    is ApiTriggerList.Hook -> {
                        ctx.tableCreate(
                            "id" to ValueString(trigger.id.value.value.toString(16)),
                            "type" to ValueString("Hook"),
                            "name" to trigger.name,
                            "namespace" to ctx.tableCreate(
                                "id" to ValueString(trigger.namespace.id.value.value.toString(16)),
                                "name" to trigger.namespace.name
                            ),
                            "func" to ctx.tableCreate(
                                "id" to ValueString(trigger.func.id.value.value.toString(16)),
                                "name" to trigger.func.name
                            ),
                        )
                    }

                    is ApiTriggerList.Cron -> ctx.tableCreate(
                        "id" to ValueString(trigger.id.value.value.toString(16)),
                        "type" to ValueString("Cron"),
                        "name" to trigger.name,
                        "namespace" to ctx.tableCreate(
                            "id" to ValueString(trigger.namespace.id.value.value.toString(16)),
                            "name" to trigger.namespace.name
                        ),
                        "func" to ctx.tableCreate(
                            "id" to ValueString(trigger.func.id.value.value.toString(16)),
                            "name" to trigger.func.name
                        ),
                        "cron" to trigger.cron
                    )

                    is ApiTriggerList.Endpoint -> {
                        ctx.tableCreate(
                            "id" to ValueString(trigger.id.value.value.toString(16)),
                            "type" to ValueString("Endpoint"),
                            "name" to trigger.name,
                            "namespace" to ctx.tableCreate(
                                "id" to ValueString(trigger.namespace.id.value.value.toString(16)),
                                "name" to trigger.namespace.name
                            ),
                            "func" to ctx.tableCreate(
                                "id" to ValueString(trigger.func.id.value.value.toString(16)),
                                "name" to trigger.func.name
                            )
                        )
                    }
                }
            })
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}