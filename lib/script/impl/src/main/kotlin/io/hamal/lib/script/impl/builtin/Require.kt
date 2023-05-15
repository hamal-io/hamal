package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ScriptEvaluationException

internal object RequireFunction : NativeFunction {
    override val identifier: Identifier = Identifier("require")

    override fun invoke(ctx: NativeFunction.Context): Value {
        val identifier = ctx.parameters.firstOrNull()
            ?: throw ScriptEvaluationException(ErrorValue("require needs one environment identifier"))

        return ctx.env.findEnvironment(Identifier("eth")) ?: NilValue
    }
}