package io.hamal.extension.std.sys.func

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableTypeArray
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.domain.ApiNamespaceList

class ListNamespaceFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In2Out<ErrorType, TableTypeArray>(
    FunctionOutput2Schema(ErrorType::class, TableTypeArray::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, TableTypeArray?> {
        val namespaces = try {
            templateSupplier()
                .get("/v1/namespaces")
                .execute(ApiNamespaceList::class)
                .namespaces

        } catch (t: Throwable) {
            t.printStackTrace()
            listOf()
        }

        return null to ctx.tableCreateArray().also { rs ->
            namespaces.forEach { namespace ->
                val inner = ctx.tableCreateMap(2)
                inner["id"] = namespace.id
                inner["name"] = namespace.name.value
                rs.append(inner)
            }
        }
    }
}