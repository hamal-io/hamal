package io.hamal.agent.extension.std.sys

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.table.TableArrayProxyValue
import io.hamal.lib.sdk.domain.ListExecsResponse

class ListExecsFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In1Out<TableArrayProxyValue>(
    FunctionOutput1Schema(TableArrayProxyValue::class)
) {
    override fun invoke(ctx: FunctionContext): TableArrayProxyValue {


        val execs = try {
            templateSupplier()
                .get("/v1/execs")
                .execute(ListExecsResponse::class)
                .execs
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf<ListExecsResponse.Exec>()
        }

        return ctx.tableCreateArray().also { rs ->
            execs.forEach { exec ->
                val inner = ctx.tableCreateMap(2)
                inner["id"] = exec.id.value.value.toString()
                inner["status"] = exec.status.toString()

                rs.append(inner)
            }
        }
    }
}