package io.hamal.extension.std.sys

import io.hamal.lib.domain.Exec
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.StringValue

class GetExecFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In1Out<StringValue, TableMapValue>(
    FunctionInput1Schema(StringValue::class),
    FunctionOutput1Schema(TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringValue): TableMapValue {
        val response = templateSupplier()
            .get("/v1/execs/${arg1.value}")
            .execute()

        if (response is SuccessHttpResponse) {
            return response.result(Exec::class)
                .let { exec ->
                    ctx.tableCreateMap(0).also {
                        it["id"] = exec.id.value.value.toString()
                        it["status"] = StringValue(exec.status.name)
//                        it["inputs"] = exec.inputs.value,
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