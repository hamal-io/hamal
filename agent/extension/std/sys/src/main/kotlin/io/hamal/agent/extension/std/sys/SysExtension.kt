package io.hamal.agent.extension.std.sys

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.FuncSecrets
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.value.*
import io.hamal.lib.sdk.domain.ApiCreateFuncRequest

class HamalExtension : Extension {
    override fun create(): DepEnvironmentValue {
        return DepEnvironmentValue(
            identifier = DepIdentifier("sys"),
            values = mapOf(
                DepIdentifier("_cfg") to DepTableValue(),
                DepIdentifier("exec") to ExecFunc(),
                DepIdentifier("create_func") to CreateFunc(),
            )
        )
    }

}

class ExecFunc : DepFunctionValue {
    override val identifier = DepIdentifier("exec")
    override val metaTable: DepMetaTable get() = TODO("Not yet implemented")

    override fun invoke(ctx: Context): DepValue {
        val funcId = (ctx.parameters.first().value as DepStringValue).toString().replace("'", "")
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


        return DepNilValue
    }

}

class CreateFunc : DepFunctionValue {
    override val identifier = DepIdentifier("create_func")
    override val metaTable: DepMetaTable get() = TODO("Not yet implemented")

    override fun invoke(ctx: Context): DepValue {
//        val funcId = (ctx.parameters.first().value as DepStringValue).toString().replace("'", "")
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

            val f = ctx.parameters.first().value as DepTableValue
            println(f)

            val r = ApiCreateFuncRequest(
                name = FuncName((f[DepIdentifier("name")] as DepStringValue).value),
                inputs = FuncInputs(listOf()),
                secrets = FuncSecrets(listOf()),
                code = Code("")
            )

            val res = HttpTemplate("http://localhost:8084")
                .post("/v1/funcs")
                .body(r)
                .execute()

            println(res)

            return DepNilValue
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }

}