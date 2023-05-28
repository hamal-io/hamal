package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ScriptEvaluationException

object RequireFunction : DepFunctionValue {
    override val identifier: DepIdentifier = DepIdentifier("require")
    override val metaTable = DepMetaTableNotImplementedYet


    override fun invoke(ctx: Context): DepValue {
        val firstParameter = ctx.parameters.firstOrNull()
            ?: throw ScriptEvaluationException(DepErrorValue("require needs one environment identifier"))

        val identifier = firstParameter.asIdentifier()

        var result: DepEnvironmentValue = ctx.env
        val splits = identifier.value.split("/")
        splits.forEach { envIdent ->
            result = result.findEnvironmentValue(DepIdentifier(envIdent))
                ?: throw ScriptEvaluationException(DepErrorValue("Environment '${identifier.value}' not found"))
        }


        return result

    }
}