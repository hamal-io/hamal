package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.ast.Identifier
import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ScriptEvaluationException
import io.hamal.lib.script.impl.ast.expr.IdentifierExpression

internal object RequireFunction : NativeFunction {
    override val identifier: Identifier = IdentifierExpression("require")

    override fun invoke(ctx: NativeFunction.Context): Value {
        val module = ctx.parameters.firstOrNull()
            ?: throw ScriptEvaluationException(ErrorValue("require has to be called with a module"))

        return NilValue
    }
}