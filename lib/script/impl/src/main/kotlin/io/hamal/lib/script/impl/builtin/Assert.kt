package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ScriptEvaluationException

object AssertFunction : DepFunctionValue {
    override val identifier: DepIdentifier = DepIdentifier("assert")
    override val metaTable = DepMetaTableNotImplementedYet


    override fun invoke(ctx: Context): DepValue {
        val parameters = ctx.parameters

        val assertionMessage = parameters.getOrNull(1)
            ?.value
            ?.let { it as DepStringValue }
            ?: DepStringValue("${parameters.first().expression}")

        val result = parameters.firstOrNull()?.value
        if (result != DepTrueValue) {
            if (result != DepFalseValue) {
                throw ScriptEvaluationException(DepErrorValue("Assertion of non boolean value is always false"))
            }
            throw ScriptEvaluationException(DepErrorValue("Assertion violated: $assertionMessage"))
        }
        return DepNilValue
    }
}