package io.hamal.extension.std.sysadmin.trigger

import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.AdminSdk
import io.hamal.lib.sdk.admin.AdminTriggerList

class ListTriggerFunction(
    private val sdk: AdminSdk
) : Function0In2Out<ErrorType, ArrayType>(
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
        return try {
            null to ArrayType(
                sdk.trigger.list()
                    .mapIndexed { index, trigger ->
                        index to when (trigger) {
                            is AdminTriggerList.FixedRateTrigger -> {
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

                            is AdminTriggerList.EventTrigger -> {
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
                        }
                    }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}