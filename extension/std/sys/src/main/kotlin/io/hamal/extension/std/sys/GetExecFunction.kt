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
import io.hamal.lib.sdk.domain.ApiExec

class GetExecFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In2Out<StringType, ErrorType, TableMap>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, TableMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, TableMap?> {
        val response = templateSupplier()
            .get("/v1/execs/{execId}")
            .path("execId", arg1.value)
            .execute()

        if (response is SuccessHttpResponse) {
            return null to response.result(ApiExec::class)
                .let { exec ->

                    val inputs = ctx.tableCreateMap(0)

                    ctx.tableCreateMap(0).also {
                        it["id"] = exec.id
                        it["status"] = StringType(exec.status.name)
                        it["inputs"] = inputs
                        exec.correlation?.correlationId?.value?.let { corId ->
                            it["correlationId"] = corId
                        } // FIXME set nil value to table --> makes the api nicer
                        it["code"] = exec.code
                    }

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