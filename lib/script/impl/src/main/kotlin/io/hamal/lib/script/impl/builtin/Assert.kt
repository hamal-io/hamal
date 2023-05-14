package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.natives.NativeFunction
import io.hamal.lib.script.api.ast.Identifier
import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ScriptEvaluationException
import io.hamal.lib.script.impl.ast.expr.IdentifierExpression

internal object AssertFunction : NativeFunction {
    override val identifier: Identifier = IdentifierExpression("assert")

    override fun invoke(ctx: NativeFunction.Context): Value {
        val parameters = ctx.parameters

        val assertionMessage = parameters.getOrNull(1)
            ?.value
            ?.let { it as StringValue }
            ?: StringValue("${parameters.first().expression}")

        val result = parameters.firstOrNull()?.value
        if (result != TrueValue) {
            if (result != FalseValue) {
                throw ScriptEvaluationException(ErrorValue("Assertion of non boolean value is always false"))
            }
            throw ScriptEvaluationException(ErrorValue("Assertion violated: $assertionMessage"))
        }
        return NilValue
    }
}