package io.hamal.extension.std.sys

import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.req.SubmittedCreateFuncReq
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.ErrorValue

class CreateFuncFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In2Out<TableMapValue, ErrorValue, TableMapValue>(
    FunctionInput1Schema(TableMapValue::class),
    FunctionOutput2Schema(ErrorValue::class, TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableMapValue): Pair<ErrorValue?, TableMapValue> {
        try {

//            val name: StringValue = when (val x = arg1["name"]) {
//                is NilValue -> StringValue("")
//                is StringValue -> x
//                is IdentValue -> ctx.env[x] as StringValue
//                else -> TODO()
//            }
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

            val r = CreateFuncReq(
                name = FuncName(arg1.getString("name")),
                inputs = FuncInputs(),
                code = arg1.getCodeValue("code")
            )

            val res = templateSupplier()
                .post("/v1/funcs")
                .body(r)
                .execute(SubmittedCreateFuncReq::class)

            return null to ctx.tableCreateMap(1).also {
                it["id"] = res.id.value.toString()
                it["status"] = res.status.name
                it["func_id"] = res.funcId.value.toString()
            }

        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }
}