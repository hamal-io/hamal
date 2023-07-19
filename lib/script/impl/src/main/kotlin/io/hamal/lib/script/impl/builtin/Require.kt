package io.hamal.lib.script.impl.builtin

import io.hamal.lib.kua.value.*
import io.hamal.lib.script.impl.ScriptEvaluationException

object RequireFunction : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        val firstParameter = ctx.params.firstOrNull()
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