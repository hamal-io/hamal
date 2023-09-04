package io.hamal.extension.std.sys.trigger

import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.ApiError
import io.hamal.lib.sdk.hub.ApiEventTrigger
import io.hamal.lib.sdk.hub.ApiFixedRateTrigger
import io.hamal.lib.sdk.hub.ApiTrigger

class GetTriggerFunction(
    private val httpTemplate: HttpTemplate
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        val response = httpTemplate.get("/v1/triggers/${arg1.value}")
            .execute()

        if (response is SuccessHttpResponse) {
            return null to response.result(ApiTrigger::class)
                .let { trigger ->

                    when (val t = trigger) {
                        is ApiFixedRateTrigger ->
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

                        is ApiEventTrigger -> {
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

                        else -> TODO()
                    }
                }
        } else {
            require(response is ErrorHttpResponse)
            return response.error(ApiError::class)
                .let { error ->
                    ErrorType(error.message ?: "An unknown error occurred")
                } to null
        }
    }
}