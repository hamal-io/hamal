package io.hamal.extension.std.sys

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableArrayValue
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.sdk.domain.ListFuncsResponse

class ListFuncsFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In2Out<ErrorValue, TableArrayValue>(
    FunctionOutput2Schema(ErrorValue::class, TableArrayValue::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorValue?, TableArrayValue?> {
        val funcs = try {
            templateSupplier()
                .get("/v1/funcs")
                .execute(ListFuncsResponse::class)
                .funcs
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf<ListFuncsResponse.Func>()
        }

        return null to ctx.tableCreateArray().also { rs ->
            funcs.forEach { func ->
                val inner = ctx.tableCreateMap(2)
                inner["id"] = func.id.value.value.toString()
                inner["name"] = func.name.value
                rs.append(inner)
            }
        }
    }
}