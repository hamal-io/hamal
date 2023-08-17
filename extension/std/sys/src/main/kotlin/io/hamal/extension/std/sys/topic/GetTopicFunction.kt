package io.hamal.extension.std.sys.topic

import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableTypeMap
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.domain.ApiError
import io.hamal.lib.sdk.domain.ApiTopic

class GetTopicFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In2Out<StringType, ErrorType, TableTypeMap>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, TableTypeMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, TableTypeMap?> {
        val response = templateSupplier()
            .get("/v1/topics/${arg1.value}")
            .execute()

        if (response is SuccessHttpResponse) {
            return null to response.result(ApiTopic::class)
                .let { topic ->
                    ctx.tableCreateMap(2).also {
                        it["id"] = topic.id
                        it["name"] = topic.name.value
                    }
                }
        } else {
            require(response is ErrorHttpResponse)
            return response.error(ApiError::class)
                .let { error -> ErrorType(error.message ?: "An unknown error occurred") } to null
        }
    }
}