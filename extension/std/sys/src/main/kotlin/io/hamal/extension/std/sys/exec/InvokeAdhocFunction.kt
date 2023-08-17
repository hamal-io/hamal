package io.hamal.extension.std.sys.exec

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.body
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableTypeMap
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.sdk.HttpTemplateSupplier
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithId

class InvokeAdhocFunction(
    private val templateSupplier: HttpTemplateSupplier
) : Function1In2Out<TableTypeMap, ErrorType, TableTypeMap>(
    FunctionInput1Schema(TableTypeMap::class),
    FunctionOutput2Schema(ErrorType::class, TableTypeMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableTypeMap): Pair<ErrorType?, TableTypeMap?> {
        val r = InvokeAdhocReq(
            inputs = InvocationInputs(),
            code = CodeType(arg1.getString("code"))
        )

        val res = templateSupplier()
            .post("/v1/adhoc")
            .body(r)
            .execute(ApiSubmittedReqWithId::class)

        return null to ctx.tableCreateMap(2).also {
            it["req_id"] = res.reqId
            it["status"] = res.status.name
            it["id"] = res.id
        }
    }
}