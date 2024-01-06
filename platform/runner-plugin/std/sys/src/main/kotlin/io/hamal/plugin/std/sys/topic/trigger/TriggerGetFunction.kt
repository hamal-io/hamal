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
import io.hamal.lib.sdk.api.ApiCronTrigger
import io.hamal.lib.sdk.api.ApiEventTrigger
import io.hamal.lib.sdk.api.ApiFixedRateTrigger
import io.hamal.lib.sdk.api.ApiHookTrigger

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
                        is ApiFixedRateTrigger ->
                            KuaMap(
                                mutableMapOf(
                                    "id" to KuaString(trigger.id.value.value.toString(16)),
                                    "type" to KuaString("FixedRate"),
                                    "name" to KuaString(trigger.name.value),
                                    "flow" to KuaMap(
                                        mutableMapOf(
                                            "id" to KuaString(trigger.flow.id.value.value.toString(16)),
                                            "name" to KuaString(trigger.flow.name.value)
                                        )
                                    ),
                                    "func" to KuaMap(
                                        mutableMapOf(
                                            "id" to KuaString(trigger.func.id.value.value.toString(16)),
                                            "name" to KuaString(trigger.func.name.value)
                                        )
                                    ),
                                    "duration" to KuaString(trigger.duration.toIsoString()),
                                    "status" to KuaString(trigger.status.name),
                                )
                            )

                        is ApiEventTrigger -> {
                            KuaMap(
                                mutableMapOf(
                                    "id" to KuaString(trigger.id.value.value.toString(16)),
                                    "type" to KuaString("Event"),
                                    "name" to KuaString(trigger.name.value),
                                    "flow" to KuaMap(
                                        mutableMapOf(
                                            "id" to KuaString(trigger.flow.id.value.value.toString(16)),
                                            "name" to KuaString(trigger.flow.name.value)
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

                        is ApiHookTrigger -> {
                            KuaMap(
                                mutableMapOf(
                                    "id" to KuaString(trigger.id.value.value.toString(16)),
                                    "type" to KuaString("Hook"),
                                    "name" to KuaString(trigger.name.value),
                                    "flow" to KuaMap(
                                        mutableMapOf(
                                            "id" to KuaString(trigger.flow.id.value.value.toString(16)),
                                            "name" to KuaString(trigger.flow.name.value)
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

                        is ApiCronTrigger -> KuaMap(
                            mutableMapOf(
                                "id" to KuaString(trigger.id.value.value.toString(16)),
                                "type" to KuaString("Cron"),
                                "name" to KuaString(trigger.name.value),
                                "flow" to KuaMap(
                                    mutableMapOf(
                                        "id" to KuaString(trigger.flow.id.value.value.toString(16)),
                                        "name" to KuaString(trigger.flow.name.value)
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