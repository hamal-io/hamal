package io.hamal.extension.std.sys

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableArray
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.domain.ApiExecList

class ListExecsFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In2Out<ErrorType, TableArray>(
    FunctionOutput2Schema(ErrorType::class, TableArray::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, TableArray?> {
        val execs = try {
            templateSupplier()
                .get("/v1/execs")
                .execute(ApiExecList::class)
                .execs
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf<ApiExecList.SimpleExec>()
        }

        return null to ctx.tableCreateArray().also { rs ->
            execs.forEach { exec ->
                val inner = ctx.tableCreateMap(2)
                inner["id"] = exec.id.value.value.toString()
                inner["status"] = exec.status.toString()

                rs.append(inner)
            }
        }
    }
}