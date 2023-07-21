package io.hamal.agent.extension.std.sys

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.body
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.Function1In1Out
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.TableValue
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.sdk.HttpTemplateSupplier

class InvokeAdhocFunction(
    private val templateSupplier: HttpTemplateSupplier
) : Function1In1Out<TableValue, StringValue>(
    FunctionInput1Schema(TableValue::class),
    FunctionOutput1Schema(StringValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableValue): StringValue {
//            val f = ctx.params.first().value as TableValue
//
//            val inputs = when (val x = f["inputs"]) {
//                is NilValue -> TableValue()
//                is TableValue -> x
//                else -> TODO()
//            }
//
//            val code = when (val x = f["code"]) {
//                is NilValue -> CodeValue("")
//                is CodeValue -> x
//                is StringValue -> CodeValue(x)
//                else -> TODO()
//            }
//
        val r = InvokeAdhocReq(
            inputs = InvocationInputs(),
            code = CodeValue("print('hello from invoke')")
        )
//
        val res = templateSupplier()
            .post("/v1/adhoc")
            .body(r)
            .execute(SubmittedInvokeAdhocReq::class)

//            sleep(500)

        println("Invoke code")

        return StringValue(res.execId.toString())
    }
}