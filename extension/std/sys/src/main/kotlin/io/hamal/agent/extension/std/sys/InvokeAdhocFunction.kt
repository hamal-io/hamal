package io.hamal.agent.extension.std.sys

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.body
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.sdk.HttpTemplateSupplier

class InvokeAdhocFunction(
    private val templateSupplier: HttpTemplateSupplier
) : Function1In1Out<TableMapValue, StringValue>(
    FunctionInput1Schema(TableMapValue::class),
    FunctionOutput1Schema(StringValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableMapValue): StringValue {
        println(arg1.getString("code"))

        val r = InvokeAdhocReq(
            inputs = InvocationInputs(),
            code = CodeValue(arg1.getString("code"))
        )
        val res = templateSupplier()
            .post("/v1/adhoc")
            .body(r)
            .execute(SubmittedInvokeAdhocReq::class)

//        sleep(500)

        println("Invoke code")

        return StringValue(res.execId.value.toString())
    }
}