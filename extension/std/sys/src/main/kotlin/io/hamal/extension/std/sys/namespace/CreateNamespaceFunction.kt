package io.hamal.extension.std.sys.namespace

import io.hamal.lib.domain.req.CreateNamespaceReq
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableMap
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithId

class CreateNamespaceFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In2Out<TableMap, ErrorType, TableMap>(
    FunctionInput1Schema(TableMap::class),
    FunctionOutput2Schema(ErrorType::class, TableMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableMap): Pair<ErrorType?, TableMap> {
        try {

            val r = CreateNamespaceReq(
                name = NamespaceName(arg1.getString("name")),
                inputs = NamespaceInputs(),
            )

            val res = templateSupplier()
                .post("/v1/namespaces")
                .body(r)
                .execute(ApiSubmittedReqWithId::class)

            return null to ctx.tableCreateMap(1).also {
                it["req_id"] = res.reqId
                it["status"] = res.status.name
                it["id"] = res.id
            }

        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }
}