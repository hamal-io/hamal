package io.hamal.agent.extension.std

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.value.*

class HamalExtension : Extension {
    override fun create(): EnvironmentValue {
        return EnvironmentValue(
            identifier = Identifier("hamal"),
            values = mapOf(
                Identifier("_cfg") to TableValue(),
                Identifier("exec") to ExecFunc(),
            )
        )
    }

}

class ExecFunc : FunctionValue {
    override val identifier = Identifier("exec")
    override val metaTable: MetaTable get() = TODO("Not yet implemented")

    override fun invoke(ctx: Context): Value {
        val funcId = (ctx.parameters.first().value as StringValue).toString().replace("'", "")
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