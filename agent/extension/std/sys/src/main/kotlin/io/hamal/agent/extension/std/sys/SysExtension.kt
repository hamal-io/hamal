package io.hamal.agent.extension.std.sys

import io.hamal.agent.extension.api.Extension
import io.hamal.agent.extension.api.ExtensionFunc
import io.hamal.agent.extension.api.ExtensionFuncInvocationContext
import io.hamal.lib.domain.req.AdhocInvocationReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.script.api.value.*
import io.hamal.lib.sdk.domain.ApiCreateFuncRequest

class HamalExtension : Extension {
    override fun create(): EnvValue {
        return EnvValue(
            ident = IdentValue("sys"),
            values = mapOf(
                IdentValue("_cfg") to TableValue(),
                IdentValue("exec") to ExecFunc(),
                IdentValue("create_func") to CreateFunc(),
                IdentValue("adhoc") to Adhoc()
            )
        )
    }
}

class Adhoc : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        try {
            println("ADHOC")

            val f = ctx.parameters.first() as TableValue
            println(f)

            val r = AdhocInvocationReq(
                inputs = InvocationInputs(TableValue()),
                secrets = InvocationSecrets(listOf()),
                code = Code((f[IdentValue("run")] as CodeValue).value)
            )

            val res = HttpTemplate("http://localhost:8084")
                .post("/v1/adhoc")
                .body(r)
                .execute()

            println(res)

            return NilValue
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }

}

class ExecFunc : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        val funcId = (ctx.parameters.first() as StringValue).toString().replace("'", "")
        println("DEBUG: ${funcId}")

        HttpTemplate("http://localhost:8084")
            .post("/v1/funcs/${funcId}/exec")
            .body(
                """
                {
                }
            """.trimIndent()
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

        try {
            println("CREATE_FUNC")

            val f = ctx.parameters.first() as TableValue
            println(f)

            val r = ApiCreateFuncRequest(
                name = FuncName((f[IdentValue("name")] as StringValue).value),
                inputs = FuncInputs(TableValue()),
                secrets = FuncSecrets(listOf()),
                code = Code((f[IdentValue("run")] as CodeValue).value)
            )

            val res = HttpTemplate("http://localhost:8084")
                .post("/v1/funcs")
                .body(r)
                .execute()

            println(res)

            return NilValue
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }
}