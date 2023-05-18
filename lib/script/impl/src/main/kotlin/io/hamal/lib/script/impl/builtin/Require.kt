package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ScriptEvaluationException

object RequireFunction : FunctionValue {
    override val identifier: Identifier = Identifier("require")
    override val metaTable = MetaTableNotImplementedYet


    override fun invoke(ctx: FunctionValue.Context): Value {
        val firstParameter = ctx.parameters.firstOrNull()
            ?: throw ScriptEvaluationException(ErrorValue("require needs one environment identifier"))

        val identifier = firstParameter.asIdentifier()

        var result: EnvironmentValue = ctx.env
        val splits = identifier.value.split("/")
        splits.forEach { envIdent ->
            result = result.findEnvironmentValue(Identifier(envIdent))
                ?: throw ScriptEvaluationException(ErrorValue("Environment '${identifier.value}' not found"))
        }


        return result

    }
}