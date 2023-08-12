package io.hamal.extension.std.sys.func

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableArray
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.domain.ApiNamespaceList

class ListNamespacesFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In2Out<ErrorType, TableArray>(
    FunctionOutput2Schema(ErrorType::class, TableArray::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, TableArray?> {
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