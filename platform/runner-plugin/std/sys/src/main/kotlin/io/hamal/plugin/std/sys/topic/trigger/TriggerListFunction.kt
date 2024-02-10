package io.hamal.plugin.std.sys.topic.trigger

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaArray
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTriggerList
import io.hamal.lib.sdk.api.ApiTriggerService.TriggerQuery

class TriggerListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaMap, KuaError, KuaArray>(
    FunctionInput1Schema(KuaMap::class),
    FunctionOutput2Schema(KuaError::class, KuaArray::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaMap): Pair<KuaError?, KuaArray?> {
        return try {
            null to KuaArray(
                sdk.trigger.list(TriggerQuery(
                    namespaceIds = arg1.getArrayType("namespace_ids")
                        .map { NamespaceId((it.value as KuaString).value) }
                )).mapIndexed { index, trigger ->
                    index to when (trigger) {
                        is ApiTriggerList.FixedRateTrigger -> {
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
                                    "duration" to KuaString(trigger.duration.value)
                                )
                            )
                        }

                        is ApiTriggerList.EventTrigger -> {
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
                                )
                            )
                        }

                        is ApiTriggerList.HookTrigger -> {
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
                                            "methods" to KuaString(trigger.hook.method.name)
                                        )
                                    ),
                                )
                            )
                        }

                        is ApiTriggerList.CronTrigger -> KuaMap(
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
                                "cron" to KuaString(trigger.cron.value)
                            )
                        )
                    }
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}