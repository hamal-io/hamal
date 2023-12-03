package io.hamal.plugin.std.sys.topic.trigger

import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiCronTrigger
import io.hamal.lib.sdk.api.ApiEventTrigger
import io.hamal.lib.sdk.api.ApiFixedRateTrigger
import io.hamal.lib.sdk.api.ApiHookTrigger

class TriggerGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        return try {
            null to sdk.trigger.get(TriggerId(arg1.value))
                .let { trigger ->
                    when (trigger) {
                        is ApiFixedRateTrigger ->
                            MapType(
                                mutableMapOf(
                                    "id" to StringType(trigger.id.value.value.toString(16)),
                                    "type" to StringType("FixedRate"),
                                    "name" to StringType(trigger.name.value),
                                    "flow" to MapType(
                                        mutableMapOf(
                                            "id" to StringType(trigger.flow.id.value.value.toString(16)),
                                            "name" to StringType(trigger.flow.name.value)
                                        )
                                    ),
                                    "func" to MapType(
                                        mutableMapOf(
                                            "id" to StringType(trigger.func.id.value.value.toString(16)),
                                            "name" to StringType(trigger.func.name.value)
                                        )
                                    ),
                                    "duration" to StringType(trigger.duration.toIsoString()),
                                    "status" to StringType(trigger.status.name),
                                )
                            )

                        is ApiEventTrigger -> {
                            MapType(
                                mutableMapOf(
                                    "id" to StringType(trigger.id.value.value.toString(16)),
                                    "type" to StringType("Event"),
                                    "name" to StringType(trigger.name.value),
                                    "flow" to MapType(
                                        mutableMapOf(
                                            "id" to StringType(trigger.flow.id.value.value.toString(16)),
                                            "name" to StringType(trigger.flow.name.value)
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
                                    "status" to StringType(trigger.status.name),
                                )
                            )
                        }

                        is ApiHookTrigger -> {
                            MapType(
                                mutableMapOf(
                                    "id" to StringType(trigger.id.value.value.toString(16)),
                                    "type" to StringType("Hook"),
                                    "name" to StringType(trigger.name.value),
                                    "flow" to MapType(
                                        mutableMapOf(
                                            "id" to StringType(trigger.flow.id.value.value.toString(16)),
                                            "name" to StringType(trigger.flow.name.value)
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
                                            "method" to StringType(trigger.hook.method.name)
                                        )
                                    ),
                                    "status" to StringType(trigger.status.name),
                                )
                            )
                        }

                        is ApiCronTrigger -> MapType(
                            mutableMapOf(
                                "id" to StringType(trigger.id.value.value.toString(16)),
                                "type" to StringType("Cron"),
                                "name" to StringType(trigger.name.value),
                                "flow" to MapType(
                                    mutableMapOf(
                                        "id" to StringType(trigger.flow.id.value.value.toString(16)),
                                        "name" to StringType(trigger.flow.name.value)
                                    )
                                ),
                                "func" to MapType(
                                    mutableMapOf(
                                        "id" to StringType(trigger.func.id.value.value.toString(16)),
                                        "name" to StringType(trigger.func.name.value)
                                    )
                                ),
                                "cron" to StringType(trigger.cron.value),
                                "status" to StringType(trigger.status.name)
                            )
                        )
                    }
                }
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}