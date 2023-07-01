package io.hamal.agent.extension.std.sys

import io.hamal.agent.extension.api.Extension
import io.hamal.agent.extension.api.ExtensionFunc
import io.hamal.agent.extension.api.ExtensionFuncInvocationContext
import io.hamal.lib.domain.req.InvokeOneshotReq
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
                            IdentValue("list") to ListFuncs()
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
                NumberValue(idx + 1) to TableValue(
                    StringValue("id") to NumberValue(func.id.value),
                    StringValue("name") to StringValue(func.name.value)
                )
            }.toMap<Value, Value>()

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
//        val funcId = (ctx.parameters.first().value as StringValue).toString().replace("'", "")
//        println("DEBUG: ${funcId}")
//
//        HttpTemplate("http://localhost:8084")
//            .post("/v1/funcs/${funcId}/exec")
//            .body(
//                """
//                {
//                }
//            """.trimIndent()
//            )
//            .execute()

//        try {
//            println("CREATE_FUNC")
//
//            val f = ctx.parameters.first() as TableValue
//            println(f)
//
//            val r = ApiCreateFuncRequest(
//                name = FuncName((f[IdentValue("name")] as StringValue).value),
//                inputs = FuncInputs(TableValue()),
//                secrets = FuncSecrets(listOf()),
//                code = Code((f[IdentValue("run")] as CodeValue).value)
//            )
//
//            val res = HttpTemplate("http://localhost:8084")
//                .post("/v1/funcs")
//                .body(r)
//                .execute()
//
//            println(res)
//
//            return NilValue
//        } catch (t: Throwable) {
//            t.printStackTrace()
//            throw t
//        }
        TODO()
    }
}