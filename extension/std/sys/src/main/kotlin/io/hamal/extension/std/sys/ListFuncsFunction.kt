package io.hamal.extension.std.sys

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableArray
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.domain.ApiFuncList

class ListFuncsFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In2Out<ErrorType, TableArray>(
    FunctionOutput2Schema(ErrorType::class, TableArray::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, TableArray?> {
        val funcs = try {
            templateSupplier()
                .get("/v1/funcs")
                .execute(ApiFuncList::class)
                .funcs
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf<ApiFuncList.ApiSimpleFunc>()
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