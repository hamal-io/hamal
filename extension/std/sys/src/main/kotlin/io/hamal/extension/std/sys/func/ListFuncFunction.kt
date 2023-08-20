package io.hamal.extension.std.sys.func

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.domain.ApiFuncList

class ListFuncFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In2Out<ErrorType, TableProxyArray>(
    FunctionOutput2Schema(ErrorType::class, TableProxyArray::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, TableProxyArray?> {
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
                inner["id"] = func.id
                inner["name"] = func.name.value
                inner["namespace"] = ctx.tableCreateMap(2).also { nt ->
                    nt["id"] = func.namespace.id
                    nt["name"] = func.namespace.name.value
                }
                rs.append(inner)
            }
        }
    }
}