package io.hamal.extension.std.sys.exec

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.body
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableMap
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.HttpTemplateSupplier
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithDomainId

class InvokeAdhocFunction(
    private val templateSupplier: HttpTemplateSupplier
) : Function1In2Out<TableMap, ErrorType, TableMap>(
    FunctionInput1Schema(TableMap::class),
    FunctionOutput2Schema(ErrorType::class, TableMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableMap): Pair<ErrorType?, TableMap?> {
        val r = InvokeAdhocReq(
            inputs = InvocationInputs(),
            code = CodeType(arg1.getString("code"))
        )

        val res = templateSupplier()
            .post("/v1/adhoc")
            .body(r)
            .execute(ApiSubmittedReqWithDomainId::class)

        return null to ctx.tableCreateMap(2).also {
            it["req_id"] = res.reqId
            it["status"] = res.status.name
            it["id"] = res.id
        }
    }
}