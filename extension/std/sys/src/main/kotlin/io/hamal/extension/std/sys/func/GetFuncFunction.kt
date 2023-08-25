package io.hamal.extension.std.sys.func

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
import io.hamal.lib.sdk.domain.ApiError
import io.hamal.lib.sdk.domain.ApiFunc

class GetFuncFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        val response = templateSupplier()
            .get("/v1/funcs/${arg1.value}")
            .execute()

        if (response is SuccessHttpResponse) {
            return null to response.result(ApiFunc::class)
                .let { func ->
                    MapType(
                        mutableMapOf(
                            "id" to StringType(func.id.value.value.toString(16)),
                            "namespace" to MapType(
                                mutableMapOf(
                                    "id" to StringType(func.namespace.id.value.value.toString(16)),
                                    "name" to StringType(func.namespace.name.value)
                                )
                            ),
                            "name" to StringType(func.name.value),
                            "code" to StringType(func.code.value)
                        )
                    )
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