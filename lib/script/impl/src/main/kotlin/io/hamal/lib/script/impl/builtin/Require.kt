package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.native_.FunctionValue
import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.MetaTableNotImplementedYet
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ScriptEvaluationException

internal object RequireFunction : FunctionValue {
    override val identifier: Identifier = Identifier("require")
    override val metaTable = MetaTableNotImplementedYet


    override fun invoke(ctx: FunctionValue.Context): Value {
        val firstParameter = ctx.parameters.firstOrNull()
            ?: throw ScriptEvaluationException(ErrorValue("require needs one environment identifier"))

        val identifier = firstParameter.asIdentifier()

        var result: Environment = ctx.env
        val splits = identifier.value.split("/")
        splits.forEach { envIdent ->
            result = result.findEnvironment(Identifier(envIdent))
                ?: throw ScriptEvaluationException(ErrorValue("Environment '${identifier.value}' not found"))
        }


        return result

    }
}