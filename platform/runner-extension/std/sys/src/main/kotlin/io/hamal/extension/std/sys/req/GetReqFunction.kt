package io.hamal.extension.std.sys.req

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
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReq

class GetReqFunction(
    private val httpTemplate: HttpTemplate
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        val response = httpTemplate.get("/v1/reqs/{reqId}")
            .path("reqId", arg1.value)
            .execute()

        if (response is SuccessHttpResponse) {
            return null to response.result(ApiSubmittedReq::class)
                .let { exec ->
                    MapType(
                        mutableMapOf(
                            "reqId" to StringType(exec.reqId.value.value.toString(16)),
                            "status" to StringType(exec.status.name)
                        )
                    )
                }
        } else {
            require(response is ErrorHttpResponse)
//            return response.error(HamalError::class)
//                .let { error ->
//                    ErrorValue(error.message ?: "An unknown error occurred")
//                }

            TODO()
        }
//
    }
}