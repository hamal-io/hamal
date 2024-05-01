package io.hamal.plugin.std.sys.trigger

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTrigger

class TriggerGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, ValueError, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<ValueError?, KuaTable?> {
        return try {
            null to sdk.trigger.get(TriggerId(arg1.stringValue))
                .let { trigger ->
                    when (trigger) {
                        is ApiTrigger.FixedRate ->
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
                                    "name" to trigger.func.name.value
                                ),
                                "duration" to ValueString(trigger.duration.value),
                                "status" to ValueString(trigger.status.name),
                            )

                        is ApiTrigger.Event -> {
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
                                    "name" to trigger.func.name
                                ),
                                "topic" to ctx.tableCreate(
                                    "id" to ValueString(trigger.topic.id.value.value.toString(16)),
                                    "name" to ValueString(trigger.topic.name.value)
                                ),
                                "status" to ValueString(trigger.status.name),
                            )
                        }

                        is ApiTrigger.Hook -> {
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
                                    "name" to trigger.func.name
                                ),
                                "status" to ValueString(trigger.status.name),
                            )
                        }

                        is ApiTrigger.Cron -> ctx.tableCreate(
                            "id" to ValueString(trigger.id.value.value.toString(16)),
                            "type" to ValueString("Cron"),
                            "name" to ValueString(trigger.name.value),
                            "namespace" to ctx.tableCreate(
                                "id" to ValueString(trigger.namespace.id.value.value.toString(16)),
                                "name" to ValueString(trigger.namespace.name.value)
                            ),
                            "func" to ctx.tableCreate(
                                "id" to ValueString(trigger.func.id.value.value.toString(16)),
                                "name" to trigger.func.name
                            ),
                            "cron" to ValueString(trigger.cron.value),
                            "status" to ValueString(trigger.status.name)
                        )

                        is ApiTrigger.Endpoint -> {
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
                                    "name" to trigger.func.name
                                ),
                                "status" to ValueString(trigger.status.name),
                            )
                        }
                    }
                }
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}