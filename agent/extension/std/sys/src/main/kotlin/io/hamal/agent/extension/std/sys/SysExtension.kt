package io.hamal.agent.extension.std.sys

import io.hamal.agent.extension.api.Extension
import io.hamal.agent.extension.api.ExtensionFunc
import io.hamal.agent.extension.api.ExtensionFuncInvocationContext
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.req.InvokeOneshotReq
import io.hamal.lib.domain.req.SubmittedCreateFuncReq
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.script.api.value.*
import io.hamal.lib.sdk.domain.ListFuncsResponse

class SysExtension : Extension {
    override fun create(): EnvValue {
        return EnvValue(
            ident = IdentValue("sys"),
            values = mapOf(
                IdentValue("_cfg") to TableValue(),
                IdentValue("invoke") to InvokeFunc(),
                IdentValue("create_func") to CreateFunc(),
                IdentValue("list_funcs") to ListFuncs(),
                IdentValue("func") to
                        TableValue(
                            "list" to ListFuncs(),
                            "create" to CreateFunc()
                        )
            )
        )
    }


}


class ListFuncs : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        println("ListFuncs")

        val response = HttpTemplate("http://localhost:8084")
            .get("/v1/funcs")
            .execute(ListFuncsResponse::class)
            .funcs
            .mapIndexed { idx, func ->
                IdentValue((idx + 1).toString()) to TableValue(
                    "id" to StringValue(func.id.value.toString()),
                    "name" to StringValue(func.name.value)
                )
            }.toMap<IdentValue, Value>()

        return TableValue(response)
    }
}


class InvokeFunc : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        val funcId = (ctx.parameters.first() as StringValue).toString().replace("'", "")
        println("DEBUG: ${funcId}")

        HttpTemplate("http://localhost:8084")
            .post("/v1/funcs/${funcId}/exec")
            .body(
                InvokeOneshotReq(
                    inputs = InvocationInputs(),
                    correlationId = null
                )
            )
            .execute()
        return NilValue
    }

}


class CreateFunc : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        try {
            println("CREATE_FUNC")

            val f = ctx.parameters.first() as TableValue
            println(f)

            val r = CreateFuncReq(
                name = FuncName((f[IdentValue("name")] as StringValue).value),
                inputs = FuncInputs(TableValue()),
                code = Code((f[IdentValue("run")] as CodeValue).value)
            )

            val res = HttpTemplate("http://localhost:8084")
                .post("/v1/funcs")
                .body(r)
                .execute(SubmittedCreateFuncReq::class)

            println(res)

            // FIXME await completion

            return StringValue(res.funcId.value.toString())
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
        TODO()
    }
}