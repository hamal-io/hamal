package io.hamal.plugin.std.sys.trigger

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTriggerList

class TriggerListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<ErrorType, ArrayType>(
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
        return try {
            null to ArrayType(
                sdk.trigger.list(ctx[GroupId::class])
                    .mapIndexed { index, trigger ->
                        index to when (trigger) {
                            is ApiTriggerList.FixedRateTrigger -> {
                                MapType(
                                    mutableMapOf(
                                        "id" to StringType(trigger.id.value.value.toString(16)),
                                        "type" to StringType("FixedRate"),
                                        "name" to StringType(trigger.name.value),
                                        "namespace" to MapType(
                                            mutableMapOf(
                                                "id" to StringType(trigger.namespace.id.value.value.toString(16)),
                                                "name" to StringType(trigger.namespace.name.value)
                                            )
                                        ),
                                        "func" to MapType(
                                            mutableMapOf(
                                                "id" to StringType(trigger.func.id.value.value.toString(16)),
                                                "name" to StringType(trigger.func.name.value)
                                            )
                                        ),
                                        "duration" to StringType(trigger.duration.toIsoString())
                                    )
                                )
                            }

                            is ApiTriggerList.EventTrigger -> {
                                MapType(
                                    mutableMapOf(
                                        "id" to StringType(trigger.id.value.value.toString(16)),
                                        "type" to StringType("Event"),
                                        "name" to StringType(trigger.name.value),
                                        "namespace" to MapType(
                                            mutableMapOf(
                                                "id" to StringType(trigger.namespace.id.value.value.toString(16)),
                                                "name" to StringType(trigger.namespace.name.value)
                                            )
                                        ),
                                        "func" to MapType(
                                            mutableMapOf(
                                                "id" to StringType(trigger.func.id.value.value.toString(16)),
                                                "name" to StringType(trigger.func.name.value)
                                            )
                                        ),
                                        "topic" to MapType(
                                            mutableMapOf(
                                                "id" to StringType(trigger.topic.id.value.value.toString(16)),
                                                "name" to StringType(trigger.topic.name.value)
                                            )
                                        ),
                                    )
                                )
                            }

                            is ApiTriggerList.HookTrigger -> {
                                MapType(
                                    mutableMapOf(
                                        "id" to StringType(trigger.id.value.value.toString(16)),
                                        "type" to StringType("Hook"),
                                        "name" to StringType(trigger.name.value),
                                        "namespace" to MapType(
                                            mutableMapOf(
                                                "id" to StringType(trigger.namespace.id.value.value.toString(16)),
                                                "name" to StringType(trigger.namespace.name.value)
                                            )
                                        ),
                                        "func" to MapType(
                                            mutableMapOf(
                                                "id" to StringType(trigger.func.id.value.value.toString(16)),
                                                "name" to StringType(trigger.func.name.value)
                                            )
                                        ),
                                        "hook" to MapType(
                                            mutableMapOf(
                                                "id" to StringType(trigger.hook.id.value.value.toString(16)),
                                                "name" to StringType(trigger.hook.name.value),
                                                "methods" to ArrayType(trigger.hook.methods.mapIndexed { methodIndex, hookMethod ->
                                                    methodIndex + 1 to StringType(hookMethod.name)
                                                }.toMap().toMutableMap())
                                            )
                                        ),
                                    )
                                )
                            }
                        }
                    }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}