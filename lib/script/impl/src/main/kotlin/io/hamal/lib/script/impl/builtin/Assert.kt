package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ScriptEvaluationException

object AssertFunction : FuncValue() {

    override fun invoke(ctx: FuncContext): Value {
        val params = ctx.params

        val assertionMessage = params.getOrNull(1)
            ?.value
            ?.let { (it as StringValue).value }
            ?: "${params.first().expression}"

        val result = params.firstOrNull()?.value
        if (result != TrueValue) {
            if (result != FalseValue) {
                throw ScriptEvaluationException(ErrorValue("Assertion of non boolean value is always false"))
            }
            throw ScriptEvaluationException(ErrorValue("Assertion violated: '$assertionMessage'"))
        }
        return NilValue
    }

}