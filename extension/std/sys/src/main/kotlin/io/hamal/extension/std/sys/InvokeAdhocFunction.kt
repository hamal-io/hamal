package io.hamal.extension.std.sys

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.SubmittedInvokeExecReq
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.body
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.sdk.HttpTemplateSupplier

class InvokeAdhocFunction(
    private val templateSupplier: HttpTemplateSupplier
) : Function1In2Out<TableMapValue, ErrorValue, TableMapValue>(
    FunctionInput1Schema(TableMapValue::class),
    FunctionOutput2Schema(ErrorValue::class, TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableMapValue): Pair<ErrorValue?, TableMapValue?> {
        val r = InvokeAdhocReq(
            inputs = InvocationInputs(),
            code = CodeValue(arg1.getString("code"))
        )

        val res = templateSupplier()
            .post("/v1/adhoc")
            .body(r)
            .execute(SubmittedInvokeExecReq::class)

        return null to ctx.tableCreateMap(2).also {
            it["req_id"] = res.reqId.value.value.toString()
            it["status"] = res.status.name
            it["id"] = res.id.value.value.toString()
        }
    }
}