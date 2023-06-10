package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.FuncInvocationContext
import io.hamal.lib.script.api.value.IdentValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.expr.IdentifierLiteral

internal class EvaluateIdentifier<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<IdentifierLiteral, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<IdentifierLiteral, INVOKE_CTX>): Value {
        return IdentValue(ctx.toEvaluate.value)
    }
}
