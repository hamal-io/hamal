package io.hamal.plugin.std.sys.topic.trigger

import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTrigger

class TriggerGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaMap>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaMap?> {
        return try {
            null to sdk.trigger.get(TriggerId(arg1.value))
                .let { trigger ->
                    when (trigger) {
                        is ApiTrigger.FixedRate ->
                            KuaMap(
                                mutableMapOf(
                                    "id" to KuaString(trigger.id.value.value.toString(16)),
                                    "type" to KuaString("FixedRate"),
                                    "name" to KuaString(trigger.name.value),
                                    "namespace" to KuaMap(
                                        mutableMapOf(
                                            "id" to KuaString(trigger.namespace.id.value.value.toString(16)),
                                            "name" to KuaString(trigger.namespace.name.value)
                                        )
                                    ),
                                    "func" to KuaMap(
                                        mutableMapOf(
                                            "id" to KuaString(trigger.func.id.value.value.toString(16)),
                                            "name" to KuaString(trigger.func.name.value)
                                        )
                                    ),
                                    "duration" to KuaString(trigger.duration.value),
                                    "status" to KuaString(trigger.status.name),
                                )
                            )

                        is ApiTrigger.Event -> {
                            KuaMap(
                                mutableMapOf(
                                    "id" to KuaString(trigger.id.value.value.toString(16)),
                                    "type" to KuaString("Event"),
                                    "name" to KuaString(trigger.name.value),
                                    "namespace" to KuaMap(
                                        mutableMapOf(
                                            "id" to KuaString(trigger.namespace.id.value.value.toString(16)),
                                            "name" to KuaString(trigger.namespace.name.value)
                                        )
                                    ),
                                    "func" to KuaMap(
                                        mutableMapOf(
                                            "id" to KuaString(trigger.func.id.value.value.toString(16)),
                                            "name" to KuaString(trigger.func.name.value)
                                        )
                                    ),
                                    "topic" to KuaMap(
                                        mutableMapOf(
                                            "id" to KuaString(trigger.topic.id.value.value.toString(16)),
                                            "name" to KuaString(trigger.topic.name.value)
                                        )
                                    ),
                                    "status" to KuaString(trigger.status.name),
                                )
                            )
                        }

                        is ApiTrigger.Hook -> {
                            KuaMap(
                                mutableMapOf(
                                    "id" to KuaString(trigger.id.value.value.toString(16)),
                                    "type" to KuaString("Hook"),
                                    "name" to KuaString(trigger.name.value),
                                    "namespace" to KuaMap(
                                        mutableMapOf(
                                            "id" to KuaString(trigger.namespace.id.value.value.toString(16)),
                                            "name" to KuaString(trigger.namespace.name.value)
                                        )
                                    ),
                                    "func" to KuaMap(
                                        mutableMapOf(
                                            "id" to KuaString(trigger.func.id.value.value.toString(16)),
                                            "name" to KuaString(trigger.func.name.value)
                                        )
                                    ),
                                    "hook" to KuaMap(
                                        mutableMapOf(
                                            "id" to KuaString(trigger.hook.id.value.value.toString(16)),
                                            "name" to KuaString(trigger.hook.name.value),
                                            "method" to KuaString(trigger.hook.method.name)
                                        )
                                    ),
                                    "status" to KuaString(trigger.status.name),
                                )
                            )
                        }

                        is ApiTrigger.Cron -> KuaMap(
                            mutableMapOf(
                                "id" to KuaString(trigger.id.value.value.toString(16)),
                                "type" to KuaString("Cron"),
                                "name" to KuaString(trigger.name.value),
                                "namespace" to KuaMap(
                                    mutableMapOf(
                                        "id" to KuaString(trigger.namespace.id.value.value.toString(16)),
                                        "name" to KuaString(trigger.namespace.name.value)
                                    )
                                ),
                                "func" to KuaMap(
                                    mutableMapOf(
                                        "id" to KuaString(trigger.func.id.value.value.toString(16)),
                                        "name" to KuaString(trigger.func.name.value)
                                    )
                                ),
                                "cron" to KuaString(trigger.cron.value),
                                "status" to KuaString(trigger.status.name)
                            )
                        )
                    }
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}