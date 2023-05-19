package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ScriptEvaluationException

object AssertFunction : FunctionValue {
    override val identifier: Identifier = Identifier("assert")
    override val metaTable = MetaTableNotImplementedYet


    override fun invoke(ctx: Context): Value {
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