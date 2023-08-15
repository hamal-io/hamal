package io.hamal.extension.std.sys.trigger

import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableMap
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.domain.ApiError
import io.hamal.lib.sdk.domain.ApiEventTrigger
import io.hamal.lib.sdk.domain.ApiFixedRateTrigger
import io.hamal.lib.sdk.domain.ApiTrigger

class GetTriggerFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In2Out<StringType, ErrorType, TableMap>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, TableMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, TableMap?> {
        val response = templateSupplier()
            .get("/v1/triggers/${arg1.value}")
            .execute()

        if (response is SuccessHttpResponse) {
            return null to response.result(ApiTrigger::class)
                .let { trigger ->

                    when (val t = trigger) {
                        is ApiFixedRateTrigger ->
                            ctx.tableCreateMap(6).also {
                                it["id"] = t.id
                                it["type"] = "FixedRate"
                                it["name"] = t.name.value
                                it["namespace"] = ctx.tableCreateMap(2).also { nt ->
                                    nt["id"] = t.namespace.id
                                    nt["name"] = t.namespace.name.value
                                }
                                it["func"] = ctx.tableCreateMap(2).also { nt ->
                                    nt["id"] = t.func.id
                                    nt["name"] = t.func.name.value
                                }
                                it["duration"] = t.duration.toIsoString()
                            }

                        is ApiEventTrigger -> {
                            ctx.tableCreateMap(6).also {
                                it["id"] = t.id
                                it["type"] = "Event"
                                it["name"] = t.name.value
                                it["namespace"] = ctx.tableCreateMap(2).also { nt ->
                                    nt["id"] = t.namespace.id
                                    nt["name"] = t.namespace.name.value
                                }
                                it["func"] = ctx.tableCreateMap(2).also { nt ->
                                    nt["id"] = t.func.id
                                    nt["name"] = t.func.name.value
                                }
                                it["topic"] = ctx.tableCreateMap(2).also { nt ->
                                    nt["id"] = t.topic.id
                                    nt["name"] = t.topic.name.value
                                }
                            }
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