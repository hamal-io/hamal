package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.MetaTableNotImplementedYet
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ScriptEvaluationException

internal object RequireFunction : NativeFunction {
    override val identifier: Identifier = Identifier("require")
    override val metaTable = MetaTableNotImplementedYet


    override fun invoke(ctx: NativeFunction.Context): Value {
        val firstParameter = ctx.parameters.firstOrNull()
            ?: throw ScriptEvaluationException(ErrorValue("require needs one environment identifier"))

        val identifier = firstParameter.asIdentifier()
        return ctx.env.findEnvironment(identifier)
            ?: throw ScriptEvaluationException(ErrorValue("Environment '${identifier.value}' not found"))
    }
}