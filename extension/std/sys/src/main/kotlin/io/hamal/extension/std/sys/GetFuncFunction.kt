package io.hamal.extension.std.sys

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
import io.hamal.lib.sdk.domain.ApiFunc

class GetFuncFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In2Out<StringType, ErrorType, TableMap>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, TableMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, TableMap?> {
        val response = templateSupplier()
            .get("/v1/funcs/${arg1.value}")
            .execute()

        if (response is SuccessHttpResponse) {
            return null to response.result(ApiFunc::class)
                .let { func ->
                    ctx.tableCreateMap(0).also {
                        it["id"] = func.id
                        it["name"] = func.name.value
//                        it["inputs"] = exec.inputs.value
                        it["code"] = func.code
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