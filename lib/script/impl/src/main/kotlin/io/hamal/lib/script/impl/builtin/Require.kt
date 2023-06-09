package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ScriptEvaluationException

object RequireFunction : BuiltinFuncValue() {
    override fun invoke(ctx: Context): Value {
        val firstParameter = ctx.parameters.firstOrNull()
            ?: throw ScriptEvaluationException(ErrorValue("require needs one environment ident"))

        val ident = firstParameter.asIdentifier()

        var result: EnvValue = ctx.env
        val splits = ident.value.split("/")
        splits.forEach { envIdent ->
            result = result.findEnvironmentValue(IdentValue(envIdent))
                ?: throw ScriptEvaluationException(ErrorValue("Environment '${ident.value}' not found"))
        }

        return result
    }
}