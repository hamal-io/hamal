package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.value.*
import io.hamal.script.api.value.*
import io.hamal.lib.script.impl.value.*

internal object AssertFunction : BuiltinFunction {
    override fun invoke(ctx: BuiltinFunction.Context): Value {
        val parameters = ctx.parameters

        val assertionMessage = parameters.getOrNull(1)
            ?.value
            ?.let { it as StringValue }
            ?: StringValue("${parameters.first().expression}")

        val result = parameters.firstOrNull()?.value
        if (result != TrueValue) {
            if (result != FalseValue) {
                throw io.hamal.lib.script.impl.ScriptEvaluationException(ErrorValue("Assertion of non boolean value is always false"))
            }
            throw io.hamal.lib.script.impl.ScriptEvaluationException(ErrorValue("Assertion violated: $assertionMessage"))
        }
        return NilValue
    }
}