package io.hamal.extension.std.sys

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableArrayValue
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.sdk.domain.ListExecsResponse

class ListExecsFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In2Out<ErrorValue, TableArrayValue>(
    FunctionOutput2Schema(ErrorValue::class, TableArrayValue::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorValue?, TableArrayValue?> {
        val execs = try {
            templateSupplier()
                .get("/v1/execs")
                .execute(ListExecsResponse::class)
                .execs
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf<ListExecsResponse.Exec>()
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