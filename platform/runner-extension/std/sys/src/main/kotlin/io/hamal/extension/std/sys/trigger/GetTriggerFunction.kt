package io.hamal.extension.std.sys.trigger

import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.HubSdk
import io.hamal.lib.sdk.hub.HubEventTrigger
import io.hamal.lib.sdk.hub.HubFixedRateTrigger

class GetTriggerFunction(
    private val sdk: HubSdk
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        return try {
            null to sdk.trigger.get(TriggerId(arg1.value))
                .let { trigger ->
                    when (trigger) {
                        is HubFixedRateTrigger ->
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

                        is HubEventTrigger -> {
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
                }
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}