package io.hamal.plugin.std.sys.trigger

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.findTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTriggerList
import io.hamal.lib.sdk.api.ApiTriggerService
import io.hamal.lib.value.ValueError
import io.hamal.lib.value.ValueString

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
                            "name" to ValueString(trigger.name.value),
                            "namespace" to ctx.tableCreate(
                                "id" to ValueString(trigger.namespace.id.value.value.toString(16)),
                                "name" to ValueString(trigger.namespace.name.value)
                            ),
                            "func" to ctx.tableCreate(
                                "id" to ValueString(trigger.func.id.value.value.toString(16)),
                                "name" to ValueString(trigger.func.name.value)
                            ),
                            "duration" to ValueString(trigger.duration.value)
                        )
                    }

                    is ApiTriggerList.Event -> {
                        ctx.tableCreate(
                            "id" to ValueString(trigger.id.value.value.toString(16)),
                            "type" to ValueString("Event"),
                            "name" to ValueString(trigger.name.value),
                            "namespace" to ctx.tableCreate(
                                "id" to ValueString(trigger.namespace.id.value.value.toString(16)),
                                "name" to ValueString(trigger.namespace.name.value)
                            ),
                            "func" to ctx.tableCreate(
                                "id" to ValueString(trigger.func.id.value.value.toString(16)),
                                "name" to ValueString(trigger.func.name.value)
                            ),
                            "topic" to ctx.tableCreate(
                                "id" to ValueString(trigger.topic.id.value.value.toString(16)),
                                "name" to ValueString(trigger.topic.name.value)
                            ),
                        )
                    }

                    is ApiTriggerList.Hook -> {
                        ctx.tableCreate(
                            "id" to ValueString(trigger.id.value.value.toString(16)),
                            "type" to ValueString("Hook"),
                            "name" to ValueString(trigger.name.value),
                            "namespace" to ctx.tableCreate(
                                "id" to ValueString(trigger.namespace.id.value.value.toString(16)),
                                "name" to ValueString(trigger.namespace.name.value)
                            ),
                            "func" to ctx.tableCreate(
                                "id" to ValueString(trigger.func.id.value.value.toString(16)),
                                "name" to ValueString(trigger.func.name.value)
                            ),
                        )
                    }

                    is ApiTriggerList.Cron -> ctx.tableCreate(
                        "id" to ValueString(trigger.id.value.value.toString(16)),
                        "type" to ValueString("Cron"),
                        "name" to ValueString(trigger.name.value),
                        "namespace" to ctx.tableCreate(
                            "id" to ValueString(trigger.namespace.id.value.value.toString(16)),
                            "name" to ValueString(trigger.namespace.name.value)
                        ),
                        "func" to ctx.tableCreate(
                            "id" to ValueString(trigger.func.id.value.value.toString(16)),
                            "name" to ValueString(trigger.func.name.value)
                        ),
                        "cron" to ValueString(trigger.cron.value)
                    )

                    is ApiTriggerList.Endpoint -> {
                        ctx.tableCreate(
                            "id" to ValueString(trigger.id.value.value.toString(16)),
                            "type" to ValueString("Endpoint"),
                            "name" to ValueString(trigger.name.value),
                            "namespace" to ctx.tableCreate(
                                "id" to ValueString(trigger.namespace.id.value.value.toString(16)),
                                "name" to ValueString(trigger.namespace.name.value)
                            ),
                            "func" to ctx.tableCreate(
                                "id" to ValueString(trigger.func.id.value.value.toString(16)),
                                "name" to ValueString(trigger.func.name.value)
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