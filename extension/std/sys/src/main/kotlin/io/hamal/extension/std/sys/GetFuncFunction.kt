package io.hamal.extension.std.sys

import io.hamal.lib.domain.HamalError
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.sdk.domain.ApiFunc

class GetFuncFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In2Out<StringValue, ErrorValue, TableMapValue>(
    FunctionInput1Schema(StringValue::class),
    FunctionOutput2Schema(ErrorValue::class, TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringValue): Pair<ErrorValue?, TableMapValue?> {
        val response = templateSupplier()
            .get("/v1/funcs/${arg1.value}")
            .execute()

        if (response is SuccessHttpResponse) {
            return null to response.result(ApiFunc::class)
                .let { func ->
                    ctx.tableCreateMap(0).also {
                        it["id"] = func.id.value.value.toString()
                        it["name"] = func.name.value
//                        it["inputs"] = exec.inputs.value
                        it["code"] = func.code
                    }

                }
        } else {
            require(response is ErrorHttpResponse)
            return response.error(HamalError::class)
                .let { error ->
                    ErrorValue(error.message ?: "An unknown error occurred")
                } to null
        }
    }
}