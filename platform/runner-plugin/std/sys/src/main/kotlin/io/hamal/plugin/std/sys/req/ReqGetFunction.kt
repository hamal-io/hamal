package io.hamal.plugin.std.sys.req

import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiRequested

class ReqGetFunction(
    private val httpTemplate: HttpTemplate
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        val response = httpTemplate.get("/v1/requests/{reqId}")
            .path("reqId", arg1.value)
            .execute()

        if (response is HttpSuccessResponse) {
            return null to response.result(ApiRequested::class)
                .let { exec ->
                    MapType(
                        mutableMapOf(
                            "reqId" to StringType(exec.id.value.value.toString(16)),
                            "status" to StringType(exec.status.name)
                        )
                    )
                }
        } else {
            require(response is HttpErrorResponse)
//            return response.error(HamalError::class)
//                .let { error ->
//                    ErrorValue(error.message ?: "An unknown error occurred")
//                }

            TODO()
        }
//
    }
}