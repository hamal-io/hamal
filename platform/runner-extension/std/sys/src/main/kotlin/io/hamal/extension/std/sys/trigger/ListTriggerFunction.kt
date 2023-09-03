package io.hamal.extension.std.sys.trigger

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.domain.ApiSimpleEventTrigger
import io.hamal.lib.sdk.hub.domain.ApiSimpleFixedRateTrigger
import io.hamal.lib.sdk.hub.domain.ApiSimpleTrigger
import io.hamal.lib.sdk.hub.domain.ApiTriggerList

class ListTriggerFunction(
    private val httpTemplate: HttpTemplate
) : Function0In2Out<ErrorType, ArrayType>(
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
        val triggers = try {
            httpTemplate
                .get("/v1/triggers")
                .execute(ApiTriggerList::class)
                .triggers
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf<ApiSimpleTrigger>()
        }

        return null to ArrayType(
            triggers.mapIndexed { index, trigger ->
                index to when (val t = trigger) {
                    is ApiSimpleFixedRateTrigger -> {
                        MapType(
                            mutableMapOf(
                                "id" to StringType(t.id.value.value.toString(16)),
                                "type" to StringType("FixedRate"),
                                "name" to StringType(t.name.value),
                                "namespace" to MapType(
                                    mutableMapOf(
                                        "id" to StringType(t.namespace.id.value.value.toString(16)),
                                        "name" to StringType(t.namespace.name.value)
                                    )
                                ),
                                "func" to MapType(
                                    mutableMapOf(
                                        "id" to StringType(t.func.id.value.value.toString(16)),
                                        "name" to StringType(t.func.name.value)
                                    )
                                ),
                                "duration" to StringType(t.duration.toIsoString())
                            )
                        )
                    }

                    is ApiSimpleEventTrigger -> {
                        MapType(
                            mutableMapOf(
                                "id" to StringType(t.id.value.value.toString(16)),
                                "type" to StringType("Event"),
                                "name" to StringType(t.name.value),
                                "namespace" to MapType(
                                    mutableMapOf(
                                        "id" to StringType(t.namespace.id.value.value.toString(16)),
                                        "name" to StringType(t.namespace.name.value)
                                    )
                                ),
                                "func" to MapType(
                                    mutableMapOf(
                                        "id" to StringType(t.func.id.value.value.toString(16)),
                                        "name" to StringType(t.func.name.value)
                                    )
                                ),
                                "topic" to MapType(
                                    mutableMapOf(
                                        "id" to StringType(t.topic.id.value.value.toString(16)),
                                        "name" to StringType(t.topic.name.value)
                                    )
                                ),
                            )
                        )
                    }
                }
            }.toMap().toMutableMap()
        )
    }
}