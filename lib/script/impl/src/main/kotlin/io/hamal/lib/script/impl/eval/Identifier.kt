package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.FuncContext
import io.hamal.lib.script.api.value.IdentValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.expr.IdentifierLiteral

internal class EvaluateIdentifier :
    Evaluate<IdentifierLiteral> {
    override fun invoke(ctx: EvaluationContext<IdentifierLiteral>): Value {
        return IdentValue(ctx.toEvaluate.value)
    }
}
