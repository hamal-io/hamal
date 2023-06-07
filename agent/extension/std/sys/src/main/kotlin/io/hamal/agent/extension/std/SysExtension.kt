package io.hamal.agent.extension.std

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.value.*

class HamalExtension : Extension {
    override fun create(): DepEnvironmentValue {
        return DepEnvironmentValue(
            identifier = DepIdentifier("sys"),
            values = mapOf(
                DepIdentifier("_cfg") to DepTableValue(),
                DepIdentifier("exec") to ExecFunc(),
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