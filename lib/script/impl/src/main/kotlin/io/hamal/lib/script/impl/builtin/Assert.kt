package io.hamal.lib.script.impl.builtin

import io.hamal.lib.kua.value.*
import io.hamal.lib.script.impl.ScriptEvaluationException

object AssertFunction : FuncValue() {

    override fun invoke(ctx: FuncContext): Value {
        val params = ctx.params

        val line = params.getOrNull(0)?.expression?.position?.line ?: 1

        val assertionMessage = params.getOrNull(1)
            ?.value
            ?.let { (it as StringValue).value }
            ?: "${params.first().expression}"

        val result = params.firstOrNull()?.value
        if (result != TrueValue) {
            if (result != FalseValue) {
                throw ScriptEvaluationException(ErrorValue("Line $line: Assertion of non boolean value is always false"))
            }
            throw ScriptEvaluationException(ErrorValue("Line $line: Assertion violated: '$assertionMessage'"))
        }
        return NilValue
    }

}